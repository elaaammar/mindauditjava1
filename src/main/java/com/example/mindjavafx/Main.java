package com.example.mindjavafx;

import com.example.mindjavafx.apirest.RestApiServer;
import com.example.mindjavafx.controller.SplashController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        com.gestionaudit.MainFx.setPrimaryStage(stage);
        // Démarrer le serveur API REST dans un thread séparé
        new Thread(() -> {
            try {
                RestApiServer.start();
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors du démarrage de l'API: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
        
        // Charger le splash screen
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/splash.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        
        // Obtenir le contrôleur et lui passer le stage
        SplashController controller = fxmlLoader.getController();
        controller.setStage(stage);
        
        stage.setTitle("MindAudit");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED); // Pas de barre de titre pour le splash
        stage.setMaximized(true); // Prendre toute la page
        
        // S'assurer que le processus Java meurt quand on clique sur X (si le style change)
        stage.setOnCloseRequest(event -> {
            javafx.application.Platform.exit();
            System.exit(0);
        });
        
        stage.show();
    }

    @Override
    public void stop() {
        // Arrêter le serveur API quand l'application se ferme
        RestApiServer.stop();
        System.out.println("Arrêt complet de l'application JavaFX.");
        System.exit(0); // Force kill the JVM to ensure no duplicate windows or phantom processes remain
    }

    public static void main(String[] args) {
        launch(args);
    }
}