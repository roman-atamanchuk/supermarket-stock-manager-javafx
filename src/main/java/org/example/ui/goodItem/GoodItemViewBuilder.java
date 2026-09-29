package org.example.ui.goodItem;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.data.*;
import org.example.store.*;
import org.example.ui.*;
import org.example.util.Node;

public class GoodItemViewBuilder {
    private final GoodItemControllerUI ui;

    public GoodItemViewBuilder(GoodItemControllerUI ui) {
        this.ui = ui;
    }

    // =============================================
    // 🧱 MAIN SETUP
    // =============================================
    public void setupUI(Aisle aisle, Shelf shelf) {
        ui.getFloorBar().getChildren().clear();
        ui.getAisleBar().getChildren().clear();
        ui.getShelfBar().getChildren().clear();

        // === FLOORS buttons ===
        FloorArea parentArea = aisle.getParentArea();
        Supermarket parentMarket = (parentArea != null) ? parentArea.getParentSupermarket() : null;
        if (parentMarket != null && parentMarket.getFloorAreas() != null) {
            Node<FloorArea> floorNode = parentMarket.getFloorAreas().getHead();
            while (floorNode != null) {
                FloorArea f = floorNode.data;
                Button floorBtn = new Button(f.getTitle());

                // Highlight active floor
                if (f == parentArea) floorBtn.setStyle(activeStyle());
                else floorBtn.setStyle(inactiveStyle());

                floorBtn.setOnAction(e -> {
                    if (f.getAisles() != null && f.getAisles().getHead() != null) {
                        Aisle firstAisle = f.getAisles().getHead().data;
                        if (firstAisle.getShelves().getHead() != null) {
                            ui.initData(firstAisle, firstAisle.getShelves().getHead().data);
                        }
                    }
                });

                ui.getFloorBar().getChildren().add(floorBtn);
                floorNode = floorNode.next;
            }

            // === ➕ Add Floor Button ===
            Button addFloorBtn = createPlusButton();
            addFloorBtn.setOnAction(e -> openDashboardWindow(parentMarket));
            ui.getFloorBar().getChildren().add(addFloorBtn);
        }

        // === AISLES buttons ===
        if (parentArea != null && parentArea.getAisles() != null) {
            Node<Aisle> aisleNode = parentArea.getAisles().getHead();
            while (aisleNode != null) {
                Aisle a = aisleNode.data;
                Button aisleBtn = new Button(a.getName());

                if (a == aisle) aisleBtn.setStyle(activeStyle());
                else aisleBtn.setStyle(inactiveStyle());

                aisleBtn.setOnAction(e -> {
                    if (a.getShelves() != null && a.getShelves().getHead() != null) {
                        Shelf firstShelf = a.getShelves().getHead().data;
                        ui.initData(a, firstShelf);
                    }
                });

                ui.getAisleBar().getChildren().add(aisleBtn);
                aisleNode = aisleNode.next;
            }

            // === ➕ Add Aisle Button ===
            Button addAisleBtn = createPlusButton();
            addAisleBtn.setOnAction(e -> openAisleManagerWindow(parentArea));
            ui.getAisleBar().getChildren().add(addAisleBtn);
        }

        // === SHELVES buttons ===
        if (aisle.getShelves() != null) {
            Node<Shelf> shelfNode = aisle.getShelves().getHead();
            while (shelfNode != null) {
                Shelf s = shelfNode.data;
                Button shelfBtn = new Button("Shelf #" + s.getNumber());

                if (s == shelf) shelfBtn.setStyle(activeStyle());
                else shelfBtn.setStyle(inactiveStyle());

                shelfBtn.setOnAction(e -> ui.initData(aisle, s));
                ui.getShelfBar().getChildren().add(shelfBtn);
                shelfNode = shelfNode.next;
            }

            // === ➕ Add Shelf Button ===
            Button addShelfBtn = createPlusButton();
            addShelfBtn.setOnAction(e -> openShelfManagerWindow(aisle));
            ui.getShelfBar().getChildren().add(addShelfBtn);
        }

        // === Info labels ===
        ui.getAisleLabel().setText("Aisle: " + aisle.getName());
        ui.getShelfNumberLabel().setText("Shelf #" + shelf.getNumber());
        ui.getTempCombo().getItems().setAll(Temperature.values());
        ui.getTempCombo().getSelectionModel().select(Temperature.UNREFRIGERATED);

        // === Fill table ===
        refreshGoodsTable();
        setupImageColumn();
    }

    // =============================================
    // 🪟 OPEN DASHBOARD WINDOW
    // =============================================
    private void openDashboardWindow(Supermarket parentMarket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ui/DashboardView.fxml"));
            Scene scene = new Scene(loader.load());

            DashboardController controller = loader.getController();
            if (parentMarket != null) {
                SupermarketController smController = new SupermarketController(parentMarket);
                controller.initData(smController);
            }

            Stage stage = new Stage();
            stage.setTitle("Manage Floors");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setOnHidden(e -> ui.initData(ui.getAisle(), ui.getShelf()));
            stage.show();

        } catch (Exception e) {
            UIHelper.showError("⚠️ Failed to open Dashboard: " + e.getMessage());
        }
    }

    // =============================================
    // 🪟 OPEN AISLE MANAGER WINDOW
    // =============================================
    private void openAisleManagerWindow(FloorArea parentArea) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ui/AisleView.fxml"));
            Scene scene = new Scene(loader.load());

            AisleControllerUI aisleController = loader.getController();
            FloorAreaController floorController = new FloorAreaController(parentArea);
            aisleController.initData(floorController);

            Stage stage = new Stage();
            stage.setTitle("Manage Aisles — " + parentArea.getTitle());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setOnHidden(e -> ui.initData(ui.getAisle(), ui.getShelf()));
            stage.show();

        } catch (Exception e) {
            UIHelper.showError("⚠️ Failed to open Aisle Manager: " + e.getMessage());
        }
    }

    // =============================================
    // 🪟 OPEN SHELF MANAGER WINDOW
    // =============================================

    private void openShelfManagerWindow(Aisle parentAisle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ui/ShelfView.fxml"));
            Scene scene = new Scene(loader.load());

            ShelfControllerUI shelfController = loader.getController();
            shelfController.initData(parentAisle);  // ✅ pass Aisle directly, not controller

            Stage stage = new Stage();
            stage.setTitle("Manage Shelves — " + parentAisle.getName());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // 🔁 Refresh when window closes
            stage.setOnHidden(e -> ui.initData(ui.getAisle(), ui.getShelf()));

            stage.show();

        } catch (Exception e) {
            UIHelper.showError("⚠️ Failed to open Shelf Manager: " + e.getMessage());
        }
    }
    // =============================================
    // 🧾 TABLE SETUP
    // =============================================
    public void refreshGoodsTable() {
        ui.getGoodsTable().getItems().clear();
        Shelf shelf = ui.getShelf();
        if (shelf == null || shelf.getGoods() == null) return;

        UIHelper.forEach(shelf.getGoods().getHead(), g ->
                ui.getGoodsTable().getItems().add(new GoodRow(g)));

        ui.getGoodsTable().refresh();
    }

    // =============================================
    // 🖼 IMAGE COLUMN SETUP
    // =============================================
    private void setupImageColumn() {
        ui.getPhotoColumn().setCellFactory(col -> new javafx.scene.control.TableCell<GoodRow, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isBlank()) {
                    setGraphic(null);
                } else {
                    try {
                        imageView.setImage(new Image(url, 60, 60, true, true));
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    // =============================================
    // 🎨 STYLE HELPERS
    // =============================================
    private String activeStyle() {
        return """
            -fx-background-color: #bbbbbb;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-border-radius: 1;
            -fx-background-radius: 1;
        """;
    }

    private String inactiveStyle() {
        return """
            -fx-background-color: #f0f0f0;
            -fx-cursor: hand;
            -fx-border-radius: 1;
            -fx-background-radius: 1;
        """;
    }

    private Button createPlusButton() {
        Button btn = new Button("➕");
        btn.setStyle("""
            -fx-background-color: #aaaaaa;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-border-radius: 1;
            -fx-background-radius: 1;
            -fx-cursor: hand;
        """);
        return btn;
    }
}