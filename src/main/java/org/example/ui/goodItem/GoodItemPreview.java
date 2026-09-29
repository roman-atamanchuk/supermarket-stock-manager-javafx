package org.example.ui.goodItem;

import javafx.scene.image.Image;
import org.example.data.GoodItem;
import org.example.ui.UIHelper;

public class GoodItemPreview {
    private final GoodItemControllerUI ui;
    private boolean wired = false;

    public GoodItemPreview(GoodItemControllerUI ui) {
        this.ui = ui;
    }

    public void setupRowSelection() {
        if (wired) return;
        wired = true;
        ui.getGoodsTable().getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> showPreview(newSel));
    }

    private void showPreview(GoodRow row) {
        if (row == null) return;

        GoodItem g = UIHelper.findNodeData(
                (ui.getShelf() != null && ui.getShelf().getGoods() != null)
                        ? ui.getShelf().getGoods().getHead() : null,
                item -> item.getName().equalsIgnoreCase(row.getName())
        );

        if (g == null) return;

        // --- IMAGE ---
        if (g.getPhotoUrl() != null && !g.getPhotoUrl().isBlank()) {
            ui.getPhotoPreview().setImage(new Image(g.getPhotoUrl(), 800, 600, false, true));
        } else {
            ui.getPhotoPreview().setImage(null);
        }

        // --- TITLE (top label) ---
        ui.getPhotoNameLabel().setText(
                g.getName() + " — €" + g.getUnitPrice() +
                        " × " + g.getQuantity() + " = €" + String.format("%.2f", g.getTotalValue())
        );

        // --- DESCRIPTION + FULL INFO ---
        StringBuilder info = new StringBuilder();

        info.append("📦 Description: ")
                .append((g.getDescription() != null && !g.getDescription().isBlank())
                        ? g.getDescription()
                        : "(no description)").append("\n\n");

        info.append("⚖️ Weight: ").append(g.getSizeWeight()).append(" g/ml\n");
        info.append("📊 Quantity: ").append(g.getQuantity()).append("\n");
        info.append("💶 Price per unit: €").append(String.format("%.2f", g.getUnitPrice())).append("\n");
        info.append("🌡 Temperature: ").append(g.getTemperature()).append("\n");

        // --- LOCATION ---
        var shelf = g.getParentShelf();
        var aisle = (shelf != null) ? shelf.getParentAisle() : null;
        var floor = (aisle != null) ? aisle.getParentArea() : null;

        info.append("\n📍 Location: ");
        if (floor != null)
            info.append("Floor ").append(floor.getTitle()).append(" → ");
        if (aisle != null)
            info.append("Aisle ").append(aisle.getName()).append(" → ");
        if (shelf != null)
            info.append("Shelf #").append(shelf.getNumber());

        // --- UPDATE RIGHT PANEL ---
        ui.getPhotoDescArea().setText(info.toString());
    }
}
