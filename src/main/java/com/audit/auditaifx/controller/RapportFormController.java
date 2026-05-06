package com.audit.auditaifx.controller;

import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.StatutRapport;
import com.audit.auditaifx.service.RapportService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RapportFormController {

    @FXML
    private TextField txtTitre;
    @FXML
    private TextField txtAuditeur;
    @FXML
    private TextField txtEntite;
    @FXML
    private TextArea txtDescription;
    @FXML
    private TextField txtScore;
    @FXML
    private DatePicker dpDate;
    @FXML
    private Label lblErreurTitre;
    @FXML
    private Label lblErreurAuditeur;
    @FXML
    private Label lblErreurEntite;

    private RapportService service;
    private RapportAudit rapport;
    private Runnable onSave;

    public void setService(RapportService service) {
        this.service = service;
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    public void setRapport(RapportAudit rapport) {
        this.rapport = rapport;
        if (rapport != null) {
            // Mode modification — pré-remplir les champs
            txtTitre.setText(rapport.getTitre());
            txtAuditeur.setText(rapport.getAuditeur());
            if (rapport.getDateCreation() != null) dpDate.setValue(rapport.getDateCreation());
            txtDescription.setText(rapport.getDescription());
            
            if (rapport.getScoreAudit() != null && !rapport.getScoreAudit().isEmpty()) {
                txtScore.setText(rapport.getScoreAudit());
            }
        }
    }

    @FXML
    public void initialize() {
        if (dpDate != null) dpDate.setValue(java.time.LocalDate.now());

        // Effacer erreur en temps réel
        txtTitre.textProperty().addListener((o, a, n) -> lblErreurTitre.setText(""));
        txtAuditeur.textProperty().addListener((o, a, n) -> lblErreurAuditeur.setText(""));
    }

    @FXML
    public void sauvegarder() {
        if (!valider())
            return;

        if (rapport == null) {
            // Création
            RapportAudit nouveau = new RapportAudit();
            nouveau.setTitre(txtTitre.getText().trim());
            nouveau.setAuditeur(txtAuditeur.getText().trim()); // Catégorie
            nouveau.setDateCreation(dpDate.getValue());
            nouveau.setEntiteAuditee("—"); // Placeholder since field removed
            
            String desc = txtDescription.getText();
            nouveau.setDescription(desc != null ? desc.trim() : "");
            nouveau.setScoreAudit(txtScore.getText().trim());
            service.ajouter(nouveau);
        } else {
            // Modification
            rapport.setTitre(txtTitre.getText().trim());
            rapport.setAuditeur(txtAuditeur.getText().trim()); // Catégorie
            rapport.setDateCreation(dpDate.getValue());
            String desc = txtDescription.getText();
            rapport.setDescription(desc != null ? desc.trim() : "");
            rapport.setScoreAudit(txtScore.getText().trim());
            service.modifier(rapport);
        }

        if (onSave != null)
            onSave.run();
        fermer();
    }

    @FXML
    public void annuler() {
        fermer();
    }

    // ─── Validation ───────────────────────────────────────────

    private boolean valider() {
        boolean ok = true;

        // Titre obligatoire, min 3 caractères
        if (txtTitre.getText().trim().length() < 3) {
            lblErreurTitre.setText("⚠ Titre obligatoire (min. 3 caractères)");
            ok = false;
        }

        // Auditeur obligatoire, lettres seulement
        if (txtAuditeur.getText().trim().isEmpty()) {
            lblErreurAuditeur.setText("⚠ Auditeur obligatoire");
            ok = false;
        } else if (!txtAuditeur.getText().trim().matches("[a-zA-ZÀ-ÿ\\s]+")) {
            lblErreurAuditeur.setText("⚠ Lettres uniquement");
            ok = false;
        }

        // Entité obligatoire
        if (txtEntite.getText().trim().isEmpty()) {
            lblErreurEntite.setText("⚠ Entité auditée obligatoire");
            ok = false;
        }

        return ok;
    }

    private void fermer() {
        ((Stage) txtTitre.getScene().getWindow()).close();
    }
}