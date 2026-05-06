package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
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
    private String userContact;
    private String generatedCode;

    @FXML
    public void initialize() {
        userService = new UserService();
        
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
            
            showSuccess("✅ Code envoyé! Une page web va s'ouvrir avec votre code.");
            
            // Ouvrir la page web
            openVerificationPage(contact);
            
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
    
    /**
     * Ouvre la page de vérification dans le navigateur par défaut
     */
    private void openVerificationPage(String email) {
        try {
            // Créer une page HTML temporaire avec le code
            String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang='fr'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <title>Code de Vérification - MindAudit</title>\n" +
                "    <style>\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            min-height: 100vh;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .container {\n" +
                "            background: white;\n" +
                "            border-radius: 20px;\n" +
                "            padding: 40px;\n" +
                "            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);\n" +
                "            max-width: 500px;\n" +
                "            width: 100%;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .logo { font-size: 4em; margin-bottom: 20px; }\n" +
                "        h1 { color: #667eea; margin-bottom: 10px; font-size: 2em; }\n" +
                "        .subtitle { color: #6b7280; margin-bottom: 30px; font-size: 1.1em; }\n" +
                "        .email-display {\n" +
                "            background: #f3f4f6;\n" +
                "            padding: 15px;\n" +
                "            border-radius: 10px;\n" +
                "            margin-bottom: 30px;\n" +
                "            font-weight: 600;\n" +
                "            color: #374151;\n" +
                "        }\n" +
                "        .code-container {\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            padding: 30px;\n" +
                "            border-radius: 15px;\n" +
                "            margin-bottom: 30px;\n" +
                "        }\n" +
                "        .code-label { color: white; font-size: 0.9em; margin-bottom: 10px; opacity: 0.9; }\n" +
                "        .code {\n" +
                "            font-size: 3em;\n" +
                "            font-weight: bold;\n" +
                "            color: white;\n" +
                "            letter-spacing: 10px;\n" +
                "            font-family: 'Courier New', monospace;\n" +
                "        }\n" +
                "        .instructions {\n" +
                "            background: #eff6ff;\n" +
                "            border-left: 4px solid #3b82f6;\n" +
                "            padding: 20px;\n" +
                "            border-radius: 10px;\n" +
                "            text-align: left;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .instructions h3 { color: #1e40af; margin-bottom: 10px; }\n" +
                "        .instructions ol { margin-left: 20px; color: #374151; }\n" +
                "        .instructions li { margin-bottom: 8px; }\n" +
                "        .btn {\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            color: white;\n" +
                "            border: none;\n" +
                "            padding: 15px 30px;\n" +
                "            border-radius: 10px;\n" +
                "            cursor: pointer;\n" +
                "            font-size: 1em;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .footer { margin-top: 30px; color: #6b7280; font-size: 0.9em; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='container'>\n" +
                "        <div class='logo'>🔐</div>\n" +
                "        <h1>Code de Vérification</h1>\n" +
                "        <p class='subtitle'>Réinitialisation de mot de passe</p>\n" +
                "        <div class='email-display'>📧 " + email + "</div>\n" +
                "        <div class='code-container'>\n" +
                "            <div class='code-label'>Votre code de vérification</div>\n" +
                "            <div class='code'>" + generatedCode + "</div>\n" +
                "        </div>\n" +
                "        <div class='instructions'>\n" +
                "            <h3>📋 Instructions</h3>\n" +
                "            <ol>\n" +
                "                <li>Copiez le code ci-dessus</li>\n" +
                "                <li>Retournez dans l'application MindAudit</li>\n" +
                "                <li>Entrez ce code dans le champ de vérification</li>\n" +
                "                <li>Créez votre nouveau mot de passe</li>\n" +
                "            </ol>\n" +
                "        </div>\n" +
                "        <button class='btn' onclick='copyCode()'>📋 Copier le code</button>\n" +
                "        <div class='footer'>\n" +
                "            <p>MindAudit - Système de Gestion d'Audit Interne</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        function copyCode() {\n" +
                "            navigator.clipboard.writeText('" + generatedCode + "').then(() => {\n" +
                "                alert('✅ Code copié dans le presse-papiers!');\n" +
                "            }).catch(() => {\n" +
                "                alert('Code: " + generatedCode + "');\n" +
                "            });\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
            
            // Créer un fichier temporaire
            File tempFile = File.createTempFile("verification-code-", ".html");
            tempFile.deleteOnExit();
            
            // Écrire le contenu HTML
            java.nio.file.Files.write(tempFile.toPath(), htmlContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            // Ouvrir dans le navigateur
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(tempFile.toURI());
                    System.out.println("[INFO] Page web ouverte avec le code: " + generatedCode);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERREUR] Impossible d'ouvrir le navigateur: " + e.getMessage());
            e.printStackTrace();
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
            
            // Ouvrir à nouveau la page web
            openVerificationPage(userContact);
            
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
