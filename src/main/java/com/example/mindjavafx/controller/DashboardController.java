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
    private Button dashboardButton;

    @FXML
    private Button statisticsButton;

    @FXML
    private Button settingsButton;
    
    @FXML private Button notificationButton;
    @FXML private Label notificationBadge;

    private AuthenticationService authService;
    private UserService userService;
    private NotificationService notificationService;

    @FXML
    public void initialize() {
        userService = new UserService();
        notificationService = new NotificationService();
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
        updateWelcomeLabel();
        configureMenuBasedOnRole();
        updateNotificationBadge();
        
        // On attend que JavaFX soit prêt pour charger le contenu par défaut
        javafx.application.Platform.runLater(() -> {
            showStatistics();
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
    public void showStatistics() {
        System.out.println("[NAV] Navigation vers Statistiques...");
        loadContent("/fxml/statistics.fxml");
        updateActiveButton(statisticsButton);
    }
    
    @FXML
    public void showDashboard() {
        System.out.println("[NAV] Navigation vers Dashboard Home...");
        loadContent("/fxml/dashboard-home.fxml");
        updateActiveButton(dashboardButton);
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
    public void showProfile() {
        System.out.println("[NAV] Navigation vers Profil/Paramètres...");
        loadContent("/fxml/profile.fxml");
        updateActiveButton(settingsButton);
    }

    private void updateActiveButton(Button activeButton) {
        Button[] buttons = {dashboardButton, userManagementButton, rolePermissionButton, statisticsButton, settingsButton};
        for (Button btn : buttons) {
            if (btn != null) {
                if (btn == activeButton) {
                    btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 15 25; -fx-font-size: 13; -fx-cursor: hand; -fx-font-weight: bold;");
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
        try {
            if (contentArea == null) return;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) return;
            
            Parent content = loader.load();

            // Passer les services au contrôleur si nécessaire
            Object controller = loader.getController();
            
            if (controllerConsumer != null) {
                controllerConsumer.accept(controller);
            }

            if (controller instanceof UserManagementController) {
                ((UserManagementController) controller).setAuthService(authService);
            } else if (controller instanceof ProfileController) {
                ((ProfileController) controller).setAuthService(authService);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
            
            // Remettre le style propre par défaut
            contentArea.setStyle("-fx-background-color: #ecf0f1;");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            content.append("🛡️ Alerte Sécurité : Un e-mail de notification a été envoyé à eleammar21@gmail.com pour confirmer la connexion au tableau de bord.");
        } else {
            for (Notification n : notifs) {
                content.append(n.isRead() ? "✓ " : "🔔 ");
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
