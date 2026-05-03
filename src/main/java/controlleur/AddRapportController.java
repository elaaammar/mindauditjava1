package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Rapport;
import services.RapportService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AddRapportController {

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private DatePicker dpDate;

    @FXML
    private Label lblStatus;

    @FXML
    private TextArea taDescription;

    @FXML
    private TextField tfTitle;

    @FXML
    private TextField tfDuration;

    private final RapportService rapportService = new RapportService();

    @FXML
    public void initialize() {
        // Step 2.1: Pre-populate the ComboBox with categories
        cbType.setItems(FXCollections.observableArrayList(
                "Security",
                "Quality",
                "Technical",
                "Compliance"
        ));
    }

    @FXML
    void handleSaveAction(ActionEvent event) {
        String title = tfTitle.getText().trim();
        String description = taDescription.getText().trim();
        String type = cbType.getValue();
        
        // Step 2.2: Get date from DatePicker
        String date = "";
        if (dpDate.getValue() != null) {
            date = dpDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        // Reset styles and status
        lblStatus.setText("");
        tfTitle.setStyle("-fx-background-radius: 5px; -fx-padding: 10px;");
        taDescription.setStyle("-fx-background-radius: 5px;");
        cbType.setStyle("-fx-background-radius: 5px; -fx-padding: 5px;");
        dpDate.setStyle("-fx-padding: 5px;");
        
        boolean hasError = false;

        // Step 2.3: Validation (Controle de Saisie)
        if (title.isEmpty() || title.length() < 5) {
            tfTitle.setStyle("-fx-background-radius: 5px; -fx-padding: 10px; -fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 5px;");
            lblStatus.setText("Error: Title must be at least 5 characters.");
            hasError = true;
        } else if (description.isEmpty() || description.length() < 20) {
            taDescription.setStyle("-fx-background-radius: 5px; -fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 5px;");
            lblStatus.setText("Error: Description must be at least 20 characters.");
            hasError = true;
        } else if (type == null) {
            cbType.setStyle("-fx-background-radius: 5px; -fx-padding: 5px; -fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 5px;");
            lblStatus.setText("Error: Please select a Category.");
            hasError = true;
        } else if (date.isEmpty()) {
            dpDate.setStyle("-fx-padding: 5px; -fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 5px;");
            lblStatus.setText("Error: Please select an Audit Date.");
            hasError = true;
        }

        int duration = 0;
        try {
            duration = Integer.parseInt(tfDuration.getText().trim());
            if (duration <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tfDuration.setStyle("-fx-background-radius: 5px; -fx-padding: 10px; -fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 5px;");
            lblStatus.setText("Error: Please enter a valid duration in minutes.");
            hasError = true;
        }

        if (hasError) {
            lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            return;
        }

        try {
            // Step 2.4: Service Call
            Rapport r = new Rapport(title, description, date, type);
            r.setDuration(duration);
            rapportService.add(r);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Rapport created successfully!");
            // Clear Form
            tfTitle.clear();
            taDescription.clear();
            tfDuration.clear();
            cbType.getSelectionModel().clearSelection();
            dpDate.setValue(null);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not create report: " + e.getMessage());
        }
    }



    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
