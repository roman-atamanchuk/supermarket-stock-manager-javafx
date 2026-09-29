package org.example.ui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.data.FloorArea;
import org.example.store.FloorAreaController;
import org.example.store.SupermarketController;
import org.example.util.DataSync;

public class DashboardController {

    @FXML private Label supermarketNameLabel;
    @FXML private TableView<FloorAreaRow> floorAreaTable;
    @FXML private TableColumn<FloorAreaRow, String> titleColumn;
    @FXML private TableColumn<FloorAreaRow, String> levelColumn;
    @FXML private TableColumn<FloorAreaRow, Integer> aisleCountColumn;
    @FXML private TableColumn<FloorAreaRow, Double> totalValueColumn;
    @FXML private TableColumn<FloorAreaRow, Integer> totalQuantityColumn;
    @FXML private Label totalValueLabel;
    @FXML private Label totalQuantityLabel;
    @FXML private TextField floorTitleField;
    @FXML private TextField floorLevelField;
    @FXML private TextField searchField;

    private SupermarketController supermarketController;

    // === TABLE ROW DATA MODEL ===
    public static class FloorAreaRow {
        private final SimpleStringProperty title;
        private final SimpleStringProperty level;
        private final SimpleIntegerProperty aisles;
        private final SimpleDoubleProperty totalValue;
        private final SimpleIntegerProperty totalQuantity;

        public FloorAreaRow(String title, String level, int aisles, double totalValue, int totalQuantity) {
            this.title = new SimpleStringProperty(title);
            this.level = new SimpleStringProperty(level);
            this.aisles = new SimpleIntegerProperty(aisles);
            this.totalValue = new SimpleDoubleProperty(totalValue);
            this.totalQuantity = new SimpleIntegerProperty(totalQuantity);
        }

        public String getTitle() { return title.get(); }
        public String getLevel() { return level.get(); }
        public int getAisles() { return aisles.get(); }
        public double getTotalValue() { return totalValue.get(); }
        public int getTotalQuantity() { return totalQuantity.get(); }
    }

    // === INITIALIZE DATA ===
    public void initData(SupermarketController controller) {
        this.supermarketController = controller;
        supermarketNameLabel.setText(controller.getSupermarket().getName());

        // Table column setup
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        levelColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLevel()));
        aisleCountColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAisles()).asObject());
        totalValueColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalValue()).asObject());
        totalQuantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalQuantity()).asObject());

        // Double-click to open Aisle Manager
        floorAreaTable.setRowFactory(tv -> {
            TableRow<FloorAreaRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openAisleWindow(row.getItem().getTitle());
                }
            });
            return row;
        });

        refreshFloorTable();
    }

    // === ADD FLOOR ===
    @FXML
    private void handleAddFloor() {
        String title = floorTitleField.getText().trim();
        String level = floorLevelField.getText().trim();

        if (!UIHelper.validateNotEmpty(title, level)) {
            UIHelper.showAlert("Please fill in both title and level.");
            return;
        }

        // ✅ Try to add floor — returns true if successful
        boolean added = supermarketController.addFloorArea(title, level);

        if (!added) {
            UIHelper.showAlert("A floor with the same title already exists or title is invalid.");
            return; // ❌ stop here if not added
        }

        // ✅ Success: refresh and save
        refreshFloorTable();
        UIHelper.clearFields(floorTitleField, floorLevelField);
        DataSync.save(supermarketController.getSupermarket());
    }

    // === REMOVE FLOOR ===
    @FXML
    private void handleRemoveFloor() {
        FloorAreaRow selected = floorAreaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIHelper.showAlert("Please select a floor area to remove.");
            return;
        }

        if (!UIHelper.confirm(
                "Confirm Removal",
                "Are you sure you want to remove '" + selected.getTitle() + "'?"
        )) return;

        supermarketController.removeFloorArea(selected.getTitle());
        refreshFloorTable();
        DataSync.save(supermarketController.getSupermarket());
    }

    // === SEARCH FLOOR ===
    @FXML
    private void handleSearchFloor() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            UIHelper.showAlert("Please enter a floor name to search.");
            return;
        }

        var results = supermarketController.searchFloorAreasProgressive(query);
        floorAreaTable.getItems().clear();

        if (results.isEmpty()) {
            UIHelper.showInfo("No floor areas found matching: " + query);
            updateSummary(0, 0);
            return;
        }

        double grandTotalValue = 0;
        int grandTotalQuantity = 0;

        var node = results.getHead();
        while (node != null) {
            var f = node.getData();
            double value = f.getTotalValue();
            int qty = f.getTotalQuantity();

            grandTotalValue += value;
            grandTotalQuantity += qty;

            floorAreaTable.getItems().add(new FloorAreaRow(
                    f.getTitle(),
                    f.getLevel(),
                    f.getAisles().size(),
                    value,
                    qty
            ));
            node = node.getNext();
        }

        updateSummary(grandTotalValue, grandTotalQuantity);
    }

    // === CLEAR SEARCH ===
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        refreshFloorTable();
    }

    // === MANUAL SAVE ===
    @FXML
    private void handleManualSave() {
        try {
            DataSync.save(supermarketController.getSupermarket());
            UIHelper.showInfo("✅ Supermarket data saved to Google Sheets!");
        } catch (Exception e) {
            UIHelper.showError("❌ Failed to save: " + e.getMessage());
        }
    }

    // === REFRESH TABLE ===
    private void refreshFloorTable() {
        floorAreaTable.getItems().clear();
        var floors = supermarketController.getSupermarket().getFloorAreas();

        double grandTotalValue = 0;
        int grandTotalQuantity = 0;

        var node = floors.getHead();
        while (node != null) {
            var f = node.getData();
            double floorValue = f.getTotalValue();
            int floorQty = f.getTotalQuantity();

            grandTotalValue += floorValue;
            grandTotalQuantity += floorQty;

            floorAreaTable.getItems().add(new FloorAreaRow(
                    f.getTitle(),
                    f.getLevel(),
                    f.getAisles().size(),
                    floorValue,
                    floorQty
            ));
            node = node.getNext();
        }

        updateSummary(grandTotalValue, grandTotalQuantity);
    }

    // === UPDATE SUMMARY LABELS ===
    private void updateSummary(double totalValue, int totalQuantity) {
        totalValueLabel.setText(String.format("€%.2f", totalValue));
        totalQuantityLabel.setText(String.valueOf(totalQuantity));
    }

    // === OPEN AISLE MANAGER ===
    private void openAisleWindow(String floorTitle) {
        var floors = supermarketController.getSupermarket().getFloorAreas();
        FloorArea found = UIHelper.findNodeData(floors.getHead(), f -> f.getTitle().equals(floorTitle));

        if (found == null) {
            UIHelper.showError("Floor not found: " + floorTitle);
            return;
        }

        try {
            FloorAreaController floorController = new FloorAreaController(found);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ui/AisleView.fxml"));
            Parent root = loader.load();

            AisleControllerUI aisleController = loader.getController();
            aisleController.initData(floorController);

            Stage stage = new Stage();
            stage.setTitle("Manage Aisles — " + found.getTitle());
            stage.setScene(new Scene(root, 700, 400));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            UIHelper.showError("Failed to open Aisle Manager window: " + e.getMessage());
        }
    }
}