package org.example.ui;

import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.example.util.Node;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class UIHelper {

    // =====================================================
    // 🔔 ALERT UTILITIES (fixed for fullscreen & focus loss)
    // =====================================================
    public static void showAlert(String message) {
        show(Alert.AlertType.WARNING, "Notice", message);
    }

    public static void showError(String message) {
        show(Alert.AlertType.ERROR, "Error", message);
    }

    public static void showInfo(String message) {
        show(Alert.AlertType.INFORMATION, "Information", message);
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // ✅ Attach to focused window (important for fullscreen mode)
        Stage owner = getActiveStage();
        if (owner != null) alert.initOwner(owner);

        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);

        // ✅ Attach to currently focused window
        Stage owner = getActiveStage();
        if (owner != null) alert.initOwner(owner);

        alert.showAndWait();
    }

    // ✅ Helper to find the currently focused stage
    private static Stage getActiveStage() {
        Window window = javafx.stage.Window.getWindows()
                .stream()
                .filter(Window::isFocused)
                .findFirst()
                .orElse(null);
        return (window instanceof Stage) ? (Stage) window : null;
    }

    // =====================================================
    // 🔢 SAFE NUMBER PARSING
    // =====================================================
    public static Integer tryParseInt(String text, String fieldName) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            showAlert(fieldName + " must be a valid integer.");
            return null;
        }
    }

    public static Double tryParseDouble(String text, String fieldName) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            showAlert(fieldName + " must be a valid number.");
            return null;
        }
    }

    // =====================================================
    // 🧩 VALIDATION & CLEARING
    // =====================================================
    public static boolean validateNotEmpty(String... values) {
        for (String v : values) {
            if (v == null || v.trim().isEmpty()) return false;
        }
        return true;
    }

    public static void clearFields(TextInputControl... fields) {
        for (TextInputControl f : fields) f.clear();
    }

    // =====================================================
    // 🔁 LINKED LIST UTILITIES
    // =====================================================
    public static <T> void forEach(Node<T> head, Consumer<T> action) {
        Node<T> node = head;
        while (node != null) {
            action.accept(node.getData());
            node = node.getNext();
        }
    }

    public static <T> T findNodeData(Node<T> head, Predicate<T> condition) {
        Node<T> node = head;
        while (node != null) {
            if (condition.test(node.getData())) return node.getData();
            node = node.getNext();
        }
        return null;
    }

    public static <T> void populateTable(Node<T> head, Consumer<T> addRow) {
        forEach(head, addRow);
    }

    // =====================================================
    // 🪶 LOGGING
    // =====================================================
    public static void log(String emoji, String message) {
        System.out.println(emoji + " " + message);
    }

    // =====================================================
    // 🎬 ANIMATION HELPERS
    // =====================================================
    public static void animatePane(Pane pane, boolean show) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(250), pane);
        FadeTransition fade = new FadeTransition(Duration.millis(250), pane);

        if (show) {
            pane.setVisible(true);
            pane.setManaged(true);
            slide.setFromY(-20);
            slide.setToY(0);
            fade.setFromValue(0);
            fade.setToValue(1);
        } else {
            slide.setFromY(0);
            slide.setToY(-20);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setOnFinished(e -> {
                pane.setVisible(false);
                pane.setManaged(false);
            });
        }

        new ParallelTransition(slide, fade).play();
    }

    // =====================================================
    // 🎨 COMMON STYLES
    // =====================================================
    public static class Style {
        public static final String ACTIVE = "-fx-background-color: #2196F3; -fx-text-fill: white;";
        public static final String INACTIVE = "-fx-background-color: #e0e0e0; -fx-text-fill: black;";
    }
}