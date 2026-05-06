package com.example.mindjavafx.controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SplashController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label loadingLabel;

    private Stage stage;

    @FXML
    public void initialize() {
        // Animation de la barre de progression
        animateProgressBar();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void animateProgressBar() {
        // Simuler le chargement avec des étapes
        new Thread(() -> {
            try {
                // Étape 1: Initialisation
                updateProgress(0.2, "Initialisation...");
                Thread.sleep(700);

                // Étape 2: Chargement des modules
                updateProgress(0.4, "Chargement des modules...");
                Thread.sleep(700);

                // Étape 3: Connexion à la base de données
                updateProgress(0.6, "Connexion a la base de donnees...");
                Thread.sleep(700);

                // Étape 4: Préparation de l'interface
                updateProgress(0.8, "Preparation de l'interface...");
                Thread.sleep(700);

                // Étape 5: Finalisation
                updateProgress(1.0, "Finalisation...");
                Thread.sleep(700);

                // Transition vers la page de login
                javafx.application.Platform.runLater(this::showLoginScreen);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateProgress(double progress, String message) {
        javafx.application.Platform.runLater(() -> {
            progressBar.setProgress(progress);
            loadingLabel.setText(message);
        });
    }

    private void showLoginScreen() {
        try {
            System.out.println("[DEBUG] Début de showLoginScreen");
            
            // Fermer le splash immédiatement
            javafx.application.Platform.runLater(() -> {
                stage.close();
                System.out.println("[DEBUG] Splash fermé");
            });
            
            // Attendre 500ms pour s'assurer que le splash est bien fermé
            Thread.sleep(500);
            
            // Afficher la fenêtre login
            javafx.application.Platform.runLater(() -> {
                try {
                    System.out.println("[DEBUG] Chargement de la page login...");
                    
                    // Charger la page de login
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root, 1050, 650);

                    System.out.println("[DEBUG] Page login chargée avec succès");
                    
                    // Créer une NOUVELLE fenêtre avec barre de titre
                    Stage loginStage = new Stage();
                    loginStage.initStyle(javafx.stage.StageStyle.DECORATED);
                    loginStage.setScene(scene);
                    loginStage.setTitle("MindAudit - Connexion");
                    loginStage.setResizable(true);
                    loginStage.setMaximized(false);
                    loginStage.setAlwaysOnTop(true); // Forcer au premier plan
                    
                    System.out.println("[DEBUG] Configuration de la fenêtre login terminée");
                    
                    // Centrer et afficher la fenêtre login
                    loginStage.centerOnScreen();
                    loginStage.show();
                    loginStage.toFront();
                    loginStage.requestFocus();
                    
                    // Retirer le "always on top" après 1 seconde
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            javafx.application.Platform.runLater(() -> {
                                loginStage.setAlwaysOnTop(false);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    
                    System.out.println("[DEBUG] Fenêtre login affichée avec succès !");

                } catch (IOException e) {
                    System.err.println("[ERREUR] Impossible de charger login.fxml");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("[ERREUR] Exception lors de l'affichage");
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            System.err.println("[ERREUR] Exception dans showLoginScreen");
            e.printStackTrace();
        }
    }
}
