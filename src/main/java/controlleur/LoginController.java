package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private VBox adminCard;

    @FXML
    private VBox userCard;

    @FXML
    void handleAdminSelection() {
        navigateToDashboard("ADMIN");
    }

    @FXML
    void handleUserSelection() {
        navigateToDashboard("USER");
    }

    private void navigateToDashboard(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/DashboardTemplate.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setUserRole(role.equals("ADMIN") ? "Administrator" : "Consultant");

            Stage stage = (Stage) adminCard.getScene().getWindow();
            
            // setRoot ensures the Stage size remains identical to current state
            stage.getScene().setRoot(root);
            
        } catch (IOException e) {
            System.err.println("Error loading dashboard template: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
