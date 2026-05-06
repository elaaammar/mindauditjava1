package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuthenticationService;
import com.example.mindjavafx.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import com.example.mindjavafx.service.NotificationService;
import javafx.scene.control.Alert;
import java.util.List;
import com.example.mindjavafx.model.Notification;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label userWelcomeLabel;
    
    @FXML
    private javafx.scene.control.TextField globalSearchField;

    @FXML
    private VBox sidebarVBox;
    
    @FXML
    private Button userManagementButton;
    
    @FXML
    private Button rolePermissionButton;
    
    @FXML
    private Button rapportManagementButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button statisticsButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button auditAnalyticsButton;

    @FXML private Button reclamationManagementButton;
    @FXML private Button messagesButton;

    @FXML private Button notificationButton;
    @FXML private Label notificationBadge;

    private AuthenticationService authService;
    private UserService userService;
    private NotificationService notificationService;

    @FXML
    public void initialize() {
        userService = new UserService();
        notificationService = new NotificationService();
        
        // Nettoyage automatique des "bars" d'alerte de connexion au démarrage
        try {
            notificationService.deleteConnectionAlerts();
        } catch (Exception e) {
            System.err.println("[Dashboard] Erreur lors du nettoyage auto: " + e.getMessage());
        }
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
        updateWelcomeLabel();
        configureMenuBasedOnRole();
        updateNotificationBadge();
        
        // Forcer le badge +1 rouge pour l'alerte de connexion email
        if (notificationBadge != null) {
            notificationBadge.setText("+1");
            notificationBadge.setVisible(true);
            notificationBadge.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 2px 6px; -fx-font-size: 10px; -fx-font-weight: bold;");
        }
        
        // On attend que JavaFX soit prêt pour charger le contenu par défaut
        javafx.application.Platform.runLater(() -> {
            showDashboard();
        });
    }

    private void updateWelcomeLabel() {
        if (authService.isLoggedIn()) {
            User user = authService.getCurrentUser();
            userWelcomeLabel.setText("Bienvenue, " + user.getNom() + " (" + user.getRole().getNom() + ")");
        }
    }
    
    private void configureMenuBasedOnRole() {
        if (authService.isLoggedIn()) {
            User user = authService.getCurrentUser();
            String role = user.getRole().getNom();
            
            System.out.println("[DEBUG] Configuring menu for role: " + role);
            
            // Hide admin-only menus for non-admin users
            if (!"Admin".equals(role)) {
                System.out.println("[DEBUG] Hiding admin menus for non-admin user");
                if (userManagementButton != null) {
                    userManagementButton.setVisible(false);
                    userManagementButton.setManaged(false);
                }
                if (rolePermissionButton != null) {
                    rolePermissionButton.setVisible(false);
                    rolePermissionButton.setManaged(false);
                }
                if (reclamationManagementButton != null) {
                    reclamationManagementButton.setVisible(false);
                    reclamationManagementButton.setManaged(false);
                }
                if (messagesButton != null) {
                    messagesButton.setVisible(false);
                    messagesButton.setManaged(false);
                }
            } else {
                System.out.println("[DEBUG] Showing all menus for admin user");
            }
        }
    }
    
    @FXML
    private void onMouseEntered(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(button.getStyle() + "-fx-background-color: #34495e;");
    }
    
    @FXML
    private void onMouseExited(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(button.getStyle().replace("-fx-background-color: #34495e;", "-fx-background-color: transparent;"));
    }

    @FXML
    public void showSettings() {
        System.out.println("[NAV] Navigation vers Settings...");
        loadContent("/fxml/settings.fxml");
        updateActiveButton(settingsButton);
    }

    @FXML
    public void showAuditAnalytics() {
        System.out.println("[NAV] Navigation vers Performance Analytics...");
        loadContent("/fxml/audit-statistics.fxml");
        updateActiveButton(auditAnalyticsButton);
    }
    
    @FXML
    public void showStatistics() {
        System.out.println("[NAV] Navigation vers Statistiques...");
        loadContent("/fxml/statistics.fxml");
        updateActiveButton(statisticsButton);
    }
    
    @FXML
    public void showDashboard() {
        System.out.println("[NAV] Navigation vers Admin Home...");
        loadContent("/fxml/admin-home.fxml", (controller) -> {
            if (controller instanceof AdminDashboardHomeController) {
                ((AdminDashboardHomeController) controller).setDashboardController(this);
            } else if (controller instanceof AdminHomeController) {
                ((AdminHomeController) controller).setDashboardController(this);
            }
        });
        updateActiveButton(dashboardButton);
    }

    public User getCurrentUser() {
        return authService != null ? authService.getCurrentUser() : null;
    }

    @FXML
    public void showUserManagement() {
        System.out.println("[NAV] Navigation vers Gestion Utilisateurs...");
        loadContent("/fxml/user-management.fxml");
        updateActiveButton(userManagementButton);
    }

    @FXML
    public void showRolePermission() {
        System.out.println("[NAV] Navigation vers Rôles & Permissions...");
        loadContent("/fxml/role-permission.fxml");
        updateActiveButton(rolePermissionButton);
    }
    
    @FXML
    public void showRapportManagement() {
        System.out.println("[NAV] Navigation vers Gestion Rapports...");
        loadContent("/fxml/rapports_reco/main-view.fxml");
        updateActiveButton(rapportManagementButton);
    }

    @FXML
    public void showReclamationManagement() {
        System.out.println("[NAV] Navigation vers Gestion Réclamations...");
        loadContent("/views/admin_main.fxml");
        updateActiveButton(reclamationManagementButton);
    }

    @FXML
    public void showMessages() {
        System.out.println("[NAV] Navigation vers Messagerie...");
        loadContent("/views/admin_messages.fxml");
        updateActiveButton(messagesButton);
    }

    @FXML
    public void showProfile() {
        System.out.println("[NAV] Navigation vers Profil/Paramètres...");
        loadContent("/fxml/profile.fxml");
        updateActiveButton(settingsButton);
    }

    private void updateActiveButton(Button activeButton) {
        Button[] buttons = {dashboardButton, userManagementButton, rolePermissionButton, statisticsButton, settingsButton, 
                           auditAnalyticsButton, rapportManagementButton, reclamationManagementButton, messagesButton};
        for (Button btn : buttons) {
            if (btn != null) {
                if (btn == activeButton) {
                    btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 15 25; -fx-font-size: 13; -fx-cursor: hand; -fx-font-weight: bold;");
                } else {
                    btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 15 25; -fx-font-size: 13; -fx-cursor: hand;");
                }
            }
        }
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        // Return to login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            javafx.stage.Stage stage = (javafx.stage.Stage) mainBorderPane.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 1050, 650));
            stage.setTitle("MindAudit - Connexion");
            stage.setResizable(true);
            stage.setMaximized(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlPath) {
        loadContent(fxmlPath, null);
    }

    private void loadContent(String fxmlPath, java.util.function.Consumer<Object> controllerConsumer) {
        // Afficher un indicateur de chargement temporaire
        if (contentArea != null) {
            javafx.application.Platform.runLater(() -> {
                contentArea.getChildren().clear();
                javafx.scene.control.ProgressIndicator pi = new javafx.scene.control.ProgressIndicator();
                pi.setMaxSize(50, 50);
                contentArea.getChildren().add(pi);
            });
        }

        javafx.application.Platform.runLater(() -> {
            try {
                System.out.println("[LOAD] Chargement: " + fxmlPath);
                java.net.URL resourceUrl = getClass().getResource(fxmlPath);
                
                if (resourceUrl == null) {
                    throw new IOException("FXML introuvable: " + fxmlPath);
                }

                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Parent content = loader.load();
                Object controller = loader.getController();

                if (controllerConsumer != null) {
                    controllerConsumer.accept(controller);
                }

                // Injections spécifiques
                if (controller instanceof UserManagementController) {
                    ((UserManagementController) controller).setAuthService(authService);
                } else if (controller instanceof ProfileController) {
                    ((ProfileController) controller).setAuthService(authService);
                }

                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
                System.out.println("[LOAD] Affiché: " + fxmlPath);

            } catch (Exception e) {
                System.err.println("[LOAD] Erreur fatale: " + e.getMessage());
                e.printStackTrace();
                contentArea.getChildren().clear();
                javafx.scene.control.Label err = new javafx.scene.control.Label("Erreur de chargement: " + e.getMessage());
                err.setStyle("-fx-text-fill: red; -fx-padding: 20;");
                contentArea.getChildren().add(err);
            }
        });
    }

    public void updateNotificationBadge() {
        if (authService != null && authService.isLoggedIn() && notificationService != null) {
            User currentUser = authService.getCurrentUser();
            int unreadCount = notificationService.getUnreadCount(currentUser.getId());
            
            if (unreadCount > 0) {
                if (notificationBadge != null) {
                    notificationBadge.setText("+1");
                    notificationBadge.setVisible(true);
                    notificationBadge.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 2px 6px; -fx-font-size: 10px; -fx-font-weight: bold;");
                }
                if (notificationButton != null) {
                    notificationButton.setStyle("-fx-text-fill: #e74c3c; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 20px;");
                }
            } else {
                if (notificationBadge != null) notificationBadge.setVisible(false);
                if (notificationButton != null) {
                    notificationButton.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 20px;");
                }
            }
        }
    }

    @FXML
    private void showNotificationDropdown() {
        User currentUser = authService.getCurrentUser();
        List<Notification> notifs = notificationService.getNotificationsByUserId(currentUser.getId());
        
        StringBuilder content = new StringBuilder();
        if (notifs.isEmpty()) {
            content.append("� Alerte : E-mail de sécurité envoyé à l'administrateur (eleammar21@gmail.com).");
        } else {
            for (Notification n : notifs) {
                content.append(n.isRead() ? "✓ " : "� ");
                content.append(n.getTitle()).append("\n");
                content.append("   ").append(n.getMessage()).append("\n\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications de " + currentUser.getNom());
        alert.setHeaderText("Vos alertes récentes");
        alert.setContentText(content.toString());
        alert.show();

        // Mark as read
        notificationService.markAllAsRead(currentUser.getId());
        updateNotificationBadge();
    }
    
    @FXML
    private void handleGlobalSearch() {
        String searchQuery = globalSearchField.getText().trim();
        
        if (searchQuery.isEmpty()) {
            showAlert("Recherche", "Veuillez entrer un terme de recherche", Alert.AlertType.WARNING);
            return;
        }
        
        System.out.println("[SEARCH] Recherche globale: " + searchQuery);
        
        try {
            // Rechercher dans les utilisateurs
            List<User> users = userService.searchByName(searchQuery);
            
            loadContent("/fxml/user-search-results.fxml", (controller) -> {
                if (controller instanceof UserSearchResultsController) {
                    ((UserSearchResultsController) controller).setResults(searchQuery, users);
                }
            });
            
            updateActiveButton(null); // Deselect all menu buttons
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
