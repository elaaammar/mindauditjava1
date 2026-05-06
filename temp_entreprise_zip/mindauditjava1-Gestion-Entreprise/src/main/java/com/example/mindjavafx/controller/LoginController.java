package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.Role;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuthenticationService;
import com.example.mindjavafx.service.GoogleOAuthService;
import com.example.mindjavafx.service.FacebookOAuthService;
import com.example.mindjavafx.service.UserService;
import com.example.mindjavafx.service.EmailService;
import com.example.mindjavafx.service.NotificationService;
import com.example.mindjavafx.util.Validation;
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

import java.io.IOException;
import java.util.Map;
import java.util.prefs.Preferences;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;
    
    @FXML
    private CheckBox rememberMeCheckBox;

    private AuthenticationService authService;
    private UserService userService;
    private EmailService emailService;
    private NotificationService notificationService;
    
    // Preferences pour sauvegarder l'email
    private static final String PREFS_KEY_EMAIL = "saved_email";
    private static final String PREFS_KEY_PASSWORD = "saved_password";
    private static final String PREFS_KEY_REMEMBER = "remember_me";
    private Preferences prefs;

    @FXML
    public void initialize() {
        authService = new AuthenticationService();
        userService = new UserService();
        emailService = new EmailService();
        notificationService = new NotificationService();
        
        // Initialiser les préférences
        prefs = Preferences.userNodeForPackage(LoginController.class);
        
        // Charger l'email et le mot de passe sauvegardés si "Se souvenir de moi" était coché
        boolean rememberMe = prefs.getBoolean(PREFS_KEY_REMEMBER, false);
        if (rememberMe) {
            String savedEmail = prefs.get(PREFS_KEY_EMAIL, "");
            String savedPassword = prefs.get(PREFS_KEY_PASSWORD, "");
            
            if (!savedEmail.isEmpty()) {
                emailField.setText(savedEmail);
                rememberMeCheckBox.setSelected(true);
                System.out.println("[INFO] Email chargé: " + savedEmail);
                
                // Ajouter l'autocomplétion pour l'email sauvegardé
                setupEmailAutoComplete(savedEmail);
            }
            
            if (!savedPassword.isEmpty()) {
                // Décoder le mot de passe depuis Base64
                try {
                    String decodedPassword = new String(java.util.Base64.getDecoder().decode(savedPassword));
                    passwordField.setText(decodedPassword);
                    System.out.println("[INFO] Mot de passe chargé");
                } catch (Exception e) {
                    System.err.println("[ERREUR] Impossible de décoder le mot de passe: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Configure l'autocomplétion pour le champ email
     */
    private void setupEmailAutoComplete(String savedEmail) {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty() && !newValue.equals(savedEmail)) {
                // Si l'email sauvegardé commence par ce que l'utilisateur tape
                if (savedEmail.toLowerCase().startsWith(newValue.toLowerCase())) {
                    // Compléter automatiquement
                    emailField.setText(savedEmail);
                    // Sélectionner la partie auto-complétée
                    emailField.positionCaret(newValue.length());
                    emailField.selectEnd();
                    System.out.println("[INFO] Autocomplétion: " + newValue + " -> " + savedEmail);
                }
            }
        });
    }

    @FXML
    private void handleLogin() {
        String emailOrPhone = emailField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (emailOrPhone.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Email/Téléphone et mot de passe requis");
            return;
        }

        // Vérifier si c'est un email ou un téléphone
        boolean isEmail = emailOrPhone.contains("@");
        
        if (isEmail && !Validation.isValidEmail(emailOrPhone)) {
            errorLabel.setText("Email invalide");
            return;
        }
        
        if (!isEmail && !Validation.isValidTelephone(emailOrPhone)) {
            errorLabel.setText("Numéro de téléphone invalide");
            return;
        }

        if (!Validation.isValidPassword(password)) {
            errorLabel.setText("Mot de passe invalide (min 6 caractères)");
            return;
        }

        // Authentication - essayer avec email ou téléphone
        boolean loginSuccess = false;
        if (isEmail) {
            loginSuccess = authService.login(emailOrPhone, password);
        } else {
            // Connexion avec téléphone
            loginSuccess = authService.loginWithPhone(emailOrPhone, password);
        }
        
        if (loginSuccess) {
            // Sauvegarder l'email et le mot de passe si "Se souvenir de moi" est coché
            if (rememberMeCheckBox.isSelected()) {
                prefs.put(PREFS_KEY_EMAIL, emailOrPhone);
                // Encoder le mot de passe en Base64 avant de le sauvegarder
                String encodedPassword = java.util.Base64.getEncoder().encodeToString(password.getBytes());
                prefs.put(PREFS_KEY_PASSWORD, encodedPassword);
                prefs.putBoolean(PREFS_KEY_REMEMBER, true);
                System.out.println("[INFO] Email et mot de passe sauvegardés");
            } else {
                // Effacer les données sauvegardées si la case n'est pas cochée
                prefs.remove(PREFS_KEY_EMAIL);
                prefs.remove(PREFS_KEY_PASSWORD);
                prefs.putBoolean(PREFS_KEY_REMEMBER, false);
                System.out.println("[INFO] Données supprimées des préférences");
            }
            
            triggerLoginNotifications(authService.getCurrentUser());
            loadDashboard();
        } else {
            String message = authService.getLastErrorMessage();
            errorLabel.setText(message != null ? message : "Identifiants incorrects");
            passwordField.clear();
        }
    }

    private void triggerLoginNotifications(User user) {
        if (user == null) return;
        
        System.out.println("[DEBUG Login] Déclenchement notification pour: " + user.getNom() + " (ID: " + user.getId() + ")");
        
        // 1. Créer une notification en base de données
        com.example.mindjavafx.model.Notification notification = new com.example.mindjavafx.model.Notification(
            user.getId(),
            "Alerte de Connexion",
            "Vous venez de vous connecter au système.",
            "alert"
        );
        int notifId = notificationService.createNotification(notification);
        System.out.println("[DEBUG Login] Notification ID créé: " + notifId);
        
        // 2. Envoyer un email d'alerte
        System.out.println("[DEBUG] Déclenchement de l'alerte email pour: " + user.getNom());
        emailService.sendLoginAlertAsync(user.getNom(), user.getEmail());
    }

    private void loadDashboard() {
        try {
            String userRole = authService.getCurrentUser().getRole().getNom();
            
            // Route based on user role
            if ("User".equalsIgnoreCase(userRole)) {
                // Load professional user dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/professional-dashboard.fxml"));
                Parent root = loader.load();

                ProfessionalDashboardController controller = loader.getController();
                controller.setAuthService(authService);
                controller.setCurrentUser(authService.getCurrentUser());

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root, 1600, 900));
                stage.setTitle("MindAudit - Dashboard Utilisateur");
                stage.setResizable(true);
                stage.setMaximized(true);  // Maximiser la fenêtre
                stage.setAlwaysOnTop(true);  // Forcer au premier plan
                stage.show();
                stage.toFront();  // Mettre la fenêtre au premier plan
                stage.requestFocus();  // Donner le focus à la fenêtre
                // Désactiver "always on top" après 1 seconde
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> stage.setAlwaysOnTop(false));
                    } catch (Exception e) {}
                }).start();
            } else {
                // Load admin dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                Parent root = loader.load();

                DashboardController controller = loader.getController();
                controller.setAuthService(authService);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root, 1600, 900));
                stage.setTitle("MindAudit - Tableau de Bord Admin");
                stage.setResizable(true);
                stage.setMaximized(true);  // Maximiser la fenêtre
                stage.setAlwaysOnTop(true);  // Forcer au premier plan
                stage.show();
                stage.toFront();  // Mettre la fenêtre au premier plan
                stage.requestFocus();  // Donner le focus à la fenêtre
                // Désactiver "always on top" après 1 seconde
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> stage.setAlwaysOnTop(false));
                    } catch (Exception e) {}
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement du tableau de bord");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(root, 1050, 650);
            stage.setScene(scene);
            stage.setTitle("MindAudit - Inscription");
            stage.setResizable(true);
            stage.setMaximized(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de la page d'inscription");
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        try {
            System.out.println("[INFO] Redirection vers récupération mot de passe");
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/forgot-password-step1.fxml"));
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.setTitle("Mot de passe oublié - Étape 1");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de la page");
        }
    }
    
    @FXML
    private void handleGoogleLogin() {
        // Récupérer l'email écrit dans le champ
        String emailFromField = emailField.getText().trim();
        
        if (emailFromField.isEmpty()) {
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            errorLabel.setText("❌ Veuillez entrer votre email d'abord");
            return;
        }
        
        errorLabel.setStyle("-fx-text-fill: #4285f4;");
        errorLabel.setText("🔵 Connexion avec Google en cours...");
        
        // Exécuter dans un thread séparé pour ne pas bloquer l'interface
        new Thread(() -> {
            try {
                // Appeler le service Google OAuth avec l'email
                Map<String, String> googleUserInfo = GoogleOAuthService.authenticate(emailFromField);
                
                // Retourner au thread JavaFX pour mettre à jour l'interface
                javafx.application.Platform.runLater(() -> {
                    try {
                        if (googleUserInfo == null) {
                            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                            errorLabel.setText("❌ Échec de l'authentification Google");
                            return;
                        }
                        
                        // Afficher message de chargement
                        errorLabel.setStyle("-fx-text-fill: #4285f4;");
                        errorLabel.setText("✅ Authentification réussie! Ouverture du dashboard...");
                        
                        // Récupérer les informations de l'utilisateur Google
                        String googleEmail = googleUserInfo.get("email");
                        String googleName = googleUserInfo.get("name");
                        
                        System.out.println("[Google Login] Email: " + googleEmail);
                        System.out.println("[Google Login] Nom: " + googleName);
                        
                        // Vérifier si l'utilisateur existe déjà
                        User existingUser = userService.getUserByEmail(googleEmail);
                        
                        if (existingUser != null) {
                            // L'utilisateur existe, le connecter
                            System.out.println("[Google Login] Utilisateur existant trouvé");
                            authService.loginWithOAuth(existingUser);
                            triggerLoginNotifications(existingUser);
                            loadDashboard();
                        } else {
                            // Créer un nouvel utilisateur
                            System.out.println("[Google Login] Création d'un nouvel utilisateur");
                            
                            // Créer un rôle User par défaut (ID 2)
                            Role userRole = new Role(2, "User");
                            
                            // Créer l'utilisateur avec un mot de passe aléatoire (non utilisé pour OAuth)
                            User newUser = new User(googleName, googleEmail, "GOOGLE_OAUTH_USER", 25, userRole);
                            
                            // Ajouter l'utilisateur à la base de données
                            int userId = userService.addUser(newUser);
                            
                            if (userId > 0) {
                                newUser.setId(userId);
                                authService.loginWithOAuth(newUser);
                                triggerLoginNotifications(newUser);
                                loadDashboard();
                            } else {
                                errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                                errorLabel.setText("❌ Erreur lors de la création du compte");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[Google Login] Erreur: " + e.getMessage());
                        e.printStackTrace();
                        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                        errorLabel.setText("❌ Erreur lors de la connexion Google");
                    }
                });
                
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    System.err.println("[Google Login] Erreur: " + e.getMessage());
                    e.printStackTrace();
                    errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                    errorLabel.setText("❌ Erreur lors de la connexion Google");
                });
            }
        }).start();
    }
    
    @FXML
    private void handleFacebookLogin() {
        // Récupérer l'email écrit dans le champ
        String emailFromField = emailField.getText().trim();
        
        if (emailFromField.isEmpty()) {
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            errorLabel.setText("❌ Veuillez entrer votre email d'abord");
            return;
        }
        
        errorLabel.setStyle("-fx-text-fill: #1877f2;");
        errorLabel.setText("🔵 Connexion avec Facebook en cours...");
        
        // Exécuter dans un thread séparé pour ne pas bloquer l'interface
        new Thread(() -> {
            try {
                // Appeler le service Facebook OAuth avec l'email
                Map<String, String> facebookUserInfo = FacebookOAuthService.authenticate(emailFromField);
                
                // Retourner au thread JavaFX pour mettre à jour l'interface
                javafx.application.Platform.runLater(() -> {
                    try {
                        if (facebookUserInfo == null) {
                            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                            errorLabel.setText("❌ Échec de l'authentification Facebook");
                            return;
                        }
                        
                        // Afficher message de chargement
                        errorLabel.setStyle("-fx-text-fill: #1877f2;");
                        errorLabel.setText("✅ Authentification réussie! Ouverture du dashboard...");
                        
                        // Récupérer les informations de l'utilisateur Facebook
                        String facebookEmail = facebookUserInfo.get("email");
                        String facebookName = facebookUserInfo.get("name");
                        
                        // Si pas d'email, utiliser l'ID Facebook comme email
                        if (facebookEmail == null || facebookEmail.isEmpty()) {
                            facebookEmail = facebookUserInfo.get("id") + "@facebook.local";
                        }
                        
                        System.out.println("[Facebook Login] Email: " + facebookEmail);
                        System.out.println("[Facebook Login] Nom: " + facebookName);
                        
                        // Vérifier si l'utilisateur existe déjà
                        User existingUser = userService.getUserByEmail(facebookEmail);
                        
                        if (existingUser != null) {
                            // L'utilisateur existe, le connecter
                            System.out.println("[Facebook Login] Utilisateur existant trouvé");
                            authService.loginWithOAuth(existingUser);
                            triggerLoginNotifications(existingUser);
                            loadDashboard();
                        } else {
                            // Créer un nouvel utilisateur
                            System.out.println("[Facebook Login] Création d'un nouvel utilisateur");
                            
                            // Créer un rôle User par défaut (ID 2)
                            Role userRole = new Role(2, "User");
                            
                            // Créer l'utilisateur avec un mot de passe aléatoire (non utilisé pour OAuth)
                            User newUser = new User(facebookName, facebookEmail, "FACEBOOK_OAUTH_USER", 25, userRole);
                            
                            // Ajouter l'utilisateur à la base de données
                            int userId = userService.addUser(newUser);
                            
                            if (userId > 0) {
                                newUser.setId(userId);
                                authService.loginWithOAuth(newUser);
                                triggerLoginNotifications(newUser);
                                loadDashboard();
                            } else {
                                errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                                errorLabel.setText("❌ Erreur lors de la création du compte");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[Facebook Login] Erreur: " + e.getMessage());
                        e.printStackTrace();
                        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                        errorLabel.setText("❌ Erreur lors de la connexion Facebook");
                    }
                });
                
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    System.err.println("[Facebook Login] Erreur: " + e.getMessage());
                    e.printStackTrace();
                    errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                    errorLabel.setText("❌ Erreur lors de la connexion Facebook");
                });
            }
        }).start();
    }
}