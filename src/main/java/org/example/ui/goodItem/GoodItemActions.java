package org.example.ui.goodItem;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import org.example.data.GoodItem;
import org.example.data.Shelf;
import org.example.data.Temperature;
import org.example.ui.UIHelper;
import org.example.util.DataSync;

import java.util.Optional;

public class GoodItemActions {

    private final GoodItemControllerUI ui;

    public GoodItemActions(GoodItemControllerUI ui) {
        this.ui = ui;
    }

    // =====================================================================
    // ✅ ADD ITEM
    // =====================================================================
    @FXML
    public void handleAddGood() {
        String name = ui.getNameField().getText().trim();
        String desc = ui.getDescField().getText().trim();
        String qtyText = ui.getQtyField().getText().trim();
        String priceText = ui.getPriceField().getText().trim();
        String weightText = ui.getWeightField().getText().trim();
        String photo = ui.getPhotoField().getText().trim();
        Temperature temp = ui.getTempCombo().getValue();

        if (!UIHelper.validateNotEmpty(name, desc, qtyText, priceText, weightText)) {
            UIHelper.showAlert("Please fill all fields before adding.");
            return;
        }

        Integer qty = UIHelper.tryParseInt(qtyText, "Quantity");
        Double price = UIHelper.tryParseDouble(priceText, "Price");
        Double weight = UIHelper.tryParseDouble(weightText, "Weight");
        if (qty == null || price == null || weight == null)
            return;

        GoodItem newItem = new GoodItem(name, desc, weight, price, qty, temp, photo);
        newItem.setParentShelf(ui.getShelf());

        GoodItem existing = UIHelper.findNodeData(
                ui.getShelf().getGoods().getHead(),
                g -> g.equals(newItem)
        );

        if (existing != null) {
            existing.merge(newItem);
        } else {
            ui.getShelf().getGoods().add(newItem);
        }

        ui.refreshGoodsTable();
        UIHelper.clearFields(
                ui.getNameField(), ui.getDescField(), ui.getQtyField(),
                ui.getPriceField(), ui.getWeightField(), ui.getPhotoField()
        );
        ui.getTempCombo().getSelectionModel().select(Temperature.UNREFRIGERATED);

        save();
    }

    // =====================================================================
    // ❌ REMOVE ITEM (with quantity input)
    // =====================================================================
    @FXML
    public void handleRemoveGood() {

        GoodRow selected = ui.getGoodsTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIHelper.showAlert("Please select an item to remove.");
            return;
        }

        // Use the exact item behind the selected row, so two sizes of the same
        // product can't be mixed up. It may be on another shelf (search results).
        GoodItem existing = selected.getSource();
        Shelf itemShelf = existing != null ? existing.getParentShelf() : null;
        if (itemShelf == null || !itemShelf.getGoods().contains(existing)) {
            existing = null;
        }

        if (existing == null) {
            UIHelper.showError("Item not found.");
            return;
        }

        // =====================================================================
        // 🔢 ASK HOW MANY TO REMOVE  (FIXED: dialog owner!)
        // =====================================================================
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(ui.getGoodsTable().getScene().getWindow());   // <<< FIX HERE
        dialog.setTitle("Remove Quantity");
        dialog.setHeaderText("How many units to remove?");
        dialog.setContentText("Enter quantity (Available: " + existing.getQuantity() + "):");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        Integer qtyToRemove = UIHelper.tryParseInt(result.get(), "Quantity");
        if (qtyToRemove == null) return;

        if (qtyToRemove <= 0) {
            UIHelper.showError("Quantity must be greater than zero.");
            return;
        }

        if (qtyToRemove > existing.getQuantity()) {
            UIHelper.showError("Cannot remove more than available (" + existing.getQuantity() + ").");
            return;
        }

        // =====================================================================
        // 🔄 REMOVE or DECREASE
        // =====================================================================
        if (qtyToRemove < existing.getQuantity()) {
            existing.setQuantity(existing.getQuantity() - qtyToRemove);
        } else {
            itemShelf.getGoods().remove(existing);
        }

        // =====================================================================
        // 🔄 REFRESH UI
        // =====================================================================
        ui.refreshGoodsTable();
        ui.getPhotoPreview().setImage(null);
        ui.getPhotoNameLabel().setText("Select an item to preview");
        ui.getPhotoDescArea().clear();

        save();
    }

    // =====================================================================
    // 💾 AUTO SAVE
    // =====================================================================
    private void save() {
        try {
            DataSync.save(ui.getAisle().getParentArea().getParentSupermarket());
            System.out.println("💾 Auto-saved supermarket after update");
        } catch (Exception e) {
            System.err.println("⚠️ Auto-save failed: " + e.getMessage());
        }
    }
}