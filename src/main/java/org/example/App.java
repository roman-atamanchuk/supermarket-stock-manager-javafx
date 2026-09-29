package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.data.*;
import org.example.store.SupermarketController;
import org.example.ui.goodItem.GoodItemControllerUI;
import org.example.util.GoogleSheetsService;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // =====================================================
        // 📥 1. Try to load supermarket data from Google Sheets
        // =====================================================
        Supermarket loadedMarket = null;
        try {
            loadedMarket = GoogleSheetsService.loadSupermarket();
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load from Google Sheets: " + e.getMessage());
        }

        // =====================================================
        // 🏪 2. Create or use loaded supermarket
        // =====================================================
        SupermarketController supermarketController;

        if (loadedMarket != null) {
            supermarketController = new SupermarketController(loadedMarket);
            System.out.println("✅ Loaded supermarket from Google Sheets: " + loadedMarket.getName());
        } else {
            supermarketController = new SupermarketController("SuperValu");
            System.out.println("🆕 Created new supermarket SuperValu");
        }

        Supermarket supermarket = supermarketController.getSupermarket();

        // =====================================================
        // ❌ REMOVE auto-creation of Floor/Aisle/Shelf
        // =====================================================

        // =====================================================
        // 🧭 3. SAFE GET — Supermarket may be empty after RESET
        // =====================================================
        FloorArea firstFloor = null;
        Aisle firstAisle = null;
        Shelf firstShelf = null;

        if (!supermarket.getFloorAreas().isEmpty()) {
            firstFloor = supermarket.getFloorAreas().getHead().data;

            if (!firstFloor.getAisles().isEmpty()) {
                firstAisle = firstFloor.getAisles().getHead().data;

                if (!firstAisle.getShelves().isEmpty()) {
                    firstShelf = firstAisle.getShelves().getHead().data;
                }
            }
        }

        // =====================================================
        // 🎛️ 4. Load GoodItem view and initialize it
        // =====================================================
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/ui/gooditem-view.fxml")
        );
        Parent root = loader.load();

        GoodItemControllerUI ui = loader.getController();
        ui.initData(firstAisle, firstShelf);   // <-- safe even when null

        // =====================================================
        // 🏷️ 5. Safe window title
        // =====================================================
        String title = "Goods Management";
        if (firstAisle != null) {
            title += " — " + firstAisle.getName();
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}