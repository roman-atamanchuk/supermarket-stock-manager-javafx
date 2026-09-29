package org.example.ui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.data.Aisle;
import org.example.data.Shelf;
import org.example.ui.goodItem.GoodItemControllerUI;
import org.example.util.DataSync;

import java.io.IOException;

public class ShelfControllerUI {

    @FXML private Label aisleNameLabel;
    @FXML private TableView<ShelfRow> shelfTable;
    @FXML private TableColumn<ShelfRow, Integer> numberColumn;
    @FXML private TableColumn<ShelfRow, Integer> quantityColumn;
    @FXML private TableColumn<ShelfRow, Double> valueColumn;
    @FXML private Label totalShelvesLabel;
    @FXML private Label totalGoodsLabel;
    @FXML private Label totalValueLabel;
    @FXML private TextField shelfNumberField;

    private Aisle aisle;

    // --- Table Row Model ---
    public static class ShelfRow {
        private final SimpleIntegerProperty number;
        private final SimpleIntegerProperty quantity;
        private final SimpleDoubleProperty value;

        public ShelfRow(int number, int quantity, double value) {
            this.number = new SimpleIntegerProperty(number);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.value = new SimpleDoubleProperty(value);
        }

        public int getNumber() { return number.get(); }
        public int getQuantity() { return quantity.get(); }
        public double getValue() { return value.get(); }
    }

    // --- Initialize controller ---
    public void initData(Aisle aisle) {
        this.aisle = aisle;
        aisleNameLabel.setText(aisle.getName());

        numberColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getNumber()).asObject());
        quantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        valueColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getValue()).asObject());

        refreshShelfTable();

        // 🖱️ Double-click to open Goods window
        shelfTable.setRowFactory(tv -> {
            TableRow<ShelfRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    openGoodsWindow(row.getItem().getNumber());
                }
            });
            return row;
        });
    }

    // --- Refresh table and totals (FIXED VERSION) ---
    private void refreshShelfTable() {
        shelfTable.getItems().clear();

        int totalShelves = 0;
        int totalGoods = 0;
        double totalValue = 0.0;

        var node = aisle.getShelves().getHead();
        while (node != null) {
            Shelf shelf = node.getData();
            int qty = shelf.getTotalQuantity();
            double val = shelf.getTotalValue();

            shelfTable.getItems().add(new ShelfRow(shelf.getNumber(), qty, val));

            totalShelves++;
            totalGoods += qty;
            totalValue += val;

            node = node.getNext();
        }

        totalShelvesLabel.setText(String.valueOf(totalShelves));
        totalGoodsLabel.setText(String.valueOf(totalGoods));
        totalValueLabel.setText(String.format("€%.2f", totalValue));
    }

    // --- Add new shelf ---
    @FXML
    private void handleAddShelf() {
        String text = shelfNumberField.getText().trim();

        if (!UIHelper.validateNotEmpty(text)) {
            UIHelper.showAlert("Please enter a shelf number.");
            return;
        }

        Integer number = UIHelper.tryParseInt(text, "Shelf number");
        if (number == null) return;

        Shelf newShelf = new Shelf(number);
        aisle.addShelf(newShelf);

        refreshShelfTable();
        UIHelper.clearFields(shelfNumberField);

        try {
            DataSync.save(aisle.getParentArea().getParentSupermarket());
            System.out.println("✅ Added shelf #" + number + " to aisle: " + aisle.getName());
        } catch (Exception e) {
            System.err.println("⚠️ Auto-save failed: " + e.getMessage());
        }
    }

    // --- Remove selected shelf ---
    @FXML
    private void handleRemoveShelf() {
        ShelfRow selected = shelfTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIHelper.showAlert("Please select a shelf to remove.");
            return;
        }

        int shelfNumber = selected.getNumber();

        if (!UIHelper.confirm("Confirm Removal",
                "Are you sure you want to remove shelf #" + shelfNumber + "?")) return;

        Shelf target = UIHelper.findNodeData(aisle.getShelves().getHead(),
                s -> s.getNumber() == shelfNumber);

        if (target != null && aisle.getShelves().remove(target)) {
            refreshShelfTable();

            try {
                DataSync.save(aisle.getParentArea().getParentSupermarket());
                System.out.println("💾 Auto-saved after removing shelf #" + shelfNumber);
            } catch (Exception e) {
                System.err.println("⚠️ Auto-save failed: " + e.getMessage());
            }

        } else {
            UIHelper.showError("Shelf not found or could not be removed.");
        }
    }

    // --- Open Goods window (double-click) ---
    private void openGoodsWindow(int shelfNumber) {
        Shelf selectedShelf = UIHelper.findNodeData(aisle.getShelves().getHead(),
                s -> s.getNumber() == shelfNumber);

        if (selectedShelf == null) {
            UIHelper.showError("Shelf not found.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ui/gooditem-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            GoodItemControllerUI controller = loader.getController();
            controller.initData(aisle, selectedShelf);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Goods — " + aisle.getName() + " / Shelf #" + shelfNumber);
            stage.show();

        } catch (IOException e) {
            UIHelper.showError("Failed to open goods window: " + e.getMessage());
        }
    }
}