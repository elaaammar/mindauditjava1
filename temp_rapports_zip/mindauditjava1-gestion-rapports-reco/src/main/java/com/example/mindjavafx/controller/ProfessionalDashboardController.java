package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuthenticationService;
import com.example.mindjavafx.service.NotificationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfessionalDashboardController {

    @FXML private TextField searchField;
    @FXML private Button notificationButton;
    @FXML private Label notificationBadge;
    @FXML private Circle profileImage;
    @FXML private Label userNameLabel;
    @FXML private Button logoutButton;
    
    @FXML private Button homeButton;
    @FXML private Button auditsButton;
    @FXML private Button analyticsButton;
    @FXML private Button reportsButton;
    @FXML private Button notificationsButton;
    @FXML private Button settingsButton;
    
    @FXML private StackPane contentArea;

    private AuthenticationService authService;
    private NotificationService notificationService;
    private User currentUser;

    @FXML
    public void initialize() {
        notificationService = new NotificationService();
        
        // Load home view by default
        showHome();
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
        this.currentUser = authService.getCurrentUser();
        
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNom());
            
            // Rendre la cloche verte pour tous les utilisateurs connectés
            notificationButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 5px; -fx-font-size: 20px; -fx-cursor: hand;");
            notificationBadge.setVisible(true);
            notificationBadge.setText("1");
            notificationBadge.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 2px 6px; -fx-font-size: 10px; -fx-font-weight: bold;");
            
            updateNotificationBadge();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userNameLabel.setText(user.getNom());
            
            // Rendre la cloche verte pour tous les utilisateurs connectés
            notificationButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 5px; -fx-font-size: 20px; -fx-cursor: hand;");
            notificationBadge.setVisible(true);
            notificationBadge.setText("1");
            notificationBadge.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 2px 6px; -fx-font-size: 10px; -fx-font-weight: bold;");
            
            updateNotificationBadge();
        }
    }

    @FXML
    private void showHome() {
        highlightButton(homeButton);
        loadSection("user-dashboard-home.fxml");
    }

    @FXML
    private void showMyAudits() {
        highlightButton(auditsButton);
        loadSection("my-audits.fxml");
    }

    @FXML
    private void showAnalytics() {
        highlightButton(analyticsButton);
        loadSection("analytics.fxml");
    }

    @FXML
    private void showReports() {
        highlightButton(reportsButton);
        loadSection("reports.fxml");
    }

    @FXML
    private void showNotifications() {
        highlightButton(notificationsButton);
        loadSection("notifications.fxml");
    }

    @FXML
    private void showSettings() {
        highlightButton(settingsButton);
        loadSection("profile.fxml");
    }

    private void loadSection(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent section = loader.load();
            
            // Pass current user to the loaded controller if it has a setCurrentUser method
            Object controller = loader.getController();
            if (controller != null && currentUser != null) {
                try {
                    // Try to pass current user
                    try {
                        controller.getClass().getMethod("setCurrentUser", User.class).invoke(controller, currentUser);
                    } catch (Exception e) {}
                    
                    // Try to pass dashboard controller for navigation
                    if (controller instanceof UserDashboardHomeController) {
                        ((UserDashboardHomeController) controller).setDashboardController(this);
                    }
                } catch (Exception e) {
                    // Controller doesn't have methods, that's okay
                }
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(section);
            
        } catch (IOException e) {
            System.err.println("Error loading section: " + fxmlFile);
            e.printStackTrace();
            showErrorDialog("Erreur", "Impossible de charger la section: " + fxmlFile);
        }
    }

    private void highlightButton(Button activeButton) {
        // Reset all buttons
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12px 15px; -fx-font-size: 14px; -fx-cursor: hand;";
        String activeStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12px 15px; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5px; -fx-font-weight: bold;";
        
        Button[] buttons = {homeButton, auditsButton, analyticsButton, reportsButton, notificationsButton, settingsButton};
        for (Button btn : buttons) {
            if (btn != null) btn.setStyle(defaultStyle);
        }
        
        // Highlight active button
        if (activeButton != null) {
            activeButton.setStyle(activeStyle);
        }
    }

    public void updateNotificationBadge() {
        if (currentUser != null && notificationService != null) {
            int unreadCount = notificationService.getUnreadCount(currentUser.getId());
            System.out.println("[DEBUG Dashboard] Notifications non lues pour " + currentUser.getNom() + ": " + unreadCount);
            
            if (unreadCount > 0) {
                notificationBadge.setText(String.valueOf(unreadCount));
                notificationBadge.setVisible(true);
                // Rendre l'alerte verte TRÈS visible
                notificationBadge.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 2px 6px; -fx-font-size: 10px; -fx-font-weight: bold;");
                notificationButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 20px; -fx-cursor: hand; -fx-font-size: 20px;");
            } else {
                notificationBadge.setVisible(false);
                notificationButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 20px;");
            }
        }
    }

    @FXML
    private void showNotificationDropdown() {
        if (currentUser == null || notificationService == null) return;
        
        java.util.List<com.example.mindjavafx.model.Notification> notifs = notificationService.getNotificationsByUserId(currentUser.getId());
        StringBuilder content = new StringBuilder();
        
        if (notifs.isEmpty()) {
            content.append("Une notification sera envoyée par email.");
        } else {
            for (com.example.mindjavafx.model.Notification n : notifs) {
                content.append(n.isRead() ? "✓ " : "🔔 ");
                content.append(n.getTitle()).append(" : ").append(n.getMessage()).append("\n");
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Notifications de " + currentUser.getNom());
        alert.setContentText(content.toString());
        
        // Marquer comme lues après affichage
        notificationService.markAllAsRead(currentUser.getId());
        updateNotificationBadge();
        
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Confirmer la déconnexion");
        alert.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Load login screen
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                    Parent root = loader.load();
                    
                    Stage stage = (Stage) logoutButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("MindAudit - Connexion");
                    
                } catch (IOException e) {
                    System.err.println("Error loading login screen: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
