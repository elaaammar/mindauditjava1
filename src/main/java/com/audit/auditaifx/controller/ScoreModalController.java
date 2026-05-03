package com.audit.auditaifx.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class ScoreModalController {

    @FXML private Label lblScoreGlobal;
    @FXML private Label lblVerdict;
    @FXML private ProgressBar progressCompletude;
    @FXML private ProgressBar progressConformite;
    @FXML private ProgressBar progressDocumentation;
    @FXML private ProgressBar progressSuivi;
    @FXML private ProgressBar progressReactivite;
    @FXML private Label lblValCompletude;
    @FXML private Label lblValConformite;
    @FXML private Label lblValDocumentation;
    @FXML private Label lblValSuivi;
    @FXML private Label lblValReactivite;

    public void setScoreData(String json) {
        try {
            JsonObject data = JsonParser.parseString(json).getAsJsonObject();

            double g = data.get("global").getAsDouble();
            lblScoreGlobal.setText(String.format("%.1f", g));
            updateBadgeColor(lblScoreGlobal, g);

            lblVerdict.setText(data.get("verdict").getAsString());

            setDimension(progressCompletude, lblValCompletude, data.get("completude").getAsDouble());
            setDimension(progressConformite, lblValConformite, data.get("conformite").getAsDouble());
            setDimension(progressDocumentation, lblValDocumentation, data.get("documentation").getAsDouble());
            setDimension(progressSuivi, lblValSuivi, data.get("suivi").getAsDouble());
            setDimension(progressReactivite, lblValReactivite, data.get("reactivite").getAsDouble());

        } catch (Exception e) {
            lblVerdict.setText("Erreur lors de l'analyse des données.");
        }
    }

    private void setDimension(ProgressBar pb, Label lb, double val) {
        pb.setProgress(val / 10.0);
        lb.setText((int)val + "/10");
        
        if (val >= 7) pb.setStyle("-fx-accent: #2ecc71;");
        else if (val >= 4) pb.setStyle("-fx-accent: #f1c40f;");
        else pb.setStyle("-fx-accent: #e74c3c;");
    }

    private void updateBadgeColor(Label lbl, double score) {
        String color;
        if (score >= 7) color = "#2ecc71";
        else if (score >= 4) color = "#f1c40f";
        else color = "#e74c3c";
        
        lbl.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                     "-fx-padding: 10 20; -fx-background-radius: 10; -fx-font-size: 20px; -fx-font-weight: bold;");
    }

    @FXML
    public void fermer() {
        ((Stage) lblScoreGlobal.getScene().getWindow()).close();
    }
}
