package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.Audit;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuditService;
import com.example.mindjavafx.service.NotificationService;
import com.example.mindjavafx.service.ReportService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserDashboardHomeController {

    @FXML private Label welcomeUserLabel;
    @FXML private Label globalScoreLabel;
    @FXML private Label scoreStatusLabel;
    @FXML private Label auditCountLabel;
    @FXML private Label reportCountLabel;
    @FXML private Label notificationCountLabel;
    
    @FXML private BarChart<String, Number> scoreEvolutionChart;
    @FXML private PieChart categoryDistributionChart;
    
    @FXML private ProgressBar securityProgress;
    @FXML private Label securityStatusLabel;
    @FXML private ProgressBar complianceProgress;
    @FXML private Label complianceStatusLabel;
    @FXML private ProgressBar performanceProgress;
    @FXML private Label performanceStatusLabel;
    
    @FXML private ListView<String> recentActivityList;

    private User currentUser;
    private ProfessionalDashboardController dashboardController;
    private AuditService auditService;
    private NotificationService notificationService;
    private ReportService reportService;

    @FXML
    public void initialize() {
        auditService = new AuditService();
        notificationService = new NotificationService();
        reportService = new ReportService();
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

        // Get user audits
        List<Audit> audits = auditService.getAuditsByUserId(currentUser.getId());
        
        // Update audit count with animation
        animateNumber(auditCountLabel, 0, audits.size());
        
        // Get most recent audit
        Audit recentAudit = auditService.getMostRecentAudit(currentUser.getId());
        
        if (recentAudit != null) {
            // Animate global score
            animateNumber(globalScoreLabel, 0, recentAudit.getGlobalScore());
            
            // Update score status
            updateScoreStatus(recentAudit.getGlobalScore());
            
            // Update category progress bars with animation
            animateProgressBar(securityProgress, recentAudit.getSecurityScore() / 100.0);
            securityStatusLabel.setText(getStatusText(recentAudit.getSecurityScore()));
            
            animateProgressBar(complianceProgress, recentAudit.getComplianceScore() / 100.0);
            complianceStatusLabel.setText(getStatusText(recentAudit.getComplianceScore()));
            
            animateProgressBar(performanceProgress, recentAudit.getPerformanceScore() / 100.0);
            performanceStatusLabel.setText(getStatusText(recentAudit.getPerformanceScore()));
        } else {
            globalScoreLabel.setText("--");
            scoreStatusLabel.setText("Aucun audit");
        }
        
        // Update report count
        int reportCount = reportService.getReportsByUserId(currentUser.getId()).size();
        animateNumber(reportCountLabel, 0, reportCount);
        
        // Update notification count
        int notifCount = notificationService.getUnreadCount(currentUser.getId());
        animateNumber(notificationCountLabel, 0, notifCount);
        
        // Load charts
        loadScoreEvolutionChart(audits);
        loadCategoryDistributionChart(recentAudit);
        
        // Load recent activity
        loadRecentActivity(audits);
    }

    private void animateNumber(Label label, int from, int to) {
        Timeline timeline = new Timeline();
        final int[] current = {from};
        
        KeyFrame keyFrame = new KeyFrame(Duration.millis(20), event -> {
            if (current[0] < to) {
                current[0]++;
                label.setText(String.valueOf(current[0]));
            }
        });
        
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(to - from);
        timeline.play();
    }

    private void animateProgressBar(ProgressBar progressBar, double targetProgress) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(1), new KeyValue(progressBar.progressProperty(), targetProgress, Interpolator.EASE_OUT))
        );
        timeline.play();
    }

    private void updateScoreStatus(int score) {
        if (score >= 85) {
            scoreStatusLabel.setText("Excellent ✨");
            scoreStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        } else if (score >= 70) {
            scoreStatusLabel.setText("Bon 👍");
            scoreStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        } else if (score >= 50) {
            scoreStatusLabel.setText("Moyen ⚠️");
            scoreStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        } else {
            scoreStatusLabel.setText("Faible ❌");
            scoreStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        }
    }

    private String getStatusText(int score) {
        if (score >= 85) {
            return "Excellent (" + score + "/100)";
        } else if (score >= 70) {
            return "Bon (" + score + "/100)";
        } else if (score >= 50) {
            return "Moyen (" + score + "/100)";
        } else {
            return "Faible (" + score + "/100)";
        }
    }

    private void loadScoreEvolutionChart(List<Audit> audits) {
        scoreEvolutionChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Score Global");
        
        // Take last 6 audits
        int startIndex = Math.max(0, audits.size() - 6);
        for (int i = startIndex; i < audits.size(); i++) {
            Audit audit = audits.get(i);
            String label = audit.getName().length() > 10 ? 
                          audit.getName().substring(0, 10) + "..." : 
                          audit.getName();
            series.getData().add(new XYChart.Data<>(label, audit.getGlobalScore()));
        }
        
        scoreEvolutionChart.getData().add(series);
        
        // Animate chart
        scoreEvolutionChart.setAnimated(true);
    }

    private void loadCategoryDistributionChart(Audit recentAudit) {
        categoryDistributionChart.getData().clear();
        
        if (recentAudit != null) {
            PieChart.Data securityData = new PieChart.Data("Sécurité", recentAudit.getSecurityScore());
            PieChart.Data complianceData = new PieChart.Data("Conformité", recentAudit.getComplianceScore());
            PieChart.Data performanceData = new PieChart.Data("Performance", recentAudit.getPerformanceScore());
            
            categoryDistributionChart.getData().addAll(securityData, complianceData, performanceData);
            
            // Animate pie chart
            categoryDistributionChart.setAnimated(true);
        }
    }

    private void loadRecentActivity(List<Audit> audits) {
        recentActivityList.getItems().clear();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        // Show last 5 audits
        int count = Math.min(5, audits.size());
        for (int i = 0; i < count; i++) {
            Audit audit = audits.get(i);
            String activity = "📝 " + audit.getName() + " - " + 
                            audit.getAuditDate().format(formatter) + 
                            " (Score: " + audit.getGlobalScore() + ")";
            recentActivityList.getItems().add(activity);
        }
        
        if (audits.isEmpty()) {
            recentActivityList.getItems().add("Aucune activité récente");
        }
    }

    public void setDashboardController(ProfessionalDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private void handleCreateAudit() {
        if (dashboardController != null) {
            dashboardController.showMyAudits();
        } else {
            showInfoDialog("Nouvel Audit", "Fonctionnalité en cours de développement...");
        }
    }

    @FXML
    private void handleGenerateReport() {
        if (dashboardController != null) {
            dashboardController.showReports();
        } else {
            showInfoDialog("Générer Rapport", "Fonctionnalité en cours de développement...");
        }
    }

    @FXML
    private void handleScheduleAudit() {
        showInfoDialog("Planifier Audit", "Fonctionnalité en cours de développement...");
    }

    @FXML
    private void handleViewAnalytics() {
        if (dashboardController != null) {
            dashboardController.showAnalytics();
        } else {
            showInfoDialog("Analytics", "Fonctionnalité en cours de développement...");
        }
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
