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
    private ComboBox<StatutRapport> cmbStatut;
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
            txtEntite.setText(rapport.getEntiteAuditee());
            txtDescription.setText(rapport.getDescription());
            cmbStatut.setValue(rapport.getStatut());
        }
    }

    @FXML
    public void initialize() {
        cmbStatut.setItems(FXCollections.observableArrayList(StatutRapport.values()));
        cmbStatut.setValue(StatutRapport.BROUILLON);

        // Effacer erreur en temps réel
        txtTitre.textProperty().addListener((o, a, n) -> lblErreurTitre.setText(""));
        txtAuditeur.textProperty().addListener((o, a, n) -> lblErreurAuditeur.setText(""));
        txtEntite.textProperty().addListener((o, a, n) -> lblErreurEntite.setText(""));
    }

    @FXML
    public void sauvegarder() {
        if (!valider())
            return;

        if (rapport == null) {
            // Création
            RapportAudit nouveau = new RapportAudit(
                    txtTitre.getText().trim(),
                    txtAuditeur.getText().trim(),
                    txtEntite.getText().trim());
            String desc = txtDescription.getText();
            nouveau.setDescription(desc != null ? desc.trim() : "");
            nouveau.setStatut(cmbStatut.getValue());
            service.ajouter(nouveau);
        } else {
            // Modification
            rapport.setTitre(txtTitre.getText().trim());
            rapport.setAuditeur(txtAuditeur.getText().trim());
            rapport.setEntiteAuditee(txtEntite.getText().trim());
            String desc = txtDescription.getText();
            rapport.setDescription(desc != null ? desc.trim() : "");
            rapport.setStatut(cmbStatut.getValue());
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