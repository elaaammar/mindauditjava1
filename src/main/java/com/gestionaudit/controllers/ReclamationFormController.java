package com.gestionaudit.controllers;

import com.gestionaudit.models.Reclamation;
import com.gestionaudit.services.ReclamationService;
import com.gestionaudit.utils.BadWordFilter;
import com.gestionaudit.utils.DialogUtils;
import com.gestionaudit.utils.QRCodeGenerator;
import com.google.zxing.WriterException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ReclamationFormController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> prioriteCombo;
    @FXML private TextField categorieField;
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private ImageView qrImageView;
    @FXML private Button submitButton;

    @FXML private Label errorTitre;
    @FXML private Label errorDescription;
    @FXML private Label errorEmail;

    private Reclamation reclamation;
    private ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        prioriteCombo.getItems().addAll(Reclamation.PRIORITE_BASSE, Reclamation.PRIORITE_MOYENNE, Reclamation.PRIORITE_HAUTE);
        prioriteCombo.setValue(Reclamation.PRIORITE_MOYENNE);
    }

    public void setReclamation(Reclamation r) {
        this.reclamation = r;
        if (r != null) {
            titreField.setText(r.getTitre());
            descriptionArea.setText(r.getDescription());
            prioriteCombo.setValue(r.getPriorite());
            categorieField.setText(r.getCategorie());
            nomField.setText(r.getNom());
            emailField.setText(r.getEmail());
            telephoneField.setText(r.getTelephone());
            submitButton.setText("Modifier");
        }
    }

    @FXML
    private void handleSubmit() {
        if (!validateInput()) return;

        if (reclamation == null) {
            reclamation = new Reclamation();
        }

        reclamation.setTitre(titreField.getText());
        reclamation.setDescription(descriptionArea.getText());
        reclamation.setPriorite(prioriteCombo.getValue());
        reclamation.setCategorie(categorieField.getText());
        reclamation.setNom(nomField.getText());
        reclamation.setEmail(emailField.getText());
        reclamation.setTelephone(telephoneField.getText());

        try {
            if (reclamation.getId() == 0) {
                reclamationService.add(reclamation);
                DialogUtils.showInfo("Succès", "Réclamation ajoutée avec succès.");
            } else {
                reclamationService.update(reclamation);
                DialogUtils.showInfo("Succès", "Réclamation mise à jour.");
            }
            closeWindow();
        } catch (SQLException e) {
            DialogUtils.showError("Database Error", "Could not save reclamation: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleGenerateQR() {
        if (titreField.getText().isEmpty()) {
            DialogUtils.showError("Erreur", "Veuillez saisir au moins un titre pour générer le QR Code.");
            return;
        }

        String content = "Réclamation: " + titreField.getText() + 
                         "\nDescription: " + descriptionArea.getText() + 
                         "\nClient: " + nomField.getText() + 
                         "\nEmail: " + emailField.getText();

        try {
            qrImageView.setImage(QRCodeGenerator.generateQRCodeImage(content, 150, 150));
        } catch (WriterException e) {
            DialogUtils.showError("Erreur QR", "Impossible de générer le QR Code: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Reset errors
        hideError(errorTitre);
        hideError(errorDescription);
        hideError(errorEmail);

        // Required fields check
        if (titreField.getText().trim().isEmpty()) {
            showError(errorTitre, "Le titre est obligatoire.");
            isValid = false;
        } else if (BadWordFilter.containsBadWords(titreField.getText())) {
            showError(errorTitre, "Le titre contient des mots inappropriés.");
            isValid = false;
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            showError(errorDescription, "La description est obligatoire.");
            isValid = false;
        } else if (BadWordFilter.containsBadWords(descriptionArea.getText())) {
            showError(errorDescription, "La description contient des mots inappropriés.");
            isValid = false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showError(errorEmail, "L'email est obligatoire.");
            isValid = false;
        } else if (!emailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showError(errorEmail, "Format d'email invalide.");
            isValid = false;
        }

        return isValid;
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void hideError(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    private void closeWindow() {
        ((Stage) titreField.getScene().getWindow()).close();
    }
}
