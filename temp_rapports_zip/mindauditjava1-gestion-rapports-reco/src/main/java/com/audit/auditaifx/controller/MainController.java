package com.audit.auditaifx.controller;

import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.Recommandation;
import com.audit.auditaifx.model.Risque;
import com.audit.auditaifx.model.StatutRapport;
import com.audit.auditaifx.service.AIService;

import com.audit.auditaifx.service.RapportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

public class MainController {

    // --- Sidebar & Views ---
    @FXML
    private StackPane paneNotifIcon;
    @FXML
    private Label lblNotifCount;
    private javafx.scene.control.ContextMenu notifMenu = new javafx.scene.control.ContextMenu();
    @FXML
    private ScrollPane dashboardView;
    @FXML
    private VBox allReportsView;
    @FXML
    private ScrollPane detailView;

    // --- Dashboard Analytics ---
    @FXML
    private PieChart chartStatut;
    @FXML
    private BarChart<String, Number> chartPriorite;
    @FXML
    private LineChart<String, Number> chartTendance;
    @FXML
    private VBox vboxRecentReports;

    // --- AI Recommendation Card ---
    @FXML
    private Label lblAIRapportTitre;
    @FXML
    private Label lblAIRaison;
    @FXML
    private Label lblAIPriorityBadge;
    @FXML
    private Label lblScoreAI;
    @FXML
    private Label lblAIDetail;
    @FXML
    private ProgressBar progressPrioriteAI;
    @FXML
    private Button btnVoirRapportAI;
    @FXML
    private Button btnRefreshAI;

    private RapportAudit rapportPrioritaireIA = null;

    // --- Rapports Table (Full List Page) ---
    @FXML
    private TableView<RapportAudit> rapportsTable;
    @FXML
    private TableColumn<RapportAudit, String> colTitre;
    @FXML
    private TableColumn<RapportAudit, String> colAuditeur;
    @FXML
    private TableColumn<RapportAudit, String> colEntite;
    @FXML
    private TableColumn<RapportAudit, String> colStatut;
    @FXML
    private TableColumn<RapportAudit, String> colDate;
    @FXML
    private TableColumn<RapportAudit, Integer> colNbReco;
    @FXML
    private TableColumn<RapportAudit, Void> colActionsRapport;
    @FXML
    private TextField txtSearchReports;
    @FXML
    private ComboBox<String> comboFilterStatut;
    @FXML
    private ComboBox<String> comboFilterNom;

    // --- Pagination ---
    @FXML private Button btnPrevPage;
    @FXML private Button btnNextPage;
    @FXML private Label lblPageInfo;
    @FXML private Label lblTotalFiltered;
    private static final int PAGE_SIZE = 8;
    private int currentPage = 0;
    private List<RapportAudit> filteredList = new java.util.ArrayList<>();

    // --- Score bar ---
    @FXML private ProgressBar progressResolution;
    @FXML private Label lblScoreResolution;
    @FXML private Label lblRecoStats;

    // --- Dashboard Stats ---
    @FXML
    private Label lblDashTotalRapports;
    @FXML
    private Label lblDashTotalReco;
    @FXML
    private Label lblDashTaux;
    @FXML
    private ProgressIndicator progressIndicator;

    // --- Selected Report Info ---
    @FXML
    private Label lblSelectedTitre;
    @FXML
    private Label lblSelectedSub;
    @FXML
    private Label txtDescriptionRapport;
    @FXML
    private Label lblStatutBadge;
    @FXML
    private Label lblDateCreation;
    @FXML
    private Label lblInfoEntite;
    @FXML
    private Label lblInfoAuditeur;

    // --- Recommandations ---
    @FXML
    private TableView<Recommandation> recoTable;
    @FXML
    private TableColumn<Recommandation, String> colRecoDesc;
    @FXML
    private TableColumn<Recommandation, String> colRecoPriorite;
    @FXML
    private TableColumn<Recommandation, Boolean> colRecoResolue;
    @FXML
    private TableColumn<Recommandation, Void> colActionsReco;

    // --- Risques Table ---
    @FXML private TableView<Risque> risqueTable;
    @FXML private TableColumn<Risque, Void> colRisqueAlerte;
    @FXML private TableColumn<Risque, String> colRisqueNiveau;
    @FXML private TableColumn<Risque, String> colRisqueDesc;
    @FXML private TableColumn<Risque, String> colRisqueImpact;
    @FXML private TableColumn<Risque, Void> colActionsRisque;
    @FXML private Label lblRisqueCount;

    // --- Labels + Boutons ---
    @FXML
    private Label lblRecommandations;
    @FXML
    private Label lblTotalRapports;
    @FXML
    private Label lblTotalReco;
    @FXML
    private Button btnAjouterReco;
    @FXML
    private Button btnAnalyserIA;
    @FXML
    private Button btnDetecterRisques;
    @FXML
    private Label lblScoreBadge;

    // --- Services ---
    private RapportService service = new RapportService();
    private AIService aiService = new AIService();

    private RapportAudit rapportSelectionne = null;

    @FXML
    public void initialize() {
        System.out.println("DEBUG Admin: Démarrage de initialize Multi-Page...");

        // Config table rapports (La "page" liste)
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuditeur.setCellValueFactory(new PropertyValueFactory<>("auditeur"));
        colEntite.setCellValueFactory(new PropertyValueFactory<>("entiteAuditee"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colNbReco.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getRecommandations().size()).asObject());

        // Boutons actions dans la table
        ajouterBoutonsRapportsTable();

        // Écouteur de sélection (Simple sélection, pas de changement de vue)
        rapportsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            rapportSelectionne = newVal;
        });

        // Colonnes recommandations (La "page" détails)
        colRecoDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colRecoPriorite.setCellValueFactory(new PropertyValueFactory<>("priorite"));
        colRecoResolue.setCellValueFactory(new PropertyValueFactory<>("resolue"));

        // Boutons actions recommandations
        ajouterBoutonsReco();

        // Colonnes risques
        colRisqueNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        colRisqueDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colRisqueImpact.setCellValueFactory(new PropertyValueFactory<>("impact"));
        ajouterIconeAlerteRisque();
        ajouterBoutonRisque();

        // Filtre statut
        comboFilterStatut.getItems().addAll("Tous", "BROUILLON", "EN_COURS", "FINALISE");
        comboFilterStatut.setValue("Tous");
        comboFilterStatut.setOnAction(e -> appliquerFiltreEtPagination());

        // Filtre nom (Auditeur)
        comboFilterNom.getItems().add("Tous");
        comboFilterNom.setValue("Tous");
        comboFilterNom.setOnAction(e -> appliquerFiltreEtPagination());

        // Recherche dynamique
        txtSearchReports.textProperty().addListener((obs, oldText, newText) -> appliquerFiltreEtPagination());

        // Charger données
        rafraichirListe();
        showDashboard();
    }

    private void updateFiltreNomList() {
        String current = comboFilterNom.getValue();
        comboFilterNom.getItems().clear();
        comboFilterNom.getItems().add("Tous");
        
        // Extract unique auditors
        java.util.Set<String> auditeurs = service.getTous().stream()
            .map(RapportAudit::getAuditeur)
            .filter(a -> a != null && !a.isBlank())
            .collect(Collectors.toSet());
            
        comboFilterNom.getItems().addAll(auditeurs);
        if (comboFilterNom.getItems().contains(current)) {
            comboFilterNom.setValue(current);
        } else {
            comboFilterNom.setValue("Tous");
        }
    }

    private void appliquerFiltreEtPagination() {
        String query = txtSearchReports.getText() != null ? txtSearchReports.getText().toLowerCase() : "";
        String statut = comboFilterStatut.getValue();
        String nom = comboFilterNom.getValue();

        filteredList = service.getTous().stream()
            .filter(r -> {
                boolean matchSearch = query.isEmpty()
                    || r.getTitre().toLowerCase().contains(query)
                    || r.getEntiteAuditee().toLowerCase().contains(query)
                    || r.getAuditeur().toLowerCase().contains(query);
                boolean matchStatut = statut == null || statut.equals("Tous")
                    || r.getStatut().name().equals(statut);
                boolean matchNom = nom == null || nom.equals("Tous")
                    || (r.getAuditeur() != null && r.getAuditeur().equals(nom));
                return matchSearch && matchStatut && matchNom;
            })
            .collect(Collectors.toList());

        currentPage = 0;
        afficherPageCourante();
    }

    private void afficherPageCourante() {
        int total = filteredList.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, total);

        ObservableList<RapportAudit> page = FXCollections.observableArrayList(
            filteredList.subList(from, to));
        rapportsTable.setItems(page);

        lblPageInfo.setText("Page " + (currentPage + 1) + " / " + totalPages);
        lblTotalFiltered.setText(total + " rapport(s) trouvé(s)");
        btnPrevPage.setDisable(currentPage == 0);
        btnNextPage.setDisable(currentPage >= totalPages - 1);
    }

    @FXML
    public void pagePrev() {
        if (currentPage > 0) { currentPage--; afficherPageCourante(); }
    }

    @FXML
    public void pageNext() {
        int totalPages = (int) Math.ceil((double) filteredList.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) { currentPage++; afficherPageCourante(); }
    }

    private void filtrerRapports(String query) {
        appliquerFiltreEtPagination();
    }

    @FXML
    public void resetFilters() {
        txtSearchReports.clear();
        comboFilterStatut.setValue("Tous");
        comboFilterNom.setValue("Tous");
        appliquerFiltreEtPagination();
    }

    private void ajouterBoutonsRapportsTable() {
        colActionsRapport.setCellFactory(param -> new TableCell<>() {
            private final Button btnVoir = new Button("Voir");
            private final Button btnEdit = new Button("Edit");
            private final Button btnDel = new Button("Supp");
            private final HBox container = new HBox(5, btnVoir, btnEdit, btnDel);

            {
                btnVoir.setStyle(
                        "-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnEdit.setStyle(
                        "-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnDel.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");

                btnVoir.setFocusTraversable(false);
                btnEdit.setFocusTraversable(false);
                btnDel.setFocusTraversable(false);

                btnVoir.setOnAction(e -> {
                    RapportAudit r = getTableView().getItems().get(getIndex());
                    rapportSelectionne = r;
                    showReportDetails(r);
                });
                btnEdit.setOnAction(e -> {
                    RapportAudit r = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireRapport(r);
                });
                btnDel.setOnAction(e -> {
                    RapportAudit r = getTableView().getItems().get(getIndex());
                    supprimerRapport(r);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void supprimerRapport(RapportAudit rapport) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le rapport ?");
        confirm.setContentText("« " + rapport.getTitre() + " » sera supprimé définitivement.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.supprimer(rapport);
                rafraichirListe();
            }
        });
    }

    @FXML
    public void showAllReports() {
        dashboardView.setVisible(false);
        detailView.setVisible(false);
        allReportsView.setVisible(true);
        rapportsTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void showDashboard() {
        dashboardView.setVisible(true);
        detailView.setVisible(false);
        allReportsView.setVisible(false);
        mettreAJourCompteurs();

        ObservableList<RapportAudit> rapports = service.getTous();

        // Lancer l'analyse IA de priorité
        afficherRecommandationIA(rapports);
        // 1. Statistiques Globales
        int totalReco = 0;
        int resolues = 0;
        int statusBrouillon = 0, statusEnCours = 0, statusTermine = 0;
        int pHaute = 0, pMoyenne = 0, pBasse = 0;

        for (RapportAudit r : rapports) {
            totalReco += r.getRecommandations().size();
            for (Recommandation reco : r.getRecommandations()) {
                if (reco.isResolue())
                    resolues++;
                String p = reco.getPriorite().toLowerCase();
                if (p.contains("haut"))
                    pHaute++;
                else if (p.contains("moy"))
                    pMoyenne++;
                else
                    pBasse++;
            }

            switch (r.getStatut()) {
                case BROUILLON -> statusBrouillon++;
                case EN_COURS -> statusEnCours++;
                case FINALISE -> statusTermine++;
            }
        }

        lblDashTotalRapports.setText(String.valueOf(rapports.size()));
        lblDashTotalReco.setText(String.valueOf(totalReco));
        lblDashTaux.setText(totalReco > 0 ? (resolues * 100 / totalReco) + "%" : "0%");

        // 2. PieChart Statut
        chartStatut.getData().clear();
        chartStatut.getData().add(new PieChart.Data("Brouillon", statusBrouillon));
        chartStatut.getData().add(new PieChart.Data("En Cours", statusEnCours));
        chartStatut.getData().add(new PieChart.Data("Finalisé", statusTermine));

        // 3. BarChart Priorité
        chartPriorite.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Haute", pHaute));
        series.getData().add(new XYChart.Data<>("Moyenne", pMoyenne));
        series.getData().add(new XYChart.Data<>("Basse", pBasse));
        chartPriorite.getData().add(series);

        // 3.5 LineChart Tendance des Créations
        if (chartTendance != null) {
            chartTendance.getData().clear();
            XYChart.Series<String, Number> seriesTendance = new XYChart.Series<>();
            
            java.util.Map<String, Long> countParDate = rapports.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    r -> r.getDateCreation().toString(), 
                    java.util.stream.Collectors.counting()
                ));
                
            countParDate.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .forEach(e -> seriesTendance.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));
                
            chartTendance.getData().add(seriesTendance);
        }

        // 4. Activités Récentes (3 derniers rapports)
        vboxRecentReports.getChildren().clear();
        List<RapportAudit> recent = rapports.stream()
                .sorted((r1, r2) -> r2.getDateCreation().compareTo(r1.getDateCreation()))
                .limit(3)
                .toList();

        for (RapportAudit r : recent) {
            HBox item = new HBox(15);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setStyle(
                    "-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-background-radius: 8; -fx-border-color: #eee; -fx-border-radius: 8;");

            VBox txt = new VBox(2);
            Label titre = new Label(r.getTitre());
            titre.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            Label date = new Label("Créé le " + r.getDateCreation() + " | " + r.getStatut());
            date.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
            txt.getChildren().addAll(titre, date);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button btn = new Button("Voir");
            btn.setStyle(
                    "-fx-background-color: white; -fx-border-color: #3498db; -fx-text-fill: #3498db; -fx-border-radius: 5;");
            btn.setFocusTraversable(false);
            btn.setOnAction(e -> {
                rapportSelectionne = r;
                showReportDetails(r);
            });

            item.getChildren().addAll(txt, spacer, btn);
            vboxRecentReports.getChildren().add(item);
        }
    }

    private void rafraichirListe() {
        updateFiltreNomList();
        appliquerFiltreEtPagination();
        mettreAJourCompteurs();
    }

    private void showReportDetails(RapportAudit rapport) {
        dashboardView.setVisible(false);
        allReportsView.setVisible(false);
        detailView.setVisible(true);

        // En-tête
        lblSelectedTitre.setText(rapport.getTitre());
        lblInfoEntite.setText(rapport.getEntiteAuditee());
        lblInfoAuditeur.setText(rapport.getAuditeur());
        lblDateCreation.setText(rapport.getDateCreation() != null ? rapport.getDateCreation().toString() : "-");
        lblSelectedSub.setText(rapport.getDateMiseAJour() != null ? rapport.getDateMiseAJour().toString() : "-");

        // Description complète (Label wrappé, pas de scroll)
        String desc = rapport.getDescription();
        txtDescriptionRapport.setText(desc != null && !desc.isBlank() ? desc : "Aucune description disponible.");

        // Afficher score si présent
        if (rapport.getScoreAudit() != null && !rapport.getScoreAudit().isEmpty()) {
            afficherBadgeScore(rapport.getScoreAudit());
        } else {
            btnDetecterRisques.setVisible(true);
            btnDetecterRisques.setManaged(true);
            lblScoreBadge.setVisible(false);
            lblScoreBadge.setManaged(false);
        }

        // Badge statut coloré
        lblStatutBadge.setText(rapport.getStatut().name().replace("_", " "));
        switch (rapport.getStatut()) {
            case EN_COURS  -> lblStatutBadge.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 3 12; -fx-font-size: 11px; -fx-font-weight: bold;");
            case FINALISE  -> lblStatutBadge.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 3 12; -fx-font-size: 11px; -fx-font-weight: bold;");
            case BROUILLON -> lblStatutBadge.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 3 12; -fx-font-size: 11px; -fx-font-weight: bold;");
            default        -> lblStatutBadge.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 3 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        recoTable.setItems(FXCollections.observableArrayList(rapport.getRecommandations()));
        mettreAJourScoreResolution(rapport);

        // Rafraîchir risques depuis la BD
        List<Risque> risques = service.getRisques(rapport.getId());
        rapport.setRisques(risques);
        rafraichirTableauRisques(rapport);

        btnAjouterReco.setDisable(false);
        btnAnalyserIA.setDisable(false);
        btnDetecterRisques.setDisable(false);
    }

    private void afficherRecommandationIA(ObservableList<RapportAudit> rapports) {
        if (rapports == null || rapports.isEmpty()) {
            lblAIRapportTitre.setText("Aucun rapport disponible.");
            lblAIRaison.setText("");
            btnVoirRapportAI.setDisable(true);
            return;
        }
        // Lancer dans un thread pour ne pas bloquer l'UI
        lblAIRapportTitre.setText("Analyse en cours...");
        lblAIRaison.setText("");
        progressPrioriteAI.setProgress(-1); // indeterminate
        btnRefreshAI.setDisable(true);
        btnVoirRapportAI.setDisable(true);

        javafx.concurrent.Task<AIService.PrioriteResult> task = new javafx.concurrent.Task<>() {
            @Override
            protected AIService.PrioriteResult call() {
                return aiService.calculeRapportPrioritaire(new java.util.ArrayList<>(rapports));
            }
        };
        task.setOnSucceeded(ev -> {
            AIService.PrioriteResult result = task.getValue();
            if (result == null) {
                lblAIRapportTitre.setText("Aucun rapport à prioriser.");
                lblAIRaison.setText("");
                progressPrioriteAI.setProgress(0);
                return;
            }
            rapportPrioritaireIA = result.rapport;
            lblAIRapportTitre.setText(result.rapport.getTitre());
            lblAIRaison.setText(result.rapport.getEntiteAuditee() + " — Audité par " + result.rapport.getAuditeur());
            lblAIDetail.setText(result.raison);

            // Score max théorique: calculé dynamiquement
            int maxScore = Math.max(result.score, 1);
            // On cap à 100
            int pct = Math.min(result.score * 100 / Math.max(maxScore * 2, 20), 100);
            progressPrioriteAI.setProgress(pct / 100.0);
            lblScoreAI.setText(result.score + " pts");

            // Couleur badge selon urgence
            if (result.score >= 15) {
                lblAIPriorityBadge.setText("🔴");
                progressPrioriteAI.setStyle("-fx-accent: #e74c3c;");
                lblScoreAI.setStyle("-fx-font-size: 11; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else if (result.score >= 8) {
                lblAIPriorityBadge.setText("🟠");
                progressPrioriteAI.setStyle("-fx-accent: #e67e22;");
                lblScoreAI.setStyle("-fx-font-size: 11; -fx-text-fill: #e67e22; -fx-font-weight: bold;");
            } else {
                lblAIPriorityBadge.setText("🟡");
                progressPrioriteAI.setStyle("-fx-accent: #f1c40f;");
                lblScoreAI.setStyle("-fx-font-size: 11; -fx-text-fill: #d4ac0d; -fx-font-weight: bold;");
            }

            btnVoirRapportAI.setDisable(false);
            btnRefreshAI.setDisable(false);
        });
        task.setOnFailed(ev -> {
            lblAIRapportTitre.setText("Erreur d'analyse IA.");
            progressPrioriteAI.setProgress(0);
            btnRefreshAI.setDisable(false);
        });
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    public void refreshAIRecommendation() {
        afficherRecommandationIA(service.getTous());
    }

    @FXML
    public void voirRapportPrioritaire() {
        if (rapportPrioritaireIA != null) {
            rapportSelectionne = rapportPrioritaireIA;
            showReportDetails(rapportPrioritaireIA);
        }
    }

    private void rafraichirTableauRisques(RapportAudit rapport) {
        List<Risque> risques = rapport.getRisques();
        risqueTable.setItems(FXCollections.observableArrayList(risques));
        int nb = risques.size();
        lblRisqueCount.setText(String.valueOf(nb));
        // Badge rouge si risques présents, gris sinon
        if (nb > 0) {
            lblRisqueCount.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            lblRisqueCount.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void modifierRapportSelectionne() {
        if (rapportSelectionne != null) {
            ouvrirFormulaireRapport(rapportSelectionne);
        }
    }

    @FXML
    public void supprimerRapportSelectionne() {
        if (rapportSelectionne != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer le rapport ?");
            confirm.setContentText(rapportSelectionne.getTitre());
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    service.supprimer(rapportSelectionne);
                    rapportSelectionne = null;
                    rafraichirListe();
                    showAllReports();
                }
            });
        }
    }

    // ─── CRUD Rapports ────────────────────────────────────────

    @FXML
    public void ajouterRapport() {
        ouvrirFormulaireRapport(null);
    }

    private void ouvrirFormulaireRapport(RapportAudit rapport) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/audit/auditaifx/rapport-form-view.fxml"));
            Parent root = loader.load();

            RapportFormController controller = loader.getController();
            controller.setService(service);
            controller.setRapport(rapport);
            controller.setOnSave(() -> {
                rafraichirListe();
                if (rapportSelectionne != null) {
                    showReportDetails(rapportSelectionne);
                } else {
                    showAllReports();
                }
            });

            Stage stage = new Stage();
            stage.setTitle(rapport == null ? "Ajouter un rapport" : "Modifier le rapport");
            stage.setScene(new Scene(root, 500, 450));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            afficherErreur("Erreur ouverture formulaire : " + e.getMessage());
        }
    }

    private void voirDetailsRapport(RapportAudit rapport) {
        if (rapport == null)
            return;

        Alert details = new Alert(Alert.AlertType.INFORMATION);
        details.setTitle("Détails du Rapport");
        details.setHeaderText(rapport.getTitre());
        details.setContentText(
                "🏢 Entité: " + rapport.getEntiteAuditee() + "\n" +
                        "👤 Auditeur: " + rapport.getAuditeur() + "\n" +
                        "📅 Date: " + rapport.getDateCreation() + "\n" +
                        "📊 Statut: " + rapport.getStatut() + "\n\n" +
                        "📝 Description:\n"
                        + (rapport.getDescription() != null ? rapport.getDescription() : "Aucune description"));
        details.getDialogPane().setPrefWidth(450);
        details.show();
    }

    @FXML
    public void exporterExcel() {
        ObservableList<RapportAudit> rapports = rapportsTable.getItems();
        if (rapports.isEmpty()) {
            afficherErreur("Aucun rapport à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Rapports_Audit_Complet.xlsx");

        File file = fileChooser.showSaveDialog(dashboardView.getScene().getWindow());
        if (file == null)
            return;

        try (Workbook workbook = new XSSFWorkbook();
                FileOutputStream fileOut = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("Rapports");

            // Style d'en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // En-têtes
            String[] headers = { "Titre", "Auditeur", "Entité", "Statut", "Date Création", "Nb Reco" };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Données
            int rowNum = 1;
            for (RapportAudit r : rapports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getTitre());
                row.createCell(1).setCellValue(r.getAuditeur());
                row.createCell(2).setCellValue(r.getEntiteAuditee());
                row.createCell(3).setCellValue(r.getStatut().toString());
                row.createCell(4).setCellValue(r.getDateCreation().toString());
                row.createCell(5).setCellValue(r.getRecommandations().size());
            }

            // Auto-resize colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Réussi");
            alert.setHeaderText(null);
            alert.setContentText("Le fichier Excel complet a été généré avec succès !");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de l'export Excel : " + e.getMessage());
        }
    }

    @FXML
    public void exporterPDF() {
        if (rapportSelectionne == null) {
            afficherErreur("Veuillez sélectionner un rapport dans le tableau pour l'exporter en PDF.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter le Rapport en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        String safeTitre = rapportSelectionne.getTitre().replaceAll("[^a-zA-Z0-9]", "_");
        fileChooser.setInitialFileName("Rapport_Audit_" + safeTitre + ".pdf");

        File file = fileChooser.showSaveDialog(dashboardView.getScene().getWindow());
        if (file == null)
            return;

        try {
            com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Polices
            com.lowagie.text.Font titleFont = com.lowagie.text.FontFactory.getFont(
                    com.lowagie.text.FontFactory.HELVETICA_BOLD, 18, com.lowagie.text.Font.BOLD, Color.DARK_GRAY);
            com.lowagie.text.Font headerFont = com.lowagie.text.FontFactory
                    .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 12, com.lowagie.text.Font.BOLD, Color.WHITE);
            com.lowagie.text.Font normalFont = com.lowagie.text.FontFactory
                    .getFont(com.lowagie.text.FontFactory.HELVETICA, 11);
            com.lowagie.text.Font boldFont = com.lowagie.text.FontFactory
                    .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 11);

            // Titre
            Paragraph pTitle = new Paragraph("RAPPORT D'AUDIT", titleFont);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);
            document.add(new Paragraph(" ")); // Espace

            // Infos du rapport
            document.add(new Paragraph("Titre: " + rapportSelectionne.getTitre(), boldFont));
            document.add(new Paragraph("Auditeur: " + rapportSelectionne.getAuditeur(), normalFont));
            document.add(new Paragraph("Entité: " + rapportSelectionne.getEntiteAuditee(), normalFont));
            document.add(new Paragraph("Statut: " + rapportSelectionne.getStatut(), normalFont));
            document.add(new Paragraph("Date: " + rapportSelectionne.getDateCreation(), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Description:", boldFont));
            document.add(new Paragraph(rapportSelectionne.getDescription(), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("────────────────────────────────────────────────────────────────────────"));
            document.add(new Paragraph(" "));

            // Recommandations
            document.add(new Paragraph("RECOMMANDATIONS",
                    com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // En-têtes de table
            String[] headers = { "Description", "Priorité", "Résolue" };
            for (String h : headers) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(Color.GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Données de table
            for (Recommandation reco : rapportSelectionne.getRecommandations()) {
                table.addCell(new Phrase(reco.getDescription(), normalFont));
                table.addCell(new Phrase(reco.getPriorite(), normalFont));
                table.addCell(new Phrase(reco.isResolue() ? "OUI" : "NON", normalFont));
            }

            document.add(table);
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF Réussi");
            alert.setHeaderText(null);
            alert.setContentText("Le rapport PDF a été généré avec succès !");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de l'export PDF : " + e.getMessage());
        }
    }

    // ─── CRUD Recommandations ─────────────────────────────────

    @FXML
    public void ajouterRecommandation() {
        if (rapportSelectionne != null)
            ouvrirFormulaireReco(null);
    }

    private void modifierRecommandation(Recommandation reco) {
        ouvrirFormulaireReco(reco);
    }

    private void supprimerRecommandation(Recommandation reco) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la recommandation ?");
        confirm.setContentText(reco.getDescription());
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.supprimerRecommandation(reco.getId());
                rapportSelectionne.getRecommandations().remove(reco);
                recoTable.setItems(FXCollections.observableArrayList(
                        rapportSelectionne.getRecommandations()));
                rafraichirListe();
                mettreAJourCompteurs();
            }
        });
    }

    private void ouvrirFormulaireReco(Recommandation reco) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/audit/auditaifx/reco-form-view.fxml"));
            Parent root = loader.load();

            RecoFormController controller = loader.getController();
            controller.setService(service);
            controller.setRapport(rapportSelectionne);
            controller.setRecommandation(reco);
            controller.setOnSave(() -> {
                rapportSelectionne.setRecommandations(service.getRecommandations(rapportSelectionne.getId()));
                recoTable.setItems(FXCollections.observableArrayList(
                        rapportSelectionne.getRecommandations()));
                rafraichirListe();
                mettreAJourCompteurs();
            });

            Stage stage = new Stage();
            stage.setTitle(reco == null ? "Ajouter une recommandation" : "Modifier");
            stage.setScene(new Scene(root, 450, 320));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            afficherErreur("Erreur ouverture formulaire : " + e.getMessage());
        }
    }

    @FXML
    public void genererRecoIA() {
        if (rapportSelectionne == null)
            return;

        // Désactiver le bouton pendant le chargement
        btnAnalyserIA.setDisable(true);
        btnAjouterReco.setDisable(true);

        // Créer une tâche de fond pour l'analyse IA
        javafx.concurrent.Task<Recommandation> task = new javafx.concurrent.Task<>() {
            @Override
            protected Recommandation call() throws Exception {
                // Simulation d'un temps de réflexion IA (1.5 seconde)
                Thread.sleep(1500);
                return aiService.genererRecommandation(rapportSelectionne);
            }
        };

        // Succès de la tâche
        task.setOnSucceeded(e -> {
            Recommandation nouvelleReco = task.getValue();
            if (nouvelleReco != null) {
                service.ajouterRecommandation(rapportSelectionne.getId(), nouvelleReco);
                rapportSelectionne.setRecommandations(service.getRecommandations(rapportSelectionne.getId()));

                // Mise à jour UI
                recoTable.setItems(FXCollections.observableArrayList(rapportSelectionne.getRecommandations()));
                rafraichirListe();
                showReportDetails(rapportSelectionne);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("IA AuditAI");
                info.setHeaderText("Recommandation Générée");
                info.setContentText("L'IA a terminé l'analyse. Une nouvelle recommandation a été ajoutée.");
                info.show();
            }
            btnAnalyserIA.setDisable(false);
            btnAjouterReco.setDisable(false);
        });

        // Erreur pendant la tâche
        task.setOnFailed(ev -> {
            btnAnalyserIA.setDisable(false);
            btnAjouterReco.setDisable(false);
            afficherErreur("Erreur lors de l'analyse IA : " + task.getException().getMessage());
        });

        // Lancer la tâche
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void calculerScoreAudit() {
        if (rapportSelectionne == null) return;

        btnDetecterRisques.setDisable(true);
        btnDetecterRisques.setText("⏳ Analyse...");

        // Utilisation du service local (Mock) au lieu de Gemini
        String json = aiService.calculerScoreAudit(rapportSelectionne);
        
        // Simuler un court délai pour l'effet "analyse"
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(e -> {
            btnDetecterRisques.setDisable(false);
            btnDetecterRisques.setText("📊 Score Rapport");

            // Sauvegarder dans l'objet et la DB
            rapportSelectionne.setScoreAudit(json);
            service.modifier(rapportSelectionne);

            // Afficher le badge et ouvrir le modal
            afficherBadgeScore(json);
            ouvrirModalScore(json);
        });
        pause.play();
    }

    private void afficherBadgeScore(String json) {
        try {
            com.google.gson.JsonObject data = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
            double score = data.get("global").getAsDouble();
            
            lblScoreBadge.setText("Score Rapport: " + String.format("%.1f", score) + "/10");
            lblScoreBadge.getStyleClass().removeAll("score-badge-green", "score-badge-orange", "score-badge-red");
            
            if (score >= 7) lblScoreBadge.getStyleClass().add("score-badge-green");
            else if (score >= 4) lblScoreBadge.getStyleClass().add("score-badge-orange");
            else lblScoreBadge.getStyleClass().add("score-badge-red");

            btnDetecterRisques.setVisible(false);
            btnDetecterRisques.setManaged(false);
            lblScoreBadge.setVisible(true);
            lblScoreBadge.setManaged(true);
            
            // Permettre de cliquer sur le badge pour revoir les détails
            lblScoreBadge.setOnMouseClicked(e -> ouvrirModalScore(json));
            lblScoreBadge.setCursor(javafx.scene.Cursor.HAND);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ouvrirModalScore(String json) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/audit/auditaifx/score-modal.fxml"));
            javafx.scene.Parent root = loader.load();
            
            ScoreModalController controller = loader.getController();
            controller.setScoreData(json);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Détails du Score Audit");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void genererRisquesIA() {
        if (rapportSelectionne == null)
            return;

        btnDetecterRisques.setDisable(true);

        javafx.concurrent.Task<Risque> task = new javafx.concurrent.Task<>() {
            @Override
            protected Risque call() throws Exception {
                Thread.sleep(1500);
                return aiService.genererRisque(rapportSelectionne);
            }
        };

        task.setOnSucceeded(e -> {
            Risque r = task.getValue();
            if (r != null) {
                service.ajouterRisque(rapportSelectionne.getId(), r);
                rapportSelectionne.getRisques().add(r);

                // Rafraîchir le tableau des risques en temps réel
                rafraichirTableauRisques(rapportSelectionne);

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("⚠ Analyse de Risques IA");
                alert.setHeaderText("Risque détecté : " + r.getNiveau());
                alert.setContentText("Description : " + r.getDescription() + "\n\nImpact : " + r.getImpact());
                alert.getDialogPane().setMinWidth(400);
                alert.show();
            }
            btnDetecterRisques.setDisable(false);
        });

        new Thread(task).start();
    }

    private void ajouterBoutonRisque() {
        if (colActionsRisque == null) return;
        colActionsRisque.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");

            {
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px;");
                btnDelete.setOnAction(event -> {
                    Risque risque = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Suppression Risque");
                    confirm.setHeaderText("Supprimer ce risque ?");
                    confirm.setContentText(risque.getDescription());
                    confirm.showAndWait().ifPresent(res -> {
                        if (res == ButtonType.OK) {
                            service.supprimerRisque(risque.getId());
                            if (rapportSelectionne != null) {
                                rapportSelectionne.setRisques(service.getRisques(rapportSelectionne.getId()));
                                rafraichirTableauRisques(rapportSelectionne);
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(btnDelete);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    private void ajouterIconeAlerteRisque() {
        colRisqueAlerte.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                Risque r = getTableView().getItems().get(getIndex());
                Label icon = new Label();
                icon.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                switch (r.getNiveau() != null ? r.getNiveau().toLowerCase() : "") {
                    case "critique" -> { icon.setText("🔴"); icon.setStyle("-fx-font-size:16px;"); }
                    case "élevé", "eleve" -> { icon.setText("🟠"); }
                    case "moyen" -> { icon.setText("🟡"); }
                    default -> { icon.setText("🟢"); }
                }
                setGraphic(icon);
                setStyle("-fx-alignment: CENTER;");
            }
        });
    }

    // ─── Boutons dans les tableaux ────────────────────────────

    private void ajouterBoutonsReco() {
        colActionsReco.setCellFactory(col -> new TableCell<>() {
            private final Button btnToggle = new Button();
            private final Button btnEdit = new Button("Edit");
            private final Button btnDel = new Button("Supp");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(5, btnToggle, btnEdit, btnDel);

            {
                btnEdit.setStyle(
                        "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnDel.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");

                btnToggle.setFocusTraversable(false);
                btnEdit.setFocusTraversable(false);
                btnDel.setFocusTraversable(false);

                btnToggle.setOnAction(e -> {
                    Recommandation reco = getTableView().getItems().get(getIndex());
                    boolean oldStatus = reco.isResolue();
                    reco.setResolue(!oldStatus);
                    System.out.println("DEBUG Admin: Toggle reco " + reco.getId() + " from " + oldStatus + " to "
                            + reco.isResolue());

                    service.modifierRecommandation(reco);

                    // Refresh data
                    if (rapportSelectionne != null) {
                        rapportSelectionne.setRecommandations(service.getRecommandations(rapportSelectionne.getId()));
                        recoTable.setItems(FXCollections.observableArrayList(rapportSelectionne.getRecommandations()));
                        mettreAJourScoreResolution(rapportSelectionne);
                    }

                    getTableView().refresh();
                    mettreAJourCompteurs();
                });

                btnEdit.setOnAction(e -> modifierRecommandation(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> supprimerRecommandation(getTableView().getItems().get(getIndex())));
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Recommandation reco = getTableView().getItems().get(getIndex());
                    if (reco.isResolue()) {
                        btnToggle.setText("Ouvrir");
                        btnToggle.setStyle(
                                "-fx-background-color: #bdc3c7; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11px;");
                    } else {
                        btnToggle.setText("Résoudre");
                        btnToggle.setStyle(
                                "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");
                    }
                    setGraphic(box);
                }
            }
        });
    }

    // ─── Utilitaires ──────────────────────────────────────────

    private void mettreAJourCompteurs() {
        lblTotalRapports.setText(service.getTous().size() + " rapport(s)");
        int nbReco = rapportSelectionne != null ? rapportSelectionne.getRecommandations().size() : 0;
        lblTotalReco.setText(nbReco + " recommandation(s)");
        
        // Mettre à jour la cloche de notification
        int totalNotifs = 0;
        notifMenu.getItems().clear();
        
        javafx.scene.layout.VBox itemsBox = new javafx.scene.layout.VBox();
        itemsBox.setStyle("-fx-background-color: white;");

        for (RapportAudit r : service.getTous()) {
            // 1. Alertes critiques (Rouge)
            long countCritique = r.getRecommandations().stream()
                .filter(reco -> !reco.isResolue() && "Haute".equalsIgnoreCase(reco.getPriorite()))
                .count();
                
            if (countCritique > 0) {
                totalNotifs++;
                String pluriel = countCritique > 1 ? "s" : "";
                String titleText = countCritique + " reco" + pluriel + " critique" + pluriel + " non résolue" + pluriel;
                String subText = "Dans : " + r.getTitre();
                
                javafx.scene.layout.HBox itemBox = creerLigneNotification("🔴", "#c0392b", titleText, subText, r);
                itemsBox.getChildren().add(itemBox);
            }
            
            // 2. Alertes finalisées (Vert)
            if (r.getStatut() == com.audit.auditaifx.model.StatutRapport.FINALISE) {
                totalNotifs++;
                String titleText = "Rapport Finalisé";
                String subText = r.getTitre() + " a été finalisé";
                
                javafx.scene.layout.HBox itemBox = creerLigneNotification("🟢", "#27ae60", titleText, subText, r);
                itemsBox.getChildren().add(itemBox);
            }
        }

        if (totalNotifs == 0) {
            javafx.scene.control.Label emptyLbl = new javafx.scene.control.Label("Aucune notification");
            emptyLbl.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-padding: 15;");
            itemsBox.getChildren().add(emptyLbl);
        }

        if (lblNotifCount != null) {
            lblNotifCount.setText(String.valueOf(totalNotifs));
            lblNotifCount.setVisible(totalNotifs > 0);
        }
        
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(itemsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        
        double height = totalNotifs == 0 ? 40 : Math.min(totalNotifs * 45, 250);
        scrollPane.setPrefViewportHeight(height);
        scrollPane.setPrefViewportWidth(280);
        
        javafx.scene.control.CustomMenuItem customItem = new javafx.scene.control.CustomMenuItem(scrollPane);
        customItem.setHideOnClick(false);
        notifMenu.getItems().add(customItem);
    }

    private javafx.scene.layout.HBox creerLigneNotification(String emoji, String colorHex, String titleText, String subText, RapportAudit r) {
        javafx.scene.layout.HBox itemBox = new javafx.scene.layout.HBox(8);
        itemBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        itemBox.setStyle("-fx-padding: 6 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-border-width: 0 0 1 0; -fx-border-color: #f5f5f5;");
        
        itemBox.setOnMouseEntered(e -> itemBox.setStyle("-fx-padding: 6 10; -fx-background-color: #f9f9f9; -fx-cursor: hand; -fx-border-width: 0 0 1 0; -fx-border-color: #f5f5f5;"));
        itemBox.setOnMouseExited(e -> itemBox.setStyle("-fx-padding: 6 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-border-width: 0 0 1 0; -fx-border-color: #f5f5f5;"));
        
        javafx.scene.control.Label icon = new javafx.scene.control.Label(emoji);
        icon.setStyle("-fx-font-size: 8px;");
        
        javafx.scene.layout.VBox texts = new javafx.scene.layout.VBox(1);
        javafx.scene.control.Label title = new javafx.scene.control.Label(titleText);
        title.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 11px; -fx-font-weight: bold;");
        javafx.scene.control.Label subtitle = new javafx.scene.control.Label(subText);
        subtitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 9px;");
        texts.getChildren().addAll(title, subtitle);
        
        itemBox.getChildren().addAll(icon, texts);
        
        itemBox.setOnMouseClicked(e -> {
            notifMenu.hide();
            rapportSelectionne = r;
            showReportDetails(r);
        });
        return itemBox;
    }

    @FXML
    public void afficherMenuNotifications(javafx.scene.input.MouseEvent event) {
        if (paneNotifIcon != null) {
            notifMenu.show(paneNotifIcon, javafx.geometry.Side.BOTTOM, 0, 0);
        }
    }

    private void mettreAJourScoreResolution(RapportAudit rapport) {
        if (rapport == null || progressResolution == null) return;
        List<Recommandation> recos = rapport.getRecommandations();
        int total = recos.size();
        int resolues = (int) recos.stream().filter(Recommandation::isResolue).count();
        double pct = total == 0 ? 0.0 : (double) resolues / total;
        progressResolution.setProgress(pct);
        lblScoreResolution.setText((int)(pct * 100) + "%");
        lblRecoStats.setText(resolues + " / " + total + " résolues");
        // Couleur dynamique selon le taux
        if (pct >= 0.75) {
            progressResolution.setStyle("-fx-accent: #27ae60;");
            lblScoreResolution.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        } else if (pct >= 0.4) {
            progressResolution.setStyle("-fx-accent: #f39c12;");
            lblScoreResolution.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");
        } else {
            progressResolution.setStyle("-fx-accent: #e74c3c;");
            lblScoreResolution.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        }
    }

    private void afficherErreur(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
    public void creerNouveauRapport() {
        // TODO: Implémenter la création d'un nouveau rapport (ouvrir popup ou form)
        System.out.println("DEBUG: Créer un nouveau rapport (à implémenter)");
        afficherErreur("Création de rapport - Fonctionnalité en cours de développement");
    }

    @FXML
    public void switchClient() {
        try {
            var resource = getClass().getResource("/com/audit/auditaifx/client-view.fxml");
            if (resource == null) {
                afficherErreur("Fichier client-view.fxml introuvable dans les ressources.");
                return;
            }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resource);
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) dashboardView.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace(); // Print to console for debugging
            afficherErreur("Impossible de basculer vers la vue client : " + e.getMessage());
        }
    }
}