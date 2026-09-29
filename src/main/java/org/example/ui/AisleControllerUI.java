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
import org.example.data.Aisle;
import org.example.data.Temperature;
import org.example.store.FloorAreaController;
import org.example.util.DataSync;

public class AisleControllerUI {

    @FXML private Label floorNameLabel;
    @FXML private TableView<AisleRow> aisleTable;
    @FXML private TableColumn<AisleRow, String> nameColumn;
    @FXML private TableColumn<AisleRow, String> tempColumn;
    @FXML private TableColumn<AisleRow, Double> lengthColumn;
    @FXML private TableColumn<AisleRow, Double> widthColumn;
    @FXML private TableColumn<AisleRow, Integer> shelfCounterColumn;
    @FXML private TableColumn<AisleRow, Double> totalValueColumn;
    @FXML private TableColumn<AisleRow, Integer> totalQuantityColumn;

    @FXML private Label totalAisleLabel;
    @FXML private Label totalShelfLabel;
    @FXML private Label totalValueLabel;
    @FXML private Label totalQuantityLabel;

    @FXML private TextField aisleNameField;
    @FXML private ChoiceBox<Temperature> temperatureChoiceBox;
    @FXML private TextField lengthField;
    @FXML private TextField widthField;

    private FloorAreaController floorAreaController;

    // --- Table row model ---
    public static class AisleRow {
        private final SimpleStringProperty name;
        private final SimpleStringProperty temp;
        private final SimpleDoubleProperty length;
        private final SimpleDoubleProperty width;
        private final SimpleIntegerProperty shelfCount;
        private final SimpleDoubleProperty totalValue;
        private final SimpleIntegerProperty totalQuantity;

        public AisleRow(String name, String temp, double length, double width,
                        int shelfCount, double totalValue, int totalQuantity) {
            this.name = new SimpleStringProperty(name);
            this.temp = new SimpleStringProperty(temp);
            this.length = new SimpleDoubleProperty(length);
            this.width = new SimpleDoubleProperty(width);
            this.shelfCount = new SimpleIntegerProperty(shelfCount);
            this.totalValue = new SimpleDoubleProperty(totalValue);
            this.totalQuantity = new SimpleIntegerProperty(totalQuantity);
        }

        public String getName() { return name.get(); }
        public String getTemp() { return temp.get(); }
        public double getLength() { return length.get(); }
        public double getWidth() { return width.get(); }
        public int getShelfCount() { return shelfCount.get(); }
        public double getTotalValue() { return totalValue.get(); }
        public int getTotalQuantity() { return totalQuantity.get(); }
    }

    // --- Initialize controller ---
    public void initData(FloorAreaController floorAreaController) {
        this.floorAreaController = floorAreaController;
        floorNameLabel.setText(floorAreaController.getFloorArea().getTitle());

        temperatureChoiceBox.getItems().setAll(Temperature.values());
        temperatureChoiceBox.getSelectionModel().selectFirst();

        // Configure table columns
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        tempColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTemp()));
        lengthColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getLength()).asObject());
        widthColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getWidth()).asObject());
        shelfCounterColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getShelfCount()).asObject());
        totalValueColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalValue()).asObject());
        totalQuantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalQuantity()).asObject());

        refreshAisleTable();

        // 🖱️ Double-click to open Shelf Manager
        aisleTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                AisleRow selected = aisleTable.getSelectionModel().getSelectedItem();
                if (selected != null) openShelfWindow(selected.getName());
            }
        });
    }

    // --- Refresh aisle table ---
    private void refreshAisleTable() {
        aisleTable.getItems().clear();
        var aisles = floorAreaController.getFloorArea().getAisles();

        int totalAisles = 0;
        int totalShelves = 0;
        double totalValue = 0;
        int totalQuantity = 0;

        var node = aisles.getHead();
        while (node != null) {
            var a = node.getData();
            int shelfCount = a.getShelves().size();
            double aisleValue = a.getTotalValue();
            int aisleQty = a.getTotalQuantity();

            aisleTable.getItems().add(
                    new AisleRow(
                            a.getName(),
                            a.getTemperature().toString(),
                            a.getLength(),
                            a.getWidth(),
                            shelfCount,
                            aisleValue,
                            aisleQty
                    )
            );

            totalAisles++;
            totalShelves += shelfCount;
            totalValue += aisleValue;
            totalQuantity += aisleQty;
            node = node.getNext();
        }

        totalAisleLabel.setText(String.valueOf(totalAisles));
        totalShelfLabel.setText(String.valueOf(totalShelves));
        totalValueLabel.setText(String.format("€%.2f", totalValue));
        totalQuantityLabel.setText(String.valueOf(totalQuantity));
    }

    // --- Add aisle ---
    @FXML
    private void handleAddAisle() {
        String name = aisleNameField.getText().trim();
        String lengthText = lengthField.getText().trim();
        String widthText  = widthField.getText().trim();
        Temperature temp  = temperatureChoiceBox.getValue();

        if (!UIHelper.validateNotEmpty(name, lengthText, widthText)) {
            UIHelper.showAlert("Please fill all fields.");
            return;
        }

        Double length = UIHelper.tryParseDouble(lengthText, "Length");
        Double width  = UIHelper.tryParseDouble(widthText, "Width");
        if (length == null || width == null) return;

        // ✅ Try to add the aisle (returns false if name exists anywhere)
        boolean added = floorAreaController.addAisle(name, temp, length, width);

        if (!added) {
            UIHelper.showAlert("An aisle with the same name already exists in the supermarket!");
            return; // ❌ Stop — don't refresh or save
        }

        // ✅ Success
        refreshAisleTable();
        UIHelper.clearFields(aisleNameField, lengthField, widthField);

        // 💾 Auto-save supermarket
        try {
            DataSync.save(floorAreaController.getFloorArea().getParentSupermarket());
        } catch (Exception e) {
            System.err.println("⚠️ Failed to save after adding aisle: " + e.getMessage());
        }
    }

    // --- Remove aisle ---
    @FXML
    private void handleRemoveAisle() {
        AisleRow selected = aisleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIHelper.showAlert("Please select an aisle to remove.");
            return;
        }

        if (!UIHelper.confirm("Confirm Removal",
                "Are you sure you want to remove aisle '" + selected.getName() + "'?")) return;

        floorAreaController.removeAisle(selected.getName());
        refreshAisleTable();

        try {
            DataSync.save(floorAreaController.getFloorArea().getParentSupermarket());
        } catch (Exception e) {
            System.err.println("⚠️ Failed to save after removing aisle: " + e.getMessage());
        }
    }

    // --- Open Shelf Manager ---
    private void openShelfWindow(String aisleName) {
        var aisles = floorAreaController.getFloorArea().getAisles();
        Aisle found = UIHelper.findNodeData(aisles.getHead(), a -> a.getName().equals(aisleName));

        if (found == null) {
            UIHelper.showError("Aisle not found: " + aisleName);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ui/ShelfView.fxml"));
            Parent root = loader.load();

            ShelfControllerUI shelfController = loader.getController();
            shelfController.initData(found);

            Stage stage = new Stage();
            stage.setTitle("Manage Shelves — " + found.getName());
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            UIHelper.showError("Failed to open Shelf Manager window: " + e.getMessage());
        }
    }
}