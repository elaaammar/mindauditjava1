package com.audit.auditaifx.controller;

import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.Recommandation;
import com.audit.auditaifx.service.RapportService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RecoFormController {

    @FXML
    private TextArea txtDescription;
    @FXML
    private ComboBox<String> cmbPriorite;
    @FXML
    private CheckBox chkResolue;
    @FXML
    private Label lblErreurDescription;
    @FXML
    private Label lblErreurPriorite;

    private RapportAudit rapport;
    private Recommandation recommandation;
    private RapportService service;
    private Runnable onSave;

    public void setRapport(RapportAudit rapport) {
        this.rapport = rapport;
    }

    public void setService(RapportService service) {
        this.service = service;
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    public void setRecommandation(Recommandation recommandation) {
        this.recommandation = recommandation;
        if (recommandation != null) {
            txtDescription.setText(recommandation.getDescription());
            cmbPriorite.setValue(recommandation.getPriorite());
            chkResolue.setSelected(recommandation.isResolue());
        }
    }

    @FXML
    public void initialize() {
        cmbPriorite.setItems(FXCollections.observableArrayList(
                "Haute", "Moyenne", "Faible"));
        cmbPriorite.setValue("Moyenne");

        txtDescription.textProperty().addListener(
                (o, a, n) -> lblErreurDescription.setText(""));
        cmbPriorite.valueProperty().addListener(
                (o, a, n) -> lblErreurPriorite.setText(""));
    }

    @FXML
    public void sauvegarder() {
        if (!valider())
            return;

        if (recommandation == null) {
            // Création
            Recommandation nouvelle = new Recommandation(
                    txtDescription.getText().trim(),
                    cmbPriorite.getValue());
            nouvelle.setResolue(chkResolue.isSelected());
            rapport.ajouterRecommandation(nouvelle);
            if (service != null && rapport.getId() != null) {
                service.ajouterRecommandation(rapport.getId(), nouvelle);
            }
        } else {
            // Modification
            recommandation.setDescription(txtDescription.getText().trim());
            recommandation.setPriorite(cmbPriorite.getValue());
            recommandation.setResolue(chkResolue.isSelected());
            if (service != null) {
                service.modifierRecommandation(recommandation);
            }
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

        // Description obligatoire min 10 caractères
        if (txtDescription.getText().trim().length() < 10) {
            lblErreurDescription.setText(
                    "⚠ Description obligatoire (min. 10 caractères)");
            ok = false;
        }

        // Priorité obligatoire
        if (cmbPriorite.getValue() == null) {
            lblErreurPriorite.setText("⚠ Priorité obligatoire");
            ok = false;
        }

        return ok;
    }

    private void fermer() {
        ((Stage) txtDescription.getScene().getWindow()).close();
    }
}