package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuthenticationService;
import com.example.mindjavafx.service.NotificationService;
import com.example.mindjavafx.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class AuditorDashboardController {

    @FXML private Button logoutButton;
    @FXML private Button auditsButton;
    @FXML private Button reportsButton;
    @FXML private Button newReportButton;
    @FXML private Button newQuestionButton;
    // Notifications and Settings removed
    
    @FXML private StackPane contentArea;

    private AuthenticationService authService;
    private NotificationService notificationService;
    private UserService userService;
    private User currentUser;

    @FXML
    public void initialize() {
        notificationService = new NotificationService();
        userService = new UserService();
        
        // Load default view
        showAuditManagement();
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
        this.currentUser = authService.getCurrentUser();
        
        // No profile updates for Auditor
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        // No profile updates for Auditor
    }

    // Removed showHome, showNotifications, showSettings

    @FXML
    public void showAuditManagement() {
        loadSection("audit-list-rapports.fxml");
        highlightButton(auditsButton);
    }

    @FXML
    public void showAddRapport() {
        loadSection("audit-add-rapport.fxml");
        highlightButton(newReportButton);
    }

    @FXML
    public void showRapportManagement() {
        loadSection("rapports_reco/main-view.fxml");
        highlightButton(reportsButton);
    }

    @FXML
    public void showAddQuestion() {
        loadSection("audit-add-question.fxml");
        highlightButton(newQuestionButton);
    }

    private void loadSection(String fxmlFile) {
        loadSection(fxmlFile, null);
    }

    private void loadSection(String fxmlFile, java.util.function.Consumer<Object> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent section = loader.load();
            
            // Pass current user to the loaded controller if it has a setCurrentUser method
            Object controller = loader.getController();
            
            if (controllerConsumer != null) {
                controllerConsumer.accept(controller);
            }

            if (controller != null && currentUser != null) {
                try {
                    // Try to pass current user
                    try {
                        controller.getClass().getMethod("setCurrentUser", User.class).invoke(controller, currentUser);
                    } catch (Exception e) {}
                    
                    // Try to pass dashboard controller for navigation
                    if (controller instanceof UserDashboardHomeController) {
                        ((UserDashboardHomeController) controller).setDashboardController(this);
                    } else if (controller instanceof ProfileController) {
                        ((ProfileController) controller).setDashboardController(this);
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

    private void highlightButton(Button button) {
        String mainInactive = "-fx-background-color: transparent; -fx-text-fill: #E0E1DD; -fx-alignment: CENTER_LEFT; -fx-padding: 15px 25px; -fx-font-size: 13px; -fx-cursor: hand; -fx-font-family: 'Consolas', monospace;";
        String mainActive = "-fx-background-color: rgba(0, 229, 255, 0.15); -fx-text-fill: #00E5FF; -fx-alignment: CENTER_LEFT; -fx-padding: 15px 25px; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-left-width: 4px; -fx-border-color: transparent transparent transparent #00E5FF; -fx-font-weight: bold; -fx-font-family: 'Consolas', monospace;";
        
        String subInactive = "-fx-background-color: transparent; -fx-text-fill: #778DA9; -fx-alignment: CENTER_LEFT; -fx-padding: 10px 45px; -fx-font-size: 12px; -fx-cursor: hand; -fx-font-family: 'Consolas', monospace;";
        String subActive = "-fx-background-color: rgba(0, 229, 255, 0.05); -fx-text-fill: #00E5FF; -fx-alignment: CENTER_LEFT; -fx-padding: 10px 45px; -fx-font-size: 12px; -fx-cursor: hand; -fx-border-left-width: 4px; -fx-border-color: transparent transparent transparent #00E5FF; -fx-font-weight: bold; -fx-font-family: 'Consolas', monospace;";
        
        auditsButton.setStyle(button == auditsButton ? mainActive : mainInactive);
        reportsButton.setStyle(button == reportsButton ? mainActive : mainInactive);
        
        newReportButton.setStyle(button == newReportButton ? subActive : subInactive);
        newQuestionButton.setStyle(button == newQuestionButton ? subActive : subInactive);
    }

    // Removed updateNotificationBadge, showNotificationDropdown, handleSearch

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

    // Removed refreshUserInfo

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
