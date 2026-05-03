package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.QuestionAudit;
import services.QuestionAuditService;

import java.io.IOException;

public class AddQuestionController {

    @FXML
    private Button btnAdd;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private Label lblStatus;

    @FXML
    private TextField tfContent;

    @FXML
    private TextField tfBonneReponse;

    @FXML
    private TextField tfTimeLimit;

    private final QuestionAuditService questionService = new QuestionAuditService();
    private QuestionAudit questionToEdit; // To track if we are in edit mode

    @FXML
    public void initialize() {
        // Step 4.1: Pre-populate the ComboBox with some types
        cbType.setItems(FXCollections.observableArrayList(
                "Security",
                "Quality",
                "Technical",
                "Compliance"
        ));
    }

    public void initData(QuestionAudit question) {
        this.questionToEdit = question;
        tfContent.setText(question.getContent());
        cbType.setValue(question.getType());
        if (question.getBonneReponse() != null) {
            tfBonneReponse.setText(question.getBonneReponse());
        }
        tfTimeLimit.setText(String.valueOf(question.getTimeLimit()));
        btnAdd.setText("Update Question");
    }

    @FXML
    void handleAddAction(ActionEvent event) {
        String content = tfContent.getText().trim();
        String type = cbType.getValue();

        // Clear previous styles/status
        lblStatus.setText("");
        tfContent.setStyle("-fx-background-radius: 5px; -fx-padding: 10px;");
        cbType.setStyle("-fx-background-radius: 5px; -fx-padding: 5px;");

        // Advanced Controle de Saisie
        boolean hasError = false;

        if (content.isEmpty() || content.length() < 10) {
            tfContent.setStyle("-fx-background-radius: 5px; -fx-padding: 10px; -fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 5px;");
            lblStatus.setText("Error: Question must be at least 10 characters long.");
            lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            hasError = true;
        }

        if (type == null) {
            cbType.setStyle("-fx-background-radius: 5px; -fx-padding: 5px; -fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 5px;");
            if (!hasError) {
                lblStatus.setText("Error: Please select a question type.");
                lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
            hasError = true;
        }

        int timeLimit = 0;
        try {
            String tl = tfTimeLimit.getText().trim();
            if (!tl.isEmpty()) {
                timeLimit = Integer.parseInt(tl);
            }
        } catch (NumberFormatException e) {
            tfTimeLimit.setStyle("-fx-background-radius: 5px; -fx-padding: 10px; -fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 5px;");
            lblStatus.setText("Error: Time limit must be a number of seconds.");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        try {
            if (questionToEdit == null) {
                // ADD MODE
                QuestionAudit q = new QuestionAudit(content, type);
                q.setBonneReponse(tfBonneReponse.getText().trim());
                q.setTimeLimit(timeLimit);
                questionService.add(q);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Question added successfully!");
            } else {
                // EDIT MODE
                questionToEdit.setContent(content);
                questionToEdit.setType(type);
                questionToEdit.setBonneReponse(tfBonneReponse.getText().trim());
                questionToEdit.setTimeLimit(timeLimit);
                questionService.update(questionToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Question updated successfully!");
            }
            // Clear form
            tfContent.clear();
            cbType.getSelectionModel().clearSelection();
            tfBonneReponse.clear();
            tfTimeLimit.clear();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Operation failed: " + e.getMessage());
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
