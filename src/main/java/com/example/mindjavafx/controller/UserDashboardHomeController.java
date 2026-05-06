package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.util.List;

public class UserDashboardHomeController {

    @FXML private Label welcomeUserLabel;
    @FXML private Label globalScoreLabel;
    @FXML private Label reportCountLabel;
    @FXML private Label totalRecoLabel;
    @FXML private Label resolvedRecoLabel;
    
    @FXML private BarChart<String, Number> scoreEvolutionChart;
    @FXML private PieChart categoryDistributionChart;
    @FXML private PieChart recoStatusChart;
    @FXML private LineChart<String, Number> trendChart;
    
    @FXML private ProgressBar securityProgress;
    @FXML private Label securityStatusLabel;
    @FXML private ProgressBar complianceProgress;
    @FXML private Label complianceStatusLabel;
    @FXML private ProgressBar performanceProgress;
    @FXML private Label performanceStatusLabel;
    
    @FXML private VBox recentReportsContainer;
    @FXML private VBox recentRecosContainer;

    private User currentUser;
    private Object dashboardController;

    @FXML
    public void initialize() {
        // Initialization if needed
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            welcomeUserLabel.setText(user.getNom());
            loadOverviewData();
        }
    }

    private void loadOverviewData() {
        if (currentUser == null) return;

        com.audit.auditaifx.service.RapportService rapportService = new com.audit.auditaifx.service.RapportService();
        List<com.audit.auditaifx.model.RapportAudit> rapports = rapportService.getTous().filtered(r -> 
            r.getAuditeur().equalsIgnoreCase(currentUser.getNom()) || 
            r.getEntiteAuditee().equalsIgnoreCase(currentUser.getNom())
        );
        
        animateNumber(reportCountLabel, 0, rapports.size());
        
        int totalReco = 0;
        int resolvedReco = 0;
        for (com.audit.auditaifx.model.RapportAudit r : rapports) {
            totalReco += r.getRecommandations().size();
            for (com.audit.auditaifx.model.Recommandation reco : r.getRecommandations()) {
                if (reco.isResolue()) resolvedReco++;
            }
        }
        if (totalRecoLabel != null) animateNumber(totalRecoLabel, 0, totalReco);
        if (resolvedRecoLabel != null) animateNumber(resolvedRecoLabel, 0, resolvedReco);

        if (!rapports.isEmpty()) {
            com.audit.auditaifx.model.RapportAudit latest = rapports.get(0);
            try {
                com.google.gson.JsonObject data = com.google.gson.JsonParser.parseString(latest.getScoreAudit()).getAsJsonObject();
                double global = data.get("global").getAsDouble();
                animateNumber(globalScoreLabel, 0, (int)(global * 10));
                
                if (data.has("details")) {
                    com.google.gson.JsonObject details = data.getAsJsonObject("details");
                    updateProgress(securityProgress, securityStatusLabel, details.has("security") ? details.get("security").getAsDouble() : 0);
                    updateProgress(complianceProgress, complianceStatusLabel, details.has("compliance") ? details.get("compliance").getAsDouble() : 0);
                    updateProgress(performanceProgress, performanceStatusLabel, details.has("performance") ? details.get("performance").getAsDouble() : 0);
                }
            } catch (Exception e) {}
        }
        
        if (scoreEvolutionChart != null) loadScoreEvolutionChart(rapports);
        if (categoryDistributionChart != null) loadCategoryDistributionChart(rapports);
        if (recoStatusChart != null) loadRecoStatusChart(totalReco, resolvedReco);
        if (trendChart != null) loadTrendChart(rapports);

        if (recentReportsContainer != null) loadRecentReports(rapports);
        if (recentRecosContainer != null) loadRecentRecos(rapports);
    }

    private void loadRecoStatusChart(int total, int resolved) {
        recoStatusChart.getData().clear();
        if (total > 0) {
            recoStatusChart.getData().add(new PieChart.Data("Résolues", resolved));
            recoStatusChart.getData().add(new PieChart.Data("En attente", total - resolved));
        }
    }

    private void loadTrendChart(List<com.audit.auditaifx.model.RapportAudit> rapports) {
        trendChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        java.util.Map<String, Long> counts = rapports.stream()
            .collect(java.util.stream.Collectors.groupingBy(r -> r.getDateCreation().toString(), java.util.stream.Collectors.counting()));
        counts.entrySet().stream()
            .sorted(java.util.Map.Entry.comparingByKey())
            .limit(7)
            .forEach(e -> series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));
        trendChart.getData().add(series);
    }

    private void loadRecentReports(List<com.audit.auditaifx.model.RapportAudit> rapports) {
        recentReportsContainer.getChildren().clear();
        rapports.stream().limit(3).forEach(r -> {
            Label l = new Label("📄 " + r.getTitre() + " (" + r.getStatut() + ")");
            l.setStyle("-fx-text-fill: #34495e; -fx-font-size: 13px; -fx-padding: 5 0;");
            recentReportsContainer.getChildren().add(l);
        });
    }

    private void loadRecentRecos(List<com.audit.auditaifx.model.RapportAudit> rapports) {
        recentRecosContainer.getChildren().clear();
        rapports.stream().flatMap(r -> r.getRecommandations().stream()).limit(3).forEach(reco -> {
            Label l = new Label("📌 " + reco.getDescription());
            l.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-padding: 5 0;");
            l.setWrapText(true);
            recentRecosContainer.getChildren().add(l);
        });
    }

    private void updateProgress(ProgressBar pb, Label lb, double score) {
        if (pb == null) return;
        animateProgressBar(pb, score / 10.0);
        if (lb != null) lb.setText(String.format("%.1f/10", score));
    }

    private void loadScoreEvolutionChart(List<com.audit.auditaifx.model.RapportAudit> rapports) {
        scoreEvolutionChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Score Audit");
        for (com.audit.auditaifx.model.RapportAudit r : rapports) {
            try {
                com.google.gson.JsonObject data = com.google.gson.JsonParser.parseString(r.getScoreAudit()).getAsJsonObject();
                double score = data.get("global").getAsDouble();
                series.getData().add(new XYChart.Data<>(r.getDateCreation().toString(), score));
            } catch (Exception e) {}
        }
        scoreEvolutionChart.getData().add(series);
    }

    private void loadCategoryDistributionChart(List<com.audit.auditaifx.model.RapportAudit> rapports) {
        categoryDistributionChart.getData().clear();
        int finalises = 0, brouillons = 0, enCours = 0;
        for (com.audit.auditaifx.model.RapportAudit r : rapports) {
            if (r.getStatut().name().equals("FINALISE")) finalises++;
            else if (r.getStatut().name().equals("BROUILLON")) brouillons++;
            else enCours++;
        }
        if (finalises > 0) categoryDistributionChart.getData().add(new PieChart.Data("Finalisés", finalises));
        if (brouillons > 0) categoryDistributionChart.getData().add(new PieChart.Data("Brouillons", brouillons));
        if (enCours > 0) categoryDistributionChart.getData().add(new PieChart.Data("En Cours", enCours));
    }

    private void animateNumber(Label label, int from, int to) {
        if (to == from) {
            label.setText(String.valueOf(to));
            return;
        }
        Timeline timeline = new Timeline();
        final int[] current = {from};
        KeyFrame keyFrame = new KeyFrame(Duration.millis(20), event -> {
            if (current[0] < to) {
                current[0]++;
                label.setText(String.valueOf(current[0]));
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Math.abs(to - from));
        timeline.play();
    }

    private void animateProgressBar(ProgressBar progressBar, double targetProgress) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(1), new KeyValue(progressBar.progressProperty(), targetProgress, Interpolator.EASE_OUT))
        );
        timeline.play();
    }

    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private void handleGenerateReport() {
        if (dashboardController instanceof UserDashboardController) {
            ((UserDashboardController) dashboardController).showAddRapport();
        }
    }
}
