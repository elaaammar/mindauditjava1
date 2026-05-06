package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuthenticationService;
import com.example.mindjavafx.service.NotificationService;
import com.example.mindjavafx.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.StatutRapport;
import com.audit.auditaifx.service.RapportService;
import com.audit.auditaifx.controller.MainController;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class UserDashboardController {

    @FXML private Button notificationButton;
    @FXML private Label notificationBadge;
    @FXML private Circle profileImage;
    @FXML private Label userNameLabel;
    @FXML private Button logoutButton;
    
    @FXML private Button entrepriseButton;
    @FXML private Button rapportManagementButton;
    @FXML private Button settingsButton;

    private Object currentSectionController;

    @FXML private Button dashboardButton;
    @FXML private Button statisticsButton;
    @FXML private Button usersButton;
    @FXML private Button analyticsButton;
    @FXML private Button reclamationButton;
    @FXML private Button reclamationChatbotButton;

    
    // --- Floating Chatbot Variables ---
    @FXML private javafx.scene.layout.VBox chatbotWindow;
    @FXML private javafx.scene.control.ScrollPane chatScroll;
    @FXML private javafx.scene.layout.VBox chatMessages;
    @FXML private javafx.scene.layout.VBox chatInputArea;
    @FXML private javafx.scene.control.TextField tfChatInput;
    @FXML private javafx.scene.layout.VBox chatUploadArea;
    @FXML private Button btnFloatingChat;
    
    private int chatStep = 0;
    private List<String> chatQuestions = new ArrayList<>();
    private Map<Integer, String> userAnswers = new HashMap<>();
    
    @FXML private StackPane contentArea;

    private AuthenticationService authService;
    private NotificationService notificationService;
    private UserService userService;
    private User currentUser;
    private RapportService rapportService = new RapportService();
    private com.audit.auditaifx.service.AIService aiService = new com.audit.auditaifx.service.AIService();

    @FXML
    public void initialize() {
        notificationService = new NotificationService();
        userService = new UserService();
        
        // Nettoyage automatique des "bars" d'alerte de connexion au démarrage
        try {
            notificationService.deleteConnectionAlerts();
        } catch (Exception e) {
            System.err.println("[UserDashboard] Erreur lors du nettoyage auto: " + e.getMessage());
        }

        // Initialiser les questions
        chatQuestions.add("Quelle est la taille de votre entreprise ?");
        chatQuestions.add("Dans quel secteur d'activité évoluez-vous ?");
        chatQuestions.add("Quel est l'objectif principal de cet audit ?");
        chatQuestions.add("Avez-vous déjà subi des incidents de sécurité ?");
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
        this.currentUser = authService.getCurrentUser();
        
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNom());
            updateNotificationBadge();
            
            // Afficher le dashboard au démarrage
            addAiMessage("Bonjour " + currentUser.getNom() + " ! Je suis votre assistant MindAudit.");
            chatStep = 1;
            askNextChatQuestion();
            showDashboardHome();
            
            // Forcer le badge +1 pour l'alerte de connexion email
            if (notificationBadge != null) {
                notificationBadge.setText("+1");
                notificationBadge.setVisible(true);
                notificationBadge.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 2px 6px; -fx-font-size: 10px; -fx-font-weight: bold;");
            }
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userNameLabel.setText(user.getNom());
            updateNotificationBadge();
        }
    }

    // --- NAVIGATION METHODS ---

    @FXML
    public void showEntrepriseManagement() {
        highlightButton(entrepriseButton);
        loadSection("entreprise-view.fxml");
    }

    @FXML
    public void showDashboardHome() {
        highlightButton(dashboardButton);
        loadSection("user-home.fxml", controller -> {
            if (controller instanceof UserDashboardHomeController) {
                ((UserDashboardHomeController) controller).setDashboardController(this);
            }
        });
    }

    @FXML
    public void showStatistics() {
        highlightButton(statisticsButton);
        loadSection("statistics.fxml");
    }

    @FXML
    public void showUserManagement() {
        highlightButton(usersButton);
        loadSection("user-management.fxml");
    }

    @FXML
    public void showRapportManagement() {
        highlightButton(rapportManagementButton);
        loadSection("rapports_reco/main-view.fxml");
    }

    @FXML
    public void showRapportDashboard() {
        highlightButton(null);
        loadSection("rapports_reco/main-view.fxml", (controller) -> {
            if (controller instanceof MainController) {
                ((MainController) controller).showDashboard();
            }
        });
    }

    @FXML
    public void showRapportList() {
        highlightButton(null);
        loadSection("rapports_reco/main-view.fxml", (controller) -> {
            if (controller instanceof MainController) {
                ((MainController) controller).showAllReports();
            }
        });
    }

    @FXML
    public void handleNewRapportTop() {
        if (currentSectionController instanceof MainController) {
            ((MainController) currentSectionController).ajouterRapport();
        } else {
            showRapportList();
            javafx.application.Platform.runLater(() -> {
                if (currentSectionController instanceof MainController) {
                    ((MainController) currentSectionController).ajouterRapport();
                }
            });
        }
    }

    @FXML
    public void handleExportExcel() {
        if (currentSectionController instanceof MainController) {
            ((MainController) currentSectionController).exporterExcel();
        }
    }

    @FXML
    public void handleExportPDF() {
        if (currentSectionController instanceof MainController) {
            ((MainController) currentSectionController).exporterPDF();
        }
    }

    @FXML
    public void handleScanDocument() {
        highlightButton(null);
        loadSection("rapports_reco/client-view.fxml", (controller) -> {
            if (controller instanceof com.audit.auditaifx.controller.ClientController) {
                ((com.audit.auditaifx.controller.ClientController) controller).showScanView();
            }
        });
    }

    @FXML
    private void showSettings() {
        highlightButton(settingsButton);
        loadSection("profile.fxml");
    }

    @FXML
    public void showReclamations() {
        highlightButton(reclamationButton);
        loadSection("/views/client_main.fxml");
    }

    @FXML
    public void showReclamationChatbot() {
        highlightButton(reclamationChatbotButton);
        loadSection("/views/chatbot.fxml");
    }

    // --- HELPER METHODS ---

    private void loadSection(String fxmlFile) {
        loadSection(fxmlFile, null);
    }

    private void loadSection(String fxmlFile, java.util.function.Consumer<Object> controllerConsumer) {
        try {
            String path = fxmlFile.startsWith("/") ? fxmlFile : "/fxml/" + fxmlFile;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent section = loader.load();
            
            currentSectionController = loader.getController();
            Object controller = currentSectionController;

            if (controller instanceof controlleur.ListRapportsController) {
                ((controlleur.ListRapportsController) controller).setRole("USER");
            } else if (controller instanceof com.audit.auditaifx.controller.MainController) {
                ((com.audit.auditaifx.controller.MainController) controller).setRole("USER");
            }
            
            if (controllerConsumer != null) {
                controllerConsumer.accept(controller);
            }

            if (controller != null && currentUser != null) {
                try {
                    try {
                        controller.getClass().getMethod("setCurrentUser", User.class).invoke(controller, currentUser);
                    } catch (Exception e) {}
                    
                    if (controller instanceof UserDashboardHomeController) {
                        ((UserDashboardHomeController) controller).setDashboardController(this);
                    } else if (controller instanceof ProfileController) {
                        ((ProfileController) controller).setDashboardController(this);
                    } else if (controller instanceof com.audit.auditaifx.controller.ClientController) {
                        ((com.audit.auditaifx.controller.ClientController) controller).setDashboardController(this);
                    }
                } catch (Exception e) {}
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(section);
            
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Erreur", "Impossible de charger la section: " + fxmlFile);
        }
    }

    private void highlightButton(Button button) {
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: CENTER_LEFT; -fx-padding: 14px 20px; -fx-font-size: 13px; -fx-cursor: hand;";
        String activeStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 14px 20px; -fx-font-size: 13px; -fx-cursor: hand; -fx-font-weight: bold;";

        Button[] allButtons = {dashboardButton, entrepriseButton, rapportManagementButton, statisticsButton, usersButton, analyticsButton, reclamationButton, reclamationChatbotButton, settingsButton};
        for (Button b : allButtons) {
            if (b != null) b.setStyle(b == button ? activeStyle : inactiveStyle);
        }
    }

    public void updateNotificationBadge() {
        if (currentUser != null && notificationService != null) {
            int unreadCount = notificationService.getUnreadCount(currentUser.getId());
            if (unreadCount > 0) {
                notificationBadge.setText("+" + unreadCount);
                notificationBadge.setVisible(true);
            } else {
                notificationBadge.setVisible(false);
            }
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showNotificationDropdown() {
        if (currentUser == null || notificationService == null) return;
        
        java.util.List<com.example.mindjavafx.model.Notification> notifs = notificationService.getNotificationsByUserId(currentUser.getId());
        StringBuilder content = new StringBuilder();
        
        if (notifs.isEmpty()) {
            content.append(" Alerte : E-mail de sécurité envoyé à l'administrateur (eleammar21@gmail.com).");
        } else {
            for (com.example.mindjavafx.model.Notification n : notifs) {
                content.append(n.isRead() ? "✓ " : "� ").append(n.getTitle()).append(" : ").append(n.getMessage()).append("\n");
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Notifications de " + currentUser.getNom());
        alert.setContentText(content.toString());
        notificationService.markAllAsRead(currentUser.getId());
        updateNotificationBadge();
        alert.showAndWait();
    }

    private RapportAudit reportForRecommendations = null;

    public void openChatbotWithReport(RapportAudit rapport) {
        this.reportForRecommendations = rapport;
        chatbotWindow.setVisible(true);
        chatbotWindow.setManaged(true);
        addAiMessage("Analyse du rapport : '" + rapport.getTitre() + "' chargée.\n\nVeuillez envoyer votre document scanné pour générer des recommandations IA.");
        chatInputArea.setVisible(false); chatInputArea.setManaged(false);
        chatUploadArea.setVisible(true); chatUploadArea.setManaged(true);
    }

    // --- Floating Chatbot Logic ---
    @FXML
    private void toggleChatbot() {
        boolean isVisible = chatbotWindow.isVisible();
        chatbotWindow.setVisible(!isVisible);
        chatbotWindow.setManaged(!isVisible);
    }

    private void askNextChatQuestion() {
        if (chatStep <= chatQuestions.size()) {
            addAiMessage(chatQuestions.get(chatStep - 1));
        } else {
            addAiMessage("Merci pour vos réponses ! Maintenant, veuillez envoyer votre document (PDF/Excel) pour l'audit.");
            chatInputArea.setVisible(false); chatInputArea.setManaged(false);
            chatUploadArea.setVisible(true); chatUploadArea.setManaged(true);
        }
    }

    @FXML
    private void handleChatSubmit() {
        String input = tfChatInput.getText();
        if (input == null || input.trim().isEmpty()) return;
        tfChatInput.clear();
        addUserMessage(input);
        
        if (chatStep >= 1 && chatStep <= chatQuestions.size()) {
            userAnswers.put(chatStep, input);
            chatStep++;
            askNextChatQuestion();
        }
    }

    @FXML
    private void onUploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner le document");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documents (PDF, Excel)", "*.pdf", "*.xlsx", "*.xls")
        );
        File file = fileChooser.showOpenDialog(chatbotWindow.getScene().getWindow());
        if (file != null) {
            addUserMessage("� " + file.getName());
            
            if (reportForRecommendations != null) {
                // FLOW: ENRICH EXISTING REPORT
                simulateAiTyping("Analyse pour ajout de recommandations au rapport '" + reportForRecommendations.getTitre() + "'...");
                javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        // 1. Add recommendations
                        reportForRecommendations.ajouterRecommandation(new com.audit.auditaifx.model.Recommandation("Correction IA (Scan " + file.getName() + ") : Mettre à jour les pare-feu", "Critique"));
                        reportForRecommendations.ajouterRecommandation(new com.audit.auditaifx.model.Recommandation("IA Recommande : Optimisation des flux", "Moyenne"));
                        
                        // 2. Update the Global Score (Simulated update)
                        reportForRecommendations.setScoreAudit("8,2"); 
                        
                        // 3. Save changes
                        rapportService.modifier(reportForRecommendations);
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    addAiMessage(" Recommandations ajoutées au rapport '" + reportForRecommendations.getTitre() + "'.");
                    addAiMessage("� Nouveau score de performance AI calculé : 8,2/10 (Mise à jour effectuée)");
                    
                    // Force UI update if in MainView
                    javafx.application.Platform.runLater(() -> {
                        if (currentSectionController instanceof com.audit.auditaifx.controller.MainController mc) {
                            mc.showReportDetails(reportForRecommendations);
                        }
                    });
                    
                    reportForRecommendations = null;
                    resetChatToInput();
                });
                new Thread(task).start();
            } else {
                // FLOW: GENERATE NEW REPORT
                simulateAiTyping("Génération d'un nouvel audit à partir de " + file.getName() + "...");
                javafx.concurrent.Task<RapportAudit> task = new javafx.concurrent.Task<>() {
                    @Override
                    protected RapportAudit call() throws Exception {
                        Thread.sleep(2500);
                        
                        // Dynamic score calculation
                        boolean isCritical = file.getName().toLowerCase().contains("critical") || file.getName().toLowerCase().contains("risque1");
                        double score = isCritical ? 1.0 : (6.5 + (new java.util.Random().nextDouble() * 3.0));
                        String formattedScore = String.format("%.1f", score).replace(".", ",");
                        
                        RapportAudit r = new RapportAudit();
                        
                        // Determine Category and Title based on user objectives
                        String goal = userAnswers.getOrDefault(3, "").toLowerCase();
                        String category = "Technical";
                        String titlePrefix = "Rapport de securite";
                        
                        if (goal.contains("secur") || goal.contains("sécur")) {
                            category = "Security";
                            titlePrefix = "Rapport de securite";
                        } else if (goal.contains("conform") || goal.contains("compliance")) {
                            category = "Compliance";
                            titlePrefix = "Regulatory Compliance Audit";
                        } else if (goal.contains("qualit")) {
                            category = "Quality";
                            titlePrefix = "Quality Control Audit";
                        } else {
                            category = "Technical";
                            titlePrefix = "IT Infrastructure Review";
                        }
                        
                        r.setTitre(titlePrefix + " - " + file.getName());
                        r.setAuditeur(category);
                        if (currentUser != null) {
                            r.setEntiteAuditee(currentUser.getNom());
                        } else {
                            r.setEntiteAuditee("Entité Détectée");
                        }
                        r.setScoreAudit(formattedScore);
                        
                        // Generate a smart summary using AIService
                        String aiSummary = aiService.genererResumeAudit(r, userAnswers);
                        r.setDescription(aiSummary);
                        
                        // Custom Recommendations
                        if (isCritical) {
                            r.ajouterRecommandation(new com.audit.auditaifx.model.Recommandation("ACTION IMMÉDIATE : Sécuriser tous les points d'accès critiques", "URGENT"));
                            r.ajouterRecommandation(new com.audit.auditaifx.model.Recommandation("Déployer le plan de réponse aux incidents", "Haute"));
                            
                            // Automatically add risks for score 1
                            r.ajouterRisque(new com.audit.auditaifx.model.Risque("Faille de sécurité critique détectée dans l'infrastructure", "Critique", "Détournement de données possible"));
                            r.ajouterRisque(new com.audit.auditaifx.model.Risque("Non-conformité réglementaire majeure", "Élevé", "Sanctions juridiques potentielles"));
                        } else if (file.getName().toLowerCase().contains("user")) {
                            r.ajouterRecommandation(new com.audit.auditaifx.model.Recommandation("Auditer les droits d'accès des utilisateurs privilégiés", "Haute"));
                            r.ajouterRecommandation(new com.audit.auditaifx.model.Recommandation("Mettre en place une revue trimestrielle des comptes", "Moyenne"));
                        } else {
                            r.ajouterRecommandation(new com.audit.auditaifx.model.Recommandation("Optimiser les configurations de sauvegarde", "Haute"));
                            r.ajouterRecommandation(new com.audit.auditaifx.model.Recommandation("Renforcer le monitoring réseau", "Basse"));
                        }
                        
                        if (!isCritical) {
                            r.ajouterRecommandation(new com.audit.auditaifx.model.Recommandation("Formation des équipes aux risques de phishing", "Moyenne"));
                        }
                        
                        return r;
                    }
                };
                task.setOnSucceeded(e -> {
                    RapportAudit generated = task.getValue();
                    rapportService.ajouter(generated);
                    addAiMessage("✨ Rapport '" + generated.getTitre() + "' généré avec succès !");
                    addAiMessage("📊 Score IA calculé : " + generated.getScoreAudit() + "/10. Catégorie : " + generated.getAuditeur());
                    
                    if (generated.getScoreAudit().equals("1,0")) {
                        addAiMessage("⚠️ ATTENTION : Un score de 1/10 indique des risques critiques. Consultez le tableau des risques immédiatement.");
                    }

                    // Force UI update and open details in the FX thread
                    javafx.application.Platform.runLater(() -> {
                        if (currentSectionController instanceof com.audit.auditaifx.controller.MainController mc) {
                            mc.rafraichirListe(); // Refresh the table so the new report is visible
                        } else if (currentSectionController instanceof com.audit.auditaifx.controller.ClientController cc) {
                            cc.showAllReports(); // Refresh the list view
                        }
                    });
                    
                    resetChatToInput();
                });
                new Thread(task).start();
            }
        }
    }

    private void resetChatToInput() {
        chatUploadArea.setVisible(false); chatUploadArea.setManaged(false);
        chatInputArea.setVisible(true); chatInputArea.setManaged(true);
        chatStep = 0;
    }

    private void addUserMessage(String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-background-color: white; -fx-text-fill: #2c3e50; -fx-padding: 8px; -fx-background-radius: 10px;");
        javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(lbl);
        hbox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        chatMessages.getChildren().add(hbox);
        scrollToBottom();
    }

    private void addAiMessage(String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-padding: 8px; -fx-background-radius: 10px;");
        javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(lbl);
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        chatMessages.getChildren().add(hbox);
        scrollToBottom();
    }

    private void simulateAiTyping(String msg) { addAiMessage(msg); }

    private void scrollToBottom() {
        chatScroll.layout();
        chatScroll.setVvalue(1.0);
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MindAudit - Connexion");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
