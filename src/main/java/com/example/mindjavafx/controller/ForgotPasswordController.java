package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.EmailService;
import com.example.mindjavafx.service.UserService;
import com.example.mindjavafx.util.ApiClient;
import com.example.mindjavafx.util.Validation;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordController {

    // Step 1
    @FXML private TextField contactField;
    @FXML private CheckBox captchaCheckBox;
    @FXML private Button sendButton;
    
    // Step 2
    @FXML private TextField codeField;
    @FXML private Label sentToLabel;
    @FXML private Label codeDisplayLabel;
    
    // Step 3
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    
    // Common
    @FXML private Label messageLabel;

    private UserService userService;
    private EmailService emailService;
    private String userContact;
    private String generatedCode;

    @FXML
    public void initialize() {
        userService = new UserService();
        emailService = new EmailService();
        
        // Activer le bouton seulement quand la checkbox est cochée
        if (captchaCheckBox != null && sendButton != null) {
            captchaCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                sendButton.setDisable(!newVal);
                if (newVal) {
                    sendButton.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #0066ff; -fx-background-radius: 30; -fx-cursor: hand; -fx-opacity: 1.0;");
                } else {
                    sendButton.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #0066ff; -fx-background-radius: 30; -fx-cursor: hand; -fx-opacity: 0.5;");
                }
            });
        }
    }

    // ==================== STEP 1: Entrer Email/Téléphone ====================
    
    @FXML
    private void handleSendCode() {
        String contact = contactField.getText().trim();
        
        if (contact.isEmpty()) {
            showError("Veuillez entrer votre email ou numéro de téléphone");
            return;
        }
        
        // Vérifier le format
        if (contact.contains("@")) {
            if (!Validation.isValidEmail(contact)) {
                showError("Email invalide");
                return;
            }
        } else {
            if (!Validation.isValidTelephone(contact)) {
                showError("Numéro de téléphone invalide");
                return;
            }
        }
        
        // Appeler l'API pour demander un code
        Map<String, String> data = new HashMap<>();
        data.put("contact", contact);
        
        JsonObject response = ApiClient.post("/password-reset/request", data);
        
        if (response.get("success").getAsBoolean()) {
            userContact = contact;
            
            // Récupérer le code depuis la réponse (pour le développement)
            JsonObject responseData = response.getAsJsonObject("data");
            generatedCode = responseData.get("code").getAsString();
            
            showSuccess("✅ Code envoyé à " + maskContact(contact) + "!");
            
            // Envoyer l'email réel
            emailService.sendPasswordResetCodeAsync(contact, generatedCode);
            
            // Attendre 2 secondes puis passer à l'étape 2
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::loadStep2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } else {
            showError("❌ " + response.get("message").getAsString());
        }
    }
    
    private String generateVerificationCode() {
        java.util.Random random = new java.util.Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    private String maskContact(String contact) {
        if (contact.contains("@")) {
            String[] parts = contact.split("@");
            return parts[0].charAt(0) + "***@" + parts[1];
        } else {
            return contact.substring(0, 2) + "****" + contact.substring(contact.length() - 4);
        }
    }
    

    // ==================== STEP 2: Vérifier le Code ====================
    
    @FXML
    private void handleVerifyCode() {
        String enteredCode = codeField.getText().trim();
        
        if (enteredCode.isEmpty()) {
            showError("Veuillez entrer le code de vérification");
            return;
        }
        
        if (enteredCode.length() != 6) {
            showError("Le code doit contenir 6 chiffres");
            return;
        }
        
        // Appeler l'API pour vérifier le code
        Map<String, String> data = new HashMap<>();
        data.put("contact", userContact);
        data.put("code", enteredCode);
        
        JsonObject response = ApiClient.post("/password-reset/verify", data);
        
        if (response.get("success").getAsBoolean()) {
            generatedCode = enteredCode; // Sauvegarder le code vérifié
            showSuccess("✅ Code vérifié avec succès!");
            
            // Attendre 1 seconde puis passer à l'étape 3
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(this::loadStep3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showError("❌ " + response.get("message").getAsString());
        }
    }
    
    @FXML
    private void handleResendCode() {
        // Appeler l'API pour demander un nouveau code
        Map<String, String> data = new HashMap<>();
        data.put("contact", userContact);
        
        JsonObject response = ApiClient.post("/password-reset/request", data);
        
        if (response.get("success").getAsBoolean()) {
            // Récupérer le nouveau code
            JsonObject responseData = response.getAsJsonObject("data");
            generatedCode = responseData.get("code").getAsString();
            
            // Envoyer l'email
            emailService.sendPasswordResetCodeAsync(userContact, generatedCode);
            
            // Afficher le nouveau code dans l'interface
            if (codeDisplayLabel != null) {
                codeDisplayLabel.setText(generatedCode);
            }
            
            showSuccess("✅ Nouveau code envoyé! Page web ouverte.");
        } else {
            showError("❌ " + response.get("message").getAsString());
        }
    }
    
    @FXML
    private void handleBackToStep1() {
        loadStep1();
    }

    // ==================== STEP 3: Nouveau Mot de Passe ====================
    
    @FXML
    private void handleResetPassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }
        
        if (!Validation.isValidPassword(newPassword)) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            return;
        }
        
        // Appeler l'API pour réinitialiser le mot de passe
        Map<String, String> data = new HashMap<>();
        data.put("contact", userContact);
        data.put("code", generatedCode);
        data.put("newPassword", newPassword);
        
        JsonObject response = ApiClient.post("/password-reset/reset", data);
        
        if (response.get("success").getAsBoolean()) {
            showSuccess("✅ Mot de passe réinitialisé avec succès!");
            
            // Attendre 2 secondes puis retourner à la page de connexion
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleBackToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showError("❌ " + response.get("message").getAsString());
        }
    }

    // ==================== Navigation ====================
    
    private void loadStep1() {
        loadPage("/fxml/forgot-password-step1.fxml", "Mot de passe oublié - Étape 1");
    }
    
    private void loadStep2() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forgot-password-step2.fxml"));
            Parent root = loader.load();
            
            ForgotPasswordController controller = loader.getController();
            controller.userContact = this.userContact;
            controller.generatedCode = this.generatedCode;
            controller.userService = this.userService;
            
            if (controller.sentToLabel != null) {
                controller.sentToLabel.setText("Code envoyé à " + maskContact(userContact));
            }
            
            // AFFICHER LE CODE DANS L'INTERFACE!
            if (controller.codeDisplayLabel != null) {
                controller.codeDisplayLabel.setText(this.generatedCode);
            }
            
            Stage stage = (Stage) contactField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 600));
            stage.setTitle("Mot de passe oublié - Étape 2");
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadStep3() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forgot-password-step3.fxml"));
            Parent root = loader.load();
            
            ForgotPasswordController controller = loader.getController();
            controller.userContact = this.userContact;
            controller.generatedCode = this.generatedCode;
            controller.userService = this.userService;
            
            Stage stage = (Stage) codeField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 550));
            stage.setTitle("Mot de passe oublié - Étape 3");
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBackToLogin() {
        loadPage("/fxml/login.fxml", "MindAudit - Connexion");
    }
    
    private void loadPage(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 1050, 650));
            stage.setTitle(title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==================== Helpers ====================
    
    private void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            messageLabel.setText(message);
        }
    }
    
    private void showSuccess(String message) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            messageLabel.setText(message);
        }
    }
}
