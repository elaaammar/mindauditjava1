package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.Role;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.UserService;
import com.example.mindjavafx.util.PasswordUtil;
import com.example.mindjavafx.util.Validation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField telephoneField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    private UserService userService;

    @FXML
    public void initialize() {
        userService = new UserService();
    }

    @FXML
    private void handleRegister() {
        // Clear previous messages
        errorLabel.setText("");
        successLabel.setText("");

        // Get form values
        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String ageStr = ageField.getText().trim();
        String telephone = telephoneField.getText().trim();

        // Validation
        if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || ageStr.isEmpty()) {
            errorLabel.setText("❌ Tous les champs requis doivent être remplis");
            return;
        }

        if (!Validation.isValidName(nom)) {
            errorLabel.setText("❌ Nom invalide");
            return;
        }

        if (!Validation.isValidEmail(email)) {
            errorLabel.setText("❌ Email invalide");
            return;
        }

        if (!Validation.isValidPassword(password)) {
            errorLabel.setText("❌ Mot de passe invalide (min 6 caractères)");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            errorLabel.setText("❌ Âge invalide");
            return;
        }

        if (!Validation.isValidAge(age)) {
            errorLabel.setText("❌ Âge doit être entre 18 et 100");
            return;
        }

        if (!telephone.isEmpty() && !Validation.isValidTelephone(telephone)) {
            errorLabel.setText("❌ Téléphone invalide");
            return;
        }

        // Create user with "User" role (role_id = 2)
        try {
            Role userRole = new Role(2, "User");
            User newUser = new User(nom, email, PasswordUtil.hashPassword(password), age, userRole);
            newUser.setTelephone(telephone);
            newUser.setActif(true);

            int userId = userService.addUser(newUser);
            if (userId > 0) {
                successLabel.setText("✅ Compte créé avec succès! Vous pouvez maintenant vous connecter.");
                successLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                
                // Clear form
                clearFields();
                
                // Wait 2 seconds then redirect to login
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::handleBackToLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                errorLabel.setText("❌ Erreur lors de la création du compte");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                errorLabel.setText("❌ Cet email est déjà utilisé");
            } else {
                errorLabel.setText("❌ Erreur base de données: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Stage stage = (Stage) nomField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root, 1050, 650);
            stage.setScene(scene);
            stage.setTitle("MindAudit - Connexion");
            stage.setResizable(true);
            stage.setMaximized(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nomField.clear();
        emailField.clear();
        passwordField.clear();
        ageField.clear();
        telephoneField.clear();
    }
}
