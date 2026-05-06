package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Step 5.1: Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Login.fxml"));
            Parent root = loader.load();

            // Step 5.2: Create the scene and set it on the stage
            Scene scene = new Scene(root);
            primaryStage.setTitle("Audit Management System - Welcome");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true); // Allow resizing for better view of cards
            primaryStage.setMaximized(true); // Start the app in full screen mode
            
            // Step 5.3: Show the window
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
