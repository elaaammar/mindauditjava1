package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.Role;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuthenticationService;
import com.example.mindjavafx.service.UserService;
import com.example.mindjavafx.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class ProfileController {

    @FXML private Label nomLabelHeader;
    @FXML private Label roleLabelBadge;
    @FXML private Label initialsLabel;
    @FXML private Label statusLabel;

    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField ageField;
    @FXML private TextField telephoneField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private Label profileMessageLabel;
    @FXML private Label passwordMessageLabel;

    private AuthenticationService authService;
    private UserService userService;
    private Object dashboardController;

    @FXML
    public void initialize() {
        userService = new UserService();
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
        loadProfile();
    }

    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
    }

    private void loadProfile() {
        if (authService != null && authService.isLoggedIn()) {
            User user = authService.getCurrentUser();
            
            // Header Info
            nomLabelHeader.setText(user.getNom());
            roleLabelBadge.setText(user.getRole().getNom());
            statusLabel.setText(user.isActif() ? "Actif" : "Inactif");
            
            // Initials
            String[] names = user.getNom().split(" ");
            String initials = "";
            if (names.length >= 2) {
                initials = names[0].substring(0, 1).toUpperCase() + names[1].substring(0, 1).toUpperCase();
            } else if (names.length == 1 && names[0].length() >= 2) {
                initials = names[0].substring(0, 2).toUpperCase();
            } else if (names.length == 1) {
                initials = names[0].substring(0, 1).toUpperCase();
            }
            initialsLabel.setText(initials);

            // Form Fields
            nomField.setText(user.getNom());
            emailField.setText(user.getEmail());
            ageField.setText(String.valueOf(user.getAge()));
            telephoneField.setText(user.getTelephone() != null ? user.getTelephone() : "");
            roleComboBox.setValue(user.getRole().getNom());
        }
    }

    @FXML
    private void handleUpdateProfile() {
        profileMessageLabel.setText("");
        
        if (authService == null || !authService.isLoggedIn()) return;
        
        User user = authService.getCurrentUser();
        
        try {
            String nom = nomField.getText().trim();
            String ageStr = ageField.getText().trim();
            String telephone = telephoneField.getText().trim();
            String roleNom = roleComboBox.getValue();

            if (nom.isEmpty() || ageStr.isEmpty()) {
                showError(profileMessageLabel, "Le nom et l'âge sont requis.");
                return;
            }

            int age = Integer.parseInt(ageStr);
            
            // Update user object
            user.setNom(nom);
            user.setAge(age);
            user.setTelephone(telephone);
            
            // Handle role change
            int roleId = getRoleIdByName(roleNom);
            user.setRole(new Role(roleId, roleNom));

            // Save to DB
            boolean success = userService.updateUser(user);
            
            if (success) {
                showSuccess(profileMessageLabel, "✓ Profil mis à jour avec succès !");
                loadProfile(); // Refresh local UI
                if (dashboardController != null) {
                    try {
                        dashboardController.getClass().getMethod("refreshUserInfo").invoke(dashboardController);
                    } catch (Exception e) {
                        // Ignore if method doesn't exist
                    }
                }
            } else {
                showError(profileMessageLabel, "Échec de la mise à jour en base de données.");
            }

        } catch (NumberFormatException e) {
            showError(profileMessageLabel, "L'âge doit être un nombre valide.");
        } catch (SQLException e) {
            showError(profileMessageLabel, "Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getRoleIdByName(String name) {
        return switch (name) {
            case "Admin" -> 1;
            case "User" -> 2;
            case "Auditeur" -> 3;
            default -> 2;
        };
    }

    @FXML
    private void handleChangePassword() {
        passwordMessageLabel.setText("");
        
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError(passwordMessageLabel, "Tous les champs sont requis.");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showError(passwordMessageLabel, "Les mots de passe ne correspondent pas.");
            return;
        }
        
        if (newPassword.length() < 6) {
            showError(passwordMessageLabel, "Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }
        
        User user = authService.getCurrentUser();
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
            showError(passwordMessageLabel, "Ancien mot de passe incorrect.");
            return;
        }
        
        try {
            user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
            boolean success = userService.updateUser(user);
            
            if (success) {
                showSuccess(passwordMessageLabel, "✓ Mot de passe modifié avec succès !");
                oldPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                showError(passwordMessageLabel, "Erreur lors de la mise à jour du mot de passe.");
            }
        } catch (SQLException e) {
            showError(passwordMessageLabel, "Erreur : " + e.getMessage());
        }
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #27ae60;");
    }
}