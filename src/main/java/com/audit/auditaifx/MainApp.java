package com.audit.auditaifx;

import com.audit.auditaifx.service.RapportService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApp.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);

            // Ajouter le CSS (doit être fait avant de montrer le stage pour éviter le
            // flash)
            String css = MainApp.class.getResource("audit.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setTitle("AuditAI - Gestion des Rapports d'Audit");
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(550);
            stage.show();

            // Charger les données de démonstration au démarrage
            Thread seedThread = new Thread(() -> {
                RapportService seedService = new RapportService();
                seedService.initialiserDonneesSiVide();
            });
            seedThread.setDaemon(true);
            seedThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("CRITICAL ERROR: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}