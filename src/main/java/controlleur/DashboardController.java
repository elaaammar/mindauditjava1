package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label lblViewTitle;

    @FXML
    private Label lblUserRole;

    @FXML
    private Button btnAddRapport;

    @FXML
    private Button btnAddQuestion;

    private String currentRole = "Administrator";

    public void setUserRole(String role) {
        this.currentRole = role.equals("ADMIN") ? "Administrator" : role;
        lblUserRole.setText(this.currentRole);
        
        // Role-based access control for sidebar
        if ("Consultant".equals(this.currentRole) || "USER".equals(this.currentRole)) {
            btnAddRapport.setVisible(false);
            btnAddRapport.setManaged(false);
            btnAddQuestion.setVisible(false);
            btnAddQuestion.setManaged(false);
        }
        
        // Reload reports so role takes effect
        showReports();
    }

    @FXML
    public void initialize() {
        // Initially do nothing until role is set by LoginController
    }

    @FXML
    void showReports() {
        loadView("/gui/ListRapports.fxml", "Dashboard Overview");
    }

    @FXML
    void showAddRapport() {
        loadView("/gui/AddRapport.fxml", "Create New Audit Report");
    }

    @FXML
    void showAddQuestion() {
        loadView("/gui/AddQuestion.fxml", "Add Audit Question");
    }

    @FXML
    void showStatistics() {
        loadView("/gui/Statistics.fxml", "Statistics & Analytics");
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Pass the role to child controllers if they need it
            if (fxmlPath.equals("/gui/ListRapports.fxml")) {
                ListRapportsController lrc = loader.getController();
                lrc.setRole(this.currentRole.equals("Administrator") ? "ADMIN" : "USER");
            }
            
            // Clear current content and add new view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            // Update title
            lblViewTitle.setText(title);
            
        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
            
            // Using setRoot instead of setScene keeps the Stage/Window size exactly as it is
            stage.getScene().setRoot(root);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
