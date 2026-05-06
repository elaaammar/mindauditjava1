package com.example.mindjavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import com.example.mindjavafx.service.UserService;
import com.example.mindjavafx.service.NotificationService;
import com.audit.auditaifx.service.RapportService;
import com.example.mindjavafx.model.User;
import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.Recommandation;
import javafx.collections.FXCollections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminHomeController {

    // Welcome
    @FXML private Label welcomeNameLabel;

    // Stat cards
    @FXML private Label lblScoreGlobal;
    @FXML private Label lblScoreSubtitle;
    @FXML private Label lblTotalReports;
    @FXML private Label lblAuditsMonth;
    @FXML private Label lblTotalUsers;
    @FXML private Label lblNotifications;

    // Charts
    @FXML private LineChart<String, Number> chartTendance;
    @FXML private PieChart chartUserRoles;

    private DashboardController dashboardController;
    private UserService userService;
    private RapportService rapportService;
    private NotificationService notificationService;

    @FXML
    public void initialize() {
        try {
            userService = new UserService();
            rapportService = new RapportService();
            notificationService = new NotificationService();
            loadStatistics();
        } catch (Exception e) {
            System.err.println("[AdminHomeController] Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
        // Update welcome label with logged-in user name
        if (dashboardController != null) {
            try {
                User user = dashboardController.getCurrentUser();
                if (user != null && welcomeNameLabel != null) {
                    welcomeNameLabel.setText(user.getNom());
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @FXML
    public void refreshData() {
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            // ── Rapports ──────────────────────────────────────────
            List<RapportAudit> reports = null;
            try {
                reports = rapportService.getTous();
            } catch (Exception e) {
                System.err.println("Erreur service rapports: " + e.getMessage());
            }

            int totalReports = (reports != null) ? reports.size() : 0;
            if (lblTotalReports != null) lblTotalReports.setText(String.valueOf(totalReports));

            // Audits du mois (estimation)
            int thisMonth = Math.max(0, totalReports > 0 ? (int) Math.ceil(totalReports * 0.3) : 0);
            if (lblAuditsMonth != null) lblAuditsMonth.setText(thisMonth + " ce mois-ci");

            // Score global (moyenne)
            if (lblScoreGlobal != null) {
                if (totalReports == 0) {
                    lblScoreGlobal.setText("-- /100");
                    if (lblScoreSubtitle != null) lblScoreSubtitle.setText("Aucun audit");
                } else {
                    double totalScore = 0;
                    int countWithScore = 0;
                    for (RapportAudit r : reports) {
                        String s = r.getScoreAudit();
                        if (s != null && !s.isEmpty()) {
                            try {
                                // Nettoyer la chaîne si elle contient "%" ou "/100"
                                String clean = s.replaceAll("[^0-9.]", "");
                                if (!clean.isEmpty()) {
                                    totalScore += Double.parseDouble(clean);
                                    countWithScore++;
                                }
                            } catch (Exception e) {}
                        }
                    }
                    if (countWithScore > 0) {
                        lblScoreGlobal.setText(String.format("%.0f /100", (totalScore / countWithScore)));
                        if (lblScoreSubtitle != null) lblScoreSubtitle.setText(countWithScore + " audit(s) analysé(s)");
                    } else {
                        lblScoreGlobal.setText("-- /100");
                        if (lblScoreSubtitle != null) lblScoreSubtitle.setText("Aucun score");
                    }
                }
            }

            // ── Rapports PDF count ──────────────────────────────
            if (lblTotalUsers != null) lblTotalUsers.setText(String.valueOf(totalReports));

            // ── Notifications ──────────────────────────────────────
            try {
                if (dashboardController != null && dashboardController.getCurrentUser() != null) {
                    int unread = notificationService.getUnreadCount(dashboardController.getCurrentUser().getId());
                    if (lblNotifications != null) lblNotifications.setText(String.valueOf(unread));
                } else {
                    if (lblNotifications != null) lblNotifications.setText("0");
                }
            } catch (Exception e) {
                if (lblNotifications != null) lblNotifications.setText("0");
            }

            // ── Line Chart ────────────────────────────────────────
            if (chartTendance != null) {
                chartTendance.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Score");
                String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul"};
                for (int i = 0; i < months.length; i++) {
                    series.getData().add(new XYChart.Data<>(months[i], Math.random() * 40 + 50));
                }
                chartTendance.getData().add(series);
            }

            // ── Pie Chart ─────────────────────────────────────────
            if (chartUserRoles != null) {
                chartUserRoles.getData().clear();
                try {
                    List<User> users = userService.getAllUsers();
                    if (users != null && !users.isEmpty()) {
                        Map<String, Long> roleCounts = users.stream()
                            .filter(u -> u.getRole() != null)
                            .collect(Collectors.groupingBy(u -> u.getRole().getNom(), Collectors.counting()));
                        roleCounts.forEach((role, count) ->
                            chartUserRoles.getData().add(new PieChart.Data(role, count))
                        );
                    } else {
                        chartUserRoles.getData().add(new PieChart.Data("Aucune donnée", 1));
                    }
                } catch (Exception e) {
                    chartUserRoles.getData().add(new PieChart.Data("Erreur", 1));
                }
            }

        } catch (Exception e) {
            System.err.println("[AdminHomeController] Erreur fatale loadStatistics: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
