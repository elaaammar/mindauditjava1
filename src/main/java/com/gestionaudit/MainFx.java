package com.gestionaudit;

import javafx.scene.Scene;
import java.net.URL;

public class MainFx {
    private static javafx.stage.Stage primaryStage;

    public static void setPrimaryStage(javafx.stage.Stage stage) {
        primaryStage = stage;
    }

    public static javafx.stage.Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Applies modern.css from classpath to the scene.
     */
    public static void attachModernStylesheet(Scene scene) {
        URL url = MainFx.class.getResource("/styles/modern.css");
        if (url == null) {
            System.err.println("WARNING: /styles/modern.css not found on classpath.");
            return;
        }
        String href = url.toExternalForm();
        if (!scene.getStylesheets().contains(href)) {
            scene.getStylesheets().add(href);
        }
    }
}
