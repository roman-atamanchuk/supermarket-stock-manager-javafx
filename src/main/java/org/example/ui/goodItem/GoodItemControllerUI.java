package org.example.ui.goodItem;

import org.example.util.DataSync;

import org.example.store.SupermarketController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.SplitPane;
import org.example.data.*;
import org.example.ui.UIHelper;

public class GoodItemControllerUI {

    @FXML private TableColumn<GoodRow, String> floorColumn, aisleColumn, shelfColumn;
    @FXML private Label shelfNumberLabel, aisleLabel, photoNameLabel;
    @FXML private HBox shelfBar;
    @FXML private FlowPane aisleBar;
    @FXML private TableView<GoodRow> goodsTable;
    @FXML private TableColumn<GoodRow, String> nameColumn, descColumn, tempColumn, photoColumn;
    @FXML private TableColumn<GoodRow, Integer> qtyColumn;
    @FXML private TableColumn<GoodRow, Double> priceColumn;

    // ⬇️ ADD THIS LINE
    @FXML private TextField nameField, descField, qtyField, priceField, weightField, photoField, searchField, removeQtyField;

    @FXML private ComboBox<Temperature> tempCombo;
    @FXML private ImageView photoPreview;
    @FXML private TextArea photoDescArea;
    @FXML private VBox formPane;
    @FXML private FlowPane floorBar;

    @FXML private SplitPane mainSplit;

    private Shelf shelf;
    private Aisle aisle;
    private boolean formVisible = false;

    private GoodItemViewBuilder viewBuilder;
    private GoodItemActions actions;
    private GoodItemPreview preview;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));
        descColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDesc()));
        qtyColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getQty()).asObject());
        priceColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPrice()).asObject());
        tempColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTemp()));
        photoColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPhotoUrl()));
        floorColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getFloorTitle()));
        aisleColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAisleNumber()));
        shelfColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getShelfNumber()));

        if (mainSplit != null) {
            mainSplit.widthProperty().addListener((obs, oldVal, newVal) -> {
                mainSplit.setDividerPositions(0.6);
            });

            photoPreview.fitWidthProperty().bind(mainSplit.widthProperty().multiply(0.35));
            photoPreview.setPreserveRatio(true);
        }
    }

    public void initData(Aisle aisle, Shelf shelf) {
        this.aisle = aisle;
        this.shelf = shelf;

        viewBuilder = new GoodItemViewBuilder(this);
        actions = new GoodItemActions(this);
        preview = new GoodItemPreview(this);

        viewBuilder.setupUI(aisle, shelf);
        preview.setupRowSelection();
    }

    @FXML
    private void toggleFormVisibility() {
        UIHelper.animatePane(formPane, !formVisible);
        formVisible = !formVisible;
    }

    @FXML
    public FlowPane getFloorBar() { return floorBar; }

    @FXML
    public void refreshGoodsTable() {
        viewBuilder.refreshGoodsTable();
        goodsTable.refresh();
    }

    @FXML
    private void handleAddGood() { actions.handleAddGood(); }

    @FXML
    private void handleRemoveGood() { actions.handleRemoveGood(); }

    // ======== GETTERS ========
    public Label getAisleLabel() { return aisleLabel; }
    public Shelf getShelf() { return shelf; }
    public Aisle getAisle() { return aisle; }
    public TableView<GoodRow> getGoodsTable() { return goodsTable; }
    public Label getShelfNumberLabel() { return shelfNumberLabel; }
    public Label getPhotoNameLabel() { return photoNameLabel; }
    public TextArea getPhotoDescArea() { return photoDescArea; }
    public ImageView getPhotoPreview() { return photoPreview; }
    public VBox getFormPane() { return formPane; }

    public ComboBox<Temperature> getTempCombo() { return tempCombo; }
    public TextField getNameField() { return nameField; }
    public TextField getDescField() { return descField; }
    public TextField getQtyField() { return qtyField; }
    public TextField getPriceField() { return priceField; }
    public TextField getWeightField() { return weightField; }
    public TextField getPhotoField() { return photoField; }

    // ⬇️ NEW GETTER
    public TextField getRemoveQtyField() { return removeQtyField; }

    public TableColumn<GoodRow, String> getPhotoColumn() { return photoColumn; }
    public HBox getShelfBar() { return shelfBar; }
    public FlowPane getAisleBar() { return aisleBar; }

    @FXML
    private void handleSearchGood() {
        String term = searchField.getText();
        if (term == null || term.trim().isEmpty()) {
            UIHelper.showAlert("Please enter a search term.");
            return;
        }

        term = term.trim();
        System.out.println("🔍 Searching supermarket for: " + term);

        Supermarket supermarket = aisle.getParentArea().getParentSupermarket();
        SupermarketController smController = new SupermarketController(supermarket);

        var results = smController.searchGoodsByName(term);

        goodsTable.getItems().clear();

        var node = results.getHead();
        while (node != null) {
            goodsTable.getItems().add(new GoodRow(node.getData()));
            node = node.getNext();
        }

        System.out.println("✅ Found " + goodsTable.getItems().size() + " goods matching '" + term + "'");
    }

    @FXML
    private void handleResetSearch() {
        if (searchField != null) searchField.clear();
        refreshGoodsTable();
        System.out.println("🔄 Search reset — showing current shelf goods again.");
    }
    @FXML
    private void handleResetDatabase() {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset Database");
        confirm.setHeaderText("Delete ALL supermarket data?");
        confirm.setContentText("This will remove all floors, aisles, shelves, and goods.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK)
            return;

        // ===== GET SUPERMARKET =====
        Supermarket supermarket = aisle.getParentArea().getParentSupermarket();

        // ===== FULL CLEAR =====
        supermarket.getFloorAreas().clear();

        // ===== CREATE CLEAN DEFAULT STRUCTURE =====
        FloorArea floor = new FloorArea("Floor 1", "1");
        supermarket.getFloorAreas().add(floor);
        floor.setParentSupermarket(supermarket);

        Aisle newAisle = new Aisle("Aisle 1", Temperature.UNREFRIGERATED, 10, 10);
        newAisle.setParentArea(floor);
        floor.getAisles().add(newAisle);

        Shelf newShelf = new Shelf(1);
        newShelf.setParentAisle(newAisle);
        newAisle.getShelves().add(newShelf);

        // ===== UPDATE CONTROLLER STATE =====
        this.aisle = newAisle;
        this.shelf = newShelf;

        // ===== CLEAR UI =====
        floorBar.getChildren().clear();
        aisleBar.getChildren().clear();
        shelfBar.getChildren().clear();
        goodsTable.getItems().clear();

        photoPreview.setImage(null);
        photoNameLabel.setText("");
        photoDescArea.clear();

        // ===== REBUILD UI =====
        viewBuilder.setupUI(newAisle, newShelf);
        preview.setupRowSelection();

        // ===== SAVE, so the old data doesn't come back on the next start =====
        DataSync.save(supermarket);

        Alert done = new Alert(Alert.AlertType.INFORMATION);
        done.setTitle("Reset Complete");
        done.setHeaderText("Database Reset");
        done.setContentText("All supermarket data has been deleted.");
        done.showAndWait();

        System.out.println("🗑 Database fully reset and default supermarket rebuilt.");
    }
}