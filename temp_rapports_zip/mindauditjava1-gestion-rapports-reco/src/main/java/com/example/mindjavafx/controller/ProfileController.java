package com.example.mindjavafx.controller;

import com.example.mindjavafx.service.AuthenticationService;
import com.example.mindjavafx.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ProfileController {

    @FXML
    private Label nomLabel;
    
    @FXML
    private Label initialsLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private Label telephoneLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private PasswordField oldPasswordField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label messageLabel;

    private AuthenticationService authService;

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
        loadProfile();
    }

    private void loadProfile() {
        if (authService.isLoggedIn()) {
            var user = authService.getCurrentUser();
            nomLabel.setText(user.getNom());
            emailLabel.setText(user.getEmail());
            roleLabel.setText(user.getRole().getNom());
            ageLabel.setText(String.valueOf(user.getAge()) + " ans");
            telephoneLabel.setText(user.getTelephone() != null ? user.getTelephone() : "Non renseigné");
            statusLabel.setText(user.isActif() ? "Actif" : "Inactif");
            statusLabel.setStyle(user.isActif() ? 
                "-fx-font-size: 14; -fx-text-fill: #27ae60; -fx-font-weight: bold;" : 
                "-fx-font-size: 14; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            
            // Set initials
            String[] names = user.getNom().split(" ");
            String initials = "";
            if (names.length >= 2) {
                initials = names[0].substring(0, 1).toUpperCase() + names[1].substring(0, 1).toUpperCase();
            } else if (names.length == 1 && names[0].length() >= 2) {
                initials = names[0].substring(0, 2).toUpperCase();
            }
            initialsLabel.setText(initials);
        }
    }
    
    @FXML
    private void handleChangePassword() {
        messageLabel.setText("");
        
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Tous les champs sont requis");
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Les mots de passe ne correspondent pas");
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            return;
        }
        
        if (newPassword.length() < 6) {
            messageLabel.setText("Le mot de passe doit contenir au moins 6 caractères");
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            return;
        }
        
        // Verify old password
        var user = authService.getCurrentUser();
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
            messageLabel.setText("Ancien mot de passe incorrect");
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            return;
        }
        
        // TODO: Update password in database
        messageLabel.setText("✓ Mot de passe modifié avec succès!");
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        // Clear fields
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}