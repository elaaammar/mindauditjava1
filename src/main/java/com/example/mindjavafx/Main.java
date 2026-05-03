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
        stage.show();
    }

    @Override
    public void stop() {
        // Arrêter le serveur API quand l'application se ferme
        RestApiServer.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}