package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

import java.sql.SQLException;
import java.util.List;

public class DashboardHomeController {

    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label blockedUsersLabel;
    @FXML private Label newUsersLabel;
    
    @FXML private Label adminCountLabel;
    @FXML private Label userCountLabel;
    @FXML private Label auditeurCountLabel;
    
    @FXML private ListView<String> recentUsersListView;
    @FXML private ListView<String> alertsListView;
    
    @FXML private LineChart<String, Number> registrationChart;
    
    private DashboardController dashboardController;
    private UserService userService;

    @FXML
    public void initialize() {
        System.out.println("[DEBUG] DashboardHomeController - initialize() appelé");
        userService = new UserService();
        
        try {
            loadDashboardData();
            System.out.println("[DEBUG] Dashboard data chargé avec succès");
        } catch (Exception e) {
            System.err.println("[ERREUR] Erreur lors du chargement du dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Valeurs par défaut en cas d'erreur
            if (totalUsersLabel != null) totalUsersLabel.setText("0");
            if (activeUsersLabel != null) activeUsersLabel.setText("0");
            if (blockedUsersLabel != null) blockedUsersLabel.setText("0");
            if (newUsersLabel != null) newUsersLabel.setText("0");
            if (adminCountLabel != null) adminCountLabel.setText("0");
            if (userCountLabel != null) userCountLabel.setText("0");
            if (auditeurCountLabel != null) auditeurCountLabel.setText("0");
        }
    }

    private void loadDashboardData() {
        try {
            System.out.println("[DEBUG] Chargement des données dashboard...");
            List<User> allUsers = userService.getAllUsers();
            System.out.println("[DEBUG] Nombre d'utilisateurs: " + allUsers.size());
            
            // 1. Cartes statistiques
            int totalUsers = allUsers.size();
            int activeUsers = (int) allUsers.stream().filter(User::isActif).count();
            int blockedUsers = totalUsers - activeUsers;
            int newUsers = Math.min(7, totalUsers);
            
            System.out.println("[DEBUG] Stats - Total: " + totalUsers + ", Actifs: " + activeUsers + ", Bloqués: " + blockedUsers);
            
            if (totalUsersLabel != null) totalUsersLabel.setText(String.valueOf(totalUsers));
            if (activeUsersLabel != null) activeUsersLabel.setText(String.valueOf(activeUsers));
            if (blockedUsersLabel != null) blockedUsersLabel.setText(String.valueOf(blockedUsers));
            if (newUsersLabel != null) newUsersLabel.setText(String.valueOf(newUsers));
            
            // 2. Aperçu des rôles
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
            
            System.out.println("[DEBUG] Rôles - Admin: " + adminCount + ", User: " + userCount + ", Auditeur: " + auditeurCount);
            
            if (adminCountLabel != null) adminCountLabel.setText(String.valueOf(adminCount));
            if (userCountLabel != null) userCountLabel.setText(String.valueOf(userCount));
            if (auditeurCountLabel != null) auditeurCountLabel.setText(String.valueOf(auditeurCount));
            
            // 3. Derniers utilisateurs inscrits
            if (recentUsersListView != null) {
                recentUsersListView.getItems().clear();
                int count = Math.min(5, allUsers.size());
                for (int i = allUsers.size() - 1; i >= Math.max(0, allUsers.size() - count); i--) {
                    User user = allUsers.get(i);
                    String roleName = user.getRole() != null ? user.getRole().getNom() : "N/A";
                    recentUsersListView.getItems().add(
                        String.format("👤 %s - %s (%s)", user.getNom(), user.getEmail(), roleName)
                    );
                }
                System.out.println("[DEBUG] Derniers utilisateurs ajoutés: " + count);
            }
            
            // 4. Alertes
            if (alertsListView != null) {
                alertsListView.getItems().clear();
                if (blockedUsers > 0) {
                    alertsListView.getItems().add("⚠️ " + blockedUsers + " compte(s) bloqué(s)");
                }
                if (newUsers > 5) {
                    alertsListView.getItems().add("🆕 " + newUsers + " nouveaux utilisateurs cette semaine");
                }
                if (alertsListView.getItems().isEmpty()) {
                    alertsListView.getItems().add("✅ Aucune alerte");
                }
                System.out.println("[DEBUG] Alertes ajoutées: " + alertsListView.getItems().size());
            }
            
            // 5. Graphique d'évolution
            if (registrationChart != null) {
                loadRegistrationChart(allUsers);
                System.out.println("[DEBUG] Graphique chargé");
            }
            
            System.out.println("[DEBUG] Toutes les données chargées avec succès!");
            
        } catch (SQLException e) {
            System.err.println("[ERREUR] SQLException lors du chargement dashboard: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERREUR] Exception lors du chargement dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private void goToUserManagement() {
        if (dashboardController != null) {
            dashboardController.showUserManagement();
        }
    }

    @FXML
    private void goToRolePermission() {
        if (dashboardController != null) {
            dashboardController.showRolePermission();
        }
    }

    @FXML
    private void goToStatistics() {
        if (dashboardController != null) {
            dashboardController.showStatistics();
        }
    }

    @FXML
    private void goToSettings() {
        if (dashboardController != null) {
            dashboardController.showProfile();
        }
    }

    private void loadRegistrationChart(List<User> users) {
        try {
            registrationChart.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Inscriptions");
            
            // Simuler des données par jour (derniers 7 jours)
            String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
            
            for (int i = 0; i < 7; i++) {
                int count = (int) (Math.random() * 5) + 1;
                series.getData().add(new XYChart.Data<>(days[i], count));
            }
            
            registrationChart.getData().add(series);
            System.out.println("[DEBUG] Graphique créé avec 7 points de données");
        } catch (Exception e) {
            System.err.println("[ERREUR] Erreur lors de la création du graphique: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
