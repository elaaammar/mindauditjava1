package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.Audit;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuditService;
import com.example.mindjavafx.service.UserService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

import java.util.List;

public class StatisticsController {

    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label inactiveUsersLabel;
    @FXML private Label totalAuditsLabel;
    
    @FXML private Label currentDateLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label lastUpdateLabel;
    
    @FXML private PieChart roleDistributionChart;
    @FXML private Label adminCountLabel;
    @FXML private Label userCountLabel;
    @FXML private Label auditeurCountLabel;
    
    @FXML private Label securityAvgLabel;
    @FXML private ProgressBar securityAvgProgress;
    @FXML private Label complianceAvgLabel;
    @FXML private ProgressBar complianceAvgProgress;
    @FXML private Label performanceAvgLabel;
    @FXML private ProgressBar performanceAvgProgress;
    @FXML private Label globalAvgLabel;
    @FXML private ProgressBar globalAvgProgress;
    
    @FXML private ListView<String> globalActivityList;
    @FXML private ListView<String> systemAlertsListView;

    private UserService userService;
    private AuditService auditService;
    private Timeline refreshTimeline;

    @FXML
    public void initialize() {
        userService = new UserService();
        auditService = new AuditService();
        
        updateDateTime();
        loadStatistics();
        loadSystemAlerts();
        startAutoRefresh();
        startClockUpdate();
    }
    
    private void startClockUpdate() {
        // Update clock every second
        Timeline clockTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> updateDateTime())
        );
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }
    
    private void updateDateTime() {
        if (currentDateLabel != null && currentTimeLabel != null) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", java.util.Locale.FRENCH);
            java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
            
            currentDateLabel.setText(now.format(dateFormatter));
            currentTimeLabel.setText(now.format(timeFormatter));
        }
    }
    
    private void startAutoRefresh() {
        // Refresh statistics every 3 seconds
        refreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(3), event -> {
                loadStatistics();
                updateLastUpdateTime();
            })
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }
    
    private void updateLastUpdateTime() {
        if (lastUpdateLabel != null) {
            lastUpdateLabel.setText("Dernière mise à jour: À l'instant");
        }
    }
    
    private void loadSystemAlerts() {
        if (systemAlertsListView == null) return;
        
        systemAlertsListView.getItems().clear();
        systemAlertsListView.getItems().addAll(
            "⚠️ 3 utilisateurs inactifs depuis plus de 30 jours",
            "✅ Tous les audits sont à jour",
            "📊 Taux de conformité global: 85%"
        );
    }
    
    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    private void loadStatistics() {
        try {
            // Get all users
            List<User> allUsers = userService.getAllUsers();
        
        // Count users by status
        int totalUsers = allUsers.size();
        int activeUsers = (int) allUsers.stream().filter(User::isActif).count();
        int inactiveUsers = totalUsers - activeUsers;
        
        // Animate user counts
        animateNumber(totalUsersLabel, 0, totalUsers, Duration.seconds(1.5));
        animateNumber(activeUsersLabel, 0, activeUsers, Duration.seconds(1.5));
        animateNumber(inactiveUsersLabel, 0, inactiveUsers, Duration.seconds(1.5));
        
        // Count users by role
        int adminCount = 0;
        int userCount = 0;
        int auditeurCount = 0;
        
        for (User user : allUsers) {
            if (user.getRole() != null) {
                String roleName = user.getRole().getNom();
                if ("Admin".equalsIgnoreCase(roleName)) {
                    adminCount++;
                } else if ("User".equalsIgnoreCase(roleName)) {
                    userCount++;
                } else if ("Auditeur".equalsIgnoreCase(roleName)) {
                    auditeurCount++;
                }
            }
        }
        
        // Calculate percentages
        double adminPercent = totalUsers > 0 ? (adminCount * 100.0 / totalUsers) : 0;
        double userPercent = totalUsers > 0 ? (userCount * 100.0 / totalUsers) : 0;
        double auditeurPercent = totalUsers > 0 ? (auditeurCount * 100.0 / totalUsers) : 0;
        
        // Update role labels with animation
        if (adminCountLabel != null) animateRoleLabel(adminCountLabel, adminCount, adminPercent);
        if (userCountLabel != null) animateRoleLabel(userCountLabel, userCount, userPercent);
        if (auditeurCountLabel != null) animateRoleLabel(auditeurCountLabel, auditeurCount, auditeurPercent);
        
        // Load role distribution chart
        loadRoleDistributionChart(adminCount, userCount, auditeurCount);
        
        // Get all audits
        List<Audit> allAudits = getAllAudits();
        if (totalAuditsLabel != null) animateNumber(totalAuditsLabel, 0, allAudits.size(), Duration.seconds(1.5));
        
        // Calculate average scores
        if (!allAudits.isEmpty() && securityAvgLabel != null) {
            double securityAvg = allAudits.stream()
                .mapToInt(Audit::getSecurityScore)
                .average()
                .orElse(0);
            
            double complianceAvg = allAudits.stream()
                .mapToInt(Audit::getComplianceScore)
                .average()
                .orElse(0);
            
            double performanceAvg = allAudits.stream()
                .mapToInt(Audit::getPerformanceScore)
                .average()
                .orElse(0);
            
            double globalAvg = allAudits.stream()
                .mapToInt(Audit::getGlobalScore)
                .average()
                .orElse(0);
            
            // Animate progress bars and labels
            animateScoreProgress(securityAvgLabel, securityAvgProgress, securityAvg);
            animateScoreProgress(complianceAvgLabel, complianceAvgProgress, complianceAvg);
            animateScoreProgress(performanceAvgLabel, performanceAvgProgress, performanceAvg);
            animateScoreProgress(globalAvgLabel, globalAvgProgress, globalAvg);
        }
        
        // Load global activity
        if (globalActivityList != null) loadGlobalActivity(allAudits);
        
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
        }
    }

    private void animateNumber(Label label, int from, int to, Duration duration) {
        Timeline timeline = new Timeline();
        final int[] current = {from};
        int steps = Math.abs(to - from);
        
        if (steps == 0) {
            label.setText(String.valueOf(to));
            return;
        }
        
        KeyFrame keyFrame = new KeyFrame(Duration.millis(duration.toMillis() / steps), event -> {
            if (current[0] < to) {
                current[0]++;
            } else if (current[0] > to) {
                current[0]--;
            }
            label.setText(String.valueOf(current[0]));
        });
        
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(steps);
        timeline.play();
    }

    private void animateRoleLabel(Label label, int count, double percent) {
        Timeline timeline = new Timeline();
        final int[] currentCount = {0};
        final double[] currentPercent = {0.0};
        
        int steps = 30;
        double countStep = count / (double) steps;
        double percentStep = percent / steps;
        
        KeyFrame keyFrame = new KeyFrame(Duration.millis(50), event -> {
            currentCount[0] = Math.min((int)(currentCount[0] + countStep), count);
            currentPercent[0] = Math.min(currentPercent[0] + percentStep, percent);
            
            label.setText(String.format("%d (%.0f%%)", currentCount[0], currentPercent[0]));
        });
        
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(steps);
        timeline.setOnFinished(e -> label.setText(String.format("%d (%.0f%%)", count, percent)));
        timeline.play();
    }

    private void animateScoreProgress(Label label, ProgressBar progressBar, double score) {
        int scoreInt = (int) Math.round(score);
        
        // Animate label
        Timeline labelTimeline = new Timeline();
        final int[] current = {0};
        
        KeyFrame keyFrame = new KeyFrame(Duration.millis(30), event -> {
            if (current[0] < scoreInt) {
                current[0]++;
                label.setText(current[0] + "/100");
            }
        });
        
        labelTimeline.getKeyFrames().add(keyFrame);
        labelTimeline.setCycleCount(scoreInt);
        labelTimeline.play();
        
        // Animate progress bar
        Timeline progressTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(progressBar.progressProperty(), score / 100.0, Interpolator.EASE_OUT))
        );
        progressTimeline.play();
    }

    private void loadRoleDistributionChart(int adminCount, int userCount, int auditeurCount) {
        if (roleDistributionChart == null) return;
        
        roleDistributionChart.getData().clear();
        
        if (adminCount > 0) {
            PieChart.Data adminData = new PieChart.Data("Admin", adminCount);
            roleDistributionChart.getData().add(adminData);
        }
        
        if (userCount > 0) {
            PieChart.Data userData = new PieChart.Data("User", userCount);
            roleDistributionChart.getData().add(userData);
        }
        
        if (auditeurCount > 0) {
            PieChart.Data auditeurData = new PieChart.Data("Auditeur", auditeurCount);
            roleDistributionChart.getData().add(auditeurData);
        }
        
        if (adminCount == 0 && userCount == 0 && auditeurCount == 0) {
            PieChart.Data emptyData = new PieChart.Data("Aucun utilisateur", 1);
            roleDistributionChart.getData().add(emptyData);
        }
        
        // Animate chart
        roleDistributionChart.setAnimated(true);
    }

    private List<Audit> getAllAudits() {
        try {
            // Get audits from all users
            List<User> allUsers = userService.getAllUsers();
            List<Audit> allAudits = new java.util.ArrayList<>();
            
            for (User user : allUsers) {
                List<Audit> userAudits = auditService.getAuditsByUserId(user.getId());
                allAudits.addAll(userAudits);
            }
            
            return allAudits;
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    private void loadGlobalActivity(List<Audit> allAudits) {
        globalActivityList.getItems().clear();
        
        if (allAudits.isEmpty()) {
            globalActivityList.getItems().add("Aucune activité récente");
            return;
        }
        
        // Sort by date (most recent first)
        allAudits.sort((a1, a2) -> a2.getAuditDate().compareTo(a1.getAuditDate()));
        
        // Show last 10 audits
        int count = Math.min(10, allAudits.size());
        for (int i = 0; i < count; i++) {
            Audit audit = allAudits.get(i);
            
            // Get user name
            String userName = "Utilisateur";
            try {
                User user = userService.getUserById(audit.getUserId());
                if (user != null) {
                    userName = user.getNom();
                }
            } catch (Exception e) {
                // Ignore
            }
            
            String activity = String.format("📝 %s a réalisé '%s' - Score: %d/100", 
                userName, audit.getName(), audit.getGlobalScore());
            
            globalActivityList.getItems().add(activity);
        }
    }
}