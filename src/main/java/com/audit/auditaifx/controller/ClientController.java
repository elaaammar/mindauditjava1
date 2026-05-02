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
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileOutputStream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

public class ClientController {

    @FXML
    private ScrollPane lblNoSelection;
    @FXML
    private VBox allReportsView;
    @FXML
    private PieChart chartRecoStatus;
    @FXML
    private LineChart<String, Number> chartTendanceClient;
    @FXML
    private VBox vboxRecentReportsClient;
    @FXML
    private VBox vboxRecentRecosClient;
    @FXML
    private TableView<RapportAudit> rapportsTable;
    @FXML
    private TableColumn<RapportAudit, String> colTitre;
    @FXML
    private TableColumn<RapportAudit, String> colAuditeur;
    @FXML
    private TableColumn<RapportAudit, String> colDate;
    @FXML
    private TableColumn<RapportAudit, String> colStatut;
    @FXML
    private TableColumn<RapportAudit, Void> colActionsRapport;

    @FXML
    private TextField txtSearchReports;
    @FXML
    private ComboBox<String> comboFilterStatut;

    private RapportAudit rapportSelectionne = null;
    private List<RapportAudit> filteredList = new java.util.ArrayList<>();

    @FXML
    private Label lblTitre;
    @FXML
    private Label lblAuditeur;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblStatut;
    @FXML
    private TextArea txtDescription;

    @FXML
    private VBox recommendationsContainer;
    @FXML
    private VBox risquesContainer;
    @FXML
    private ScrollPane detailPane;
    @FXML
    private Label lblStatTotalRapports;
    @FXML
    private Label lblStatTotalReco;
    @FXML
    private Label lblStatRecoResolues;
    @FXML
    private Label lblScoreBadge;
    @FXML
    private Label lblTrophyBadge;

    // --- Scan View ---
    @FXML private ScrollPane scanScrollPane;
    @FXML private VBox scanView;
    @FXML private Label lblFileName;
    @FXML private VBox scanProgressBox;
    @FXML private ProgressBar scanProgressBar;
    @FXML private Label lblScanStatus;
    @FXML private VBox scanResultsContainer;
    @FXML private VBox vboxPointsCles;
    @FXML private Pane paneCelebration;

    private RapportService service = new RapportService();
    private AIService aiService = new AIService();

    private ObservableList<RapportAudit> allReports;

    @FXML
    public void initialize() {
        // Config table rapports
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuditeur.setCellValueFactory(new PropertyValueFactory<>("auditeur"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Boutons actions
        ajouterBoutonsRapportsTable();

        // Listen for selection in Table
        rapportsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                rapportSelectionne = newVal;
            }
        });

        // Config filtres
        comboFilterStatut.getItems().addAll("Tous", "BROUILLON", "EN_COURS", "FINALISE");
        comboFilterStatut.setValue("Tous");
        comboFilterStatut.setOnAction(e -> appliquerFiltre());
        txtSearchReports.textProperty().addListener((obs, oldText, newText) -> appliquerFiltre());

        rafraichirListe();
        showDashboard();
    }

    private void ajouterBoutonsRapportsTable() {
        colActionsRapport.setCellFactory(param -> new TableCell<>() {
            private final Button btnVoir = new Button("Voir");
            private final Button btnEdit = new Button("Edit");
            private final Button btnDel = new Button("Supp");
            private final HBox container = new HBox(5, btnVoir, btnEdit, btnDel);

            {
                btnVoir.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnEdit.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnDel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");

                btnVoir.setFocusTraversable(false);
                btnEdit.setFocusTraversable(false);
                btnDel.setFocusTraversable(false);

                btnVoir.setOnAction(e -> {
                    RapportAudit r = getTableView().getItems().get(getIndex());
                    showReportDetails(r);
                });
                btnEdit.setOnAction(e -> {
                    RapportAudit r = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireRapport(r);
                });
                btnDel.setOnAction(e -> {
                    RapportAudit r = getTableView().getItems().get(getIndex());
                    supprimerRapportManual(r);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void supprimerRapportManual(RapportAudit rapport) {
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

    private void appliquerFiltre() {
        String query = txtSearchReports.getText() != null ? txtSearchReports.getText().toLowerCase() : "";
        String statut = comboFilterStatut.getValue();

        List<RapportAudit> results = allReports.stream()
            .filter(r -> {
                boolean matchSearch = query.isEmpty()
                    || r.getTitre().toLowerCase().contains(query)
                    || r.getAuditeur().toLowerCase().contains(query);
                boolean matchStatut = statut == null || statut.equals("Tous")
                    || r.getStatut().name().equals(statut);
                return matchSearch && matchStatut;
            })
            .toList();

        rapportsTable.setItems(FXCollections.observableArrayList(results));
    }

    @FXML
    public void resetFilters() {
        txtSearchReports.clear();
        comboFilterStatut.setValue("Tous");
        appliquerFiltre();
    }

    @FXML
    public void showDashboard() {
        allReportsView.setVisible(false);
        detailPane.setVisible(false);
        scanScrollPane.setVisible(false);
        lblNoSelection.setVisible(true);
        
        rafraichirStats();

        ObservableList<RapportAudit> rapports = service.getTous();
        
        // Charts update
        chartRecoStatus.getData().clear();
        int totalReco = 0;
        int resolues = 0;
        for (RapportAudit r : rapports) {
            totalReco += r.getRecommandations().size();
            for (Recommandation reco : r.getRecommandations()) {
                if (reco.isResolue()) resolues++;
            }
        }
        if (totalReco > 0) {
            chartRecoStatus.getData().add(new PieChart.Data("Résolues", resolues));
            chartRecoStatus.getData().add(new PieChart.Data("En attente", totalReco - resolues));
        }

        if (chartTendanceClient != null) {
            chartTendanceClient.getData().clear();
            XYChart.Series<String, Number> seriesTendance = new XYChart.Series<>();
            java.util.Map<String, Long> countParDate = rapports.stream()
                .collect(java.util.stream.Collectors.groupingBy(r -> r.getDateCreation().toString(), java.util.stream.Collectors.counting()));
            countParDate.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .forEach(e -> seriesTendance.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));
            chartTendanceClient.getData().add(seriesTendance);
        }

        // Activités
        vboxRecentReportsClient.getChildren().clear();
        rapports.stream()
                .sorted((r1, r2) -> r2.getDateCreation().compareTo(r1.getDateCreation()))
                .limit(2)
                .forEach(r -> {
                    VBox card = new VBox(5);
                    card.setStyle("-fx-padding: 8; -fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #e9ecef; -fx-border-radius: 8;");
                    Label t = new Label(r.getTitre());
                    t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                    Label d = new Label(r.getDateCreation() + " | " + r.getStatut());
                    d.setStyle("-fx-font-size: 10px; -fx-text-fill: #6c757d;");
                    card.getChildren().addAll(t, d);
                    vboxRecentReportsClient.getChildren().add(card);
                });

        vboxRecentRecosClient.getChildren().clear();
        rapports.stream()
                .flatMap(r -> r.getRecommandations().stream())
                .limit(2)
                .forEach(reco -> {
                    HBox recoItem = new HBox(10);
                    recoItem.setAlignment(Pos.CENTER_LEFT);
                    Label dot = new Label("•");
                    dot.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                    Label desc = new Label(reco.getDescription());
                    desc.setStyle("-fx-font-size: 11px;");
                    desc.setWrapText(true);
                    recoItem.getChildren().addAll(dot, desc);
                    vboxRecentRecosClient.getChildren().add(recoItem);
                });
    }

    @FXML
    public void showAllReports() {
        lblNoSelection.setVisible(false);
        detailPane.setVisible(false);
        scanScrollPane.setVisible(false);
        allReportsView.setVisible(true);
        rapportsTable.getSelectionModel().clearSelection();
        rapportSelectionne = null;
        rafraichirListe();
    }

    private void rafraichirListe() {
        allReports = service.getTous();
        rapportsTable.setItems(allReports);
        rafraichirStats();
    }

    @FXML
    public void showScanView() {
        lblNoSelection.setVisible(false);
        detailPane.setVisible(false);
        allReportsView.setVisible(false);
        scanScrollPane.setVisible(true);
    }

    @FXML
    private void handleChooseFileScan() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un document d'audit");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documents PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("Documents Word", "*.docx"),
            new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(scanView.getScene().getWindow());
        if (selectedFile != null) {
            lblFileName.setText(selectedFile.getName());
            startMockScan(selectedFile.getName());
        }
    }

    private void startMockScan(String fileName) {
        scanProgressBox.setVisible(true);
        scanResultsContainer.setVisible(false);
        scanProgressBar.setProgress(0);
        lblScanStatus.setText("Initialisation du scan...");

        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Lecture du fichier...");
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(25);
                    updateProgress(i, 100);
                    if (i == 30) updateMessage("Extraction du texte...");
                    if (i == 60) updateMessage("Analyse sémantique par IA...");
                    if (i == 90) updateMessage("Génération des recommandations...");
                }
                return null;
            }
        };

        scanProgressBar.progressProperty().bind(task.progressProperty());
        lblScanStatus.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(e -> {
            lblScanStatus.textProperty().unbind();
            scanProgressBar.progressProperty().unbind();
            lblScanStatus.setText("✅ Analyse terminée avec succès");
            afficherResultatsModernes(fileName);
        });

        new Thread(task).start();
    }

    private void afficherResultatsModernes(String fileName) {
        scanResultsContainer.setVisible(true);
        vboxPointsCles.getChildren().clear();

        String[][] findings = {
            {"⚠️", "Accès Critiques", "Des accès administrateurs sans MFA ont été détectés.", "#ef4444"},
            {"💾", "Sauvegardes", "Rétention insuffisante (30 jours au lieu de 90).", "#f59e0b"},
            {"🔒", "Chiffrement", "Données de transit non chiffrées (TLS 1.2 manquant).", "#3b82f6"},
            {"📝", "Documentation", "Procédures d'exploitation non révisées depuis 2023.", "#10b981"}
        };

        for (String[] f : findings) {
            HBox card = new HBox(15);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: white; -fx-padding: 12 15; -fx-background-radius: 8; -fx-border-color: #f1f5f9; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 5, 0, 0, 2);");
            
            VBox colorStrip = new VBox();
            colorStrip.setPrefWidth(4);
            colorStrip.setStyle("-fx-background-color: " + f[3] + "; -fx-background-radius: 2;");
            colorStrip.setPrefHeight(40);

            Label icon = new Label(f[0]);
            icon.setStyle("-fx-font-size: 18;");
            
            VBox texts = new VBox(2);
            Label title = new Label(f[1]);
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-font-size: 14;");
            Label desc = new Label(f[2]);
            desc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");
            desc.setWrapText(true);
            
            texts.getChildren().addAll(title, desc);
            card.getChildren().addAll(colorStrip, icon, texts);
            
            card.setOpacity(0);
            vboxPointsCles.getChildren().add(card);
            
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500), card);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(javafx.util.Duration.millis(vboxPointsCles.getChildren().size() * 150));
            ft.play();
        }
    }

    @FXML
    private void handleGenererRapportDepuisScan() {
        StringBuilder sb = new StringBuilder("Rapport généré par Scan IA le " + java.time.LocalDate.now() + "\n\nPOINTS DÉTECTÉS :\n");
        for (javafx.scene.Node node : vboxPointsCles.getChildren()) {
            if (node instanceof HBox card) {
                VBox texts = card.getChildren().stream()
                    .filter(n -> n instanceof VBox)
                    .map(n -> (VBox) n)
                    .filter(v -> v.getChildren().size() >= 2)
                    .findFirst()
                    .orElse(null);

                if (texts != null && texts.getChildren().size() >= 2) {
                    Label title = (Label) texts.getChildren().get(0);
                    Label desc = (Label) texts.getChildren().get(1);
                    sb.append("- ").append(title.getText()).append(" : ").append(desc.getText()).append("\n");
                }
            }
        }

        RapportAudit nouveau = new RapportAudit();
        nouveau.setTitre("Nouveau Rapport (Scan)");
        nouveau.setAuditeur("Client (Auto)");
        nouveau.setEntiteAuditee("Entité Détectée");
        nouveau.setDescription(sb.toString());
        nouveau.setStatut(StatutRapport.BROUILLON);
        nouveau.setDateCreation(java.time.LocalDate.now());

        service.ajouter(nouveau);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rapport Créé");
        alert.setHeaderText("Succès de l'importation");
        alert.setContentText("Un nouveau rapport a été créé en mode BROUILLON avec les données du scan.");
        alert.showAndWait();

        showAllReports();
    }

    private void showReportDetails(RapportAudit report) {
        this.rapportSelectionne = report;
        lblNoSelection.setVisible(false);
        detailPane.setVisible(true);
        allReportsView.setVisible(false);

        lblTitre.setText(report.getTitre());
        lblAuditeur.setText("Auditeur: " + report.getAuditeur());
        lblDate.setText("Date: " + report.getDateCreation());
        lblStatut.setText(report.getStatut().name());
        txtDescription.setText(report.getDescription());

        if (report.getScoreAudit() != null && !report.getScoreAudit().isEmpty()) {
            afficherBadgeScore(report.getScoreAudit());
        } else {
            lblScoreBadge.setVisible(false);
            lblScoreBadge.setManaged(false);
        }

        boolean allResolue = !report.getRecommandations().isEmpty() && 
                             report.getRecommandations().stream().allMatch(Recommandation::isResolue);
        lblTrophyBadge.setVisible(allResolue);
        lblTrophyBadge.setManaged(allResolue);

        recommendationsContainer.getChildren().clear();
        for (Recommandation reco : report.getRecommandations()) {
            VBox card = new VBox(8);
            card.getStyleClass().add("reco-card");
            Label d = new Label(reco.getDescription());
            d.setWrapText(true);
            d.setStyle("-fx-font-weight: bold;");
            Label m = new Label("Priorité: " + reco.getPriorite() + " | " + (reco.isResolue() ? "✅ Résolue" : "⏳ En attente"));
            m.setStyle("-fx-font-size: 11px;");
            card.getChildren().addAll(d, m);
            recommendationsContainer.getChildren().add(card);
        }

        risquesContainer.getChildren().clear();
        for (Risque risque : report.getRisques()) {
            VBox card = new VBox(8);
            card.getStyleClass().add("reco-card");
            card.setStyle("-fx-border-color: #e67e22; -fx-background-color: #fff9f5;");
            Label d = new Label(risque.getDescription());
            d.setWrapText(true);
            d.setStyle("-fx-font-weight: bold; -fx-text-fill: #d35400;");
            Label impact = new Label("Impact: " + risque.getImpact());
            impact.setStyle("-fx-font-size: 11px;");
            Label niveau = new Label("Niveau: " + risque.getNiveau());
            niveau.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + (risque.getNiveau().contains("Critique") ? "#c0392b" : "#e67e22") + "; -fx-padding: 2 5; -fx-background-radius: 3;");
            card.getChildren().addAll(d, impact, niveau);
            risquesContainer.getChildren().add(card);
        }
    }

    private void rafraichirStats() {
        if (allReports == null) return;
        int totalRapports = allReports.size();
        int totalReco = 0;
        int recoResolues = 0;
        for (RapportAudit r : allReports) {
            totalReco += r.getRecommandations().size();
            for (Recommandation reco : r.getRecommandations()) {
                if (reco.isResolue()) recoResolues++;
            }
        }
        lblStatTotalRapports.setText(String.valueOf(totalRapports));
        lblStatTotalReco.setText(String.valueOf(totalReco));
        lblStatRecoResolues.setText(String.valueOf(recoResolues));
    }

    @FXML
    public void ajouterRapport() {
        ouvrirFormulaireRapport(null);
    }

    @FXML
    public void calculerScoreAudit() {
        if (rapportSelectionne == null) return;
        String json = aiService.calculerScoreAudit(rapportSelectionne);
        rapportSelectionne.setScoreAudit(json);
        service.modifier(rapportSelectionne);
        afficherBadgeScore(json);
        ouvrirModalScore(json);
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
            lblScoreBadge.setVisible(true);
            lblScoreBadge.setManaged(true);
            lblScoreBadge.setOnMouseClicked(e -> ouvrirModalScore(json));
            lblScoreBadge.setCursor(javafx.scene.Cursor.HAND);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void ouvrirModalScore(String json) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/audit/auditaifx/score-modal.fxml"));
            Parent root = loader.load();
            ScoreModalController controller = loader.getController();
            controller.setScoreData(json);
            Stage stage = new Stage();
            stage.setTitle("Détails du Score Audit");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void modifierRapport() {
        RapportAudit selected = (rapportSelectionne != null) ? rapportSelectionne : rapportsTable.getSelectionModel().getSelectedItem();
        if (selected != null) ouvrirFormulaireRapport(selected);
    }

    @FXML
    public void supprimerRapport() {
        RapportAudit selected = (rapportSelectionne != null) ? rapportSelectionne : rapportsTable.getSelectionModel().getSelectedItem();
        if (selected != null) supprimerRapportManual(selected);
    }

    private void ouvrirFormulaireRapport(RapportAudit rapport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/audit/auditaifx/rapport-form-view.fxml"));
            Parent root = loader.load();
            RapportFormController controller = loader.getController();
            controller.setService(service);
            controller.setRapport(rapport);
            controller.setOnSave(() -> {
                rafraichirListe();
                if (rapportSelectionne() != null) showReportDetails(rapportSelectionne());
                else showDashboard();
            });
            Stage stage = new Stage();
            stage.setTitle(rapport == null ? "Ajouter un rapport" : "Modifier le rapport");
            stage.setScene(new Scene(root, 500, 450));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private RapportAudit rapportSelectionne() { return rapportsTable.getSelectionModel().getSelectedItem(); }

    @FXML
    public void exporterPDF() {
        RapportAudit selected = (rapportSelectionne != null) ? rapportSelectionne : rapportsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter le Rapport en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Rapport_Audit_" + selected.getTitre().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf");
        File file = fileChooser.showSaveDialog(rapportsTable.getScene().getWindow());
        if (file == null) return;
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD, Color.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Paragraph pTitle = new Paragraph("RAPPORT D'AUDIT", titleFont);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Titre: " + selected.getTitre(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
            document.add(new Paragraph("Auditeur: " + selected.getAuditeur(), normalFont));
            document.add(new Paragraph("Statut: " + selected.getStatut(), normalFont));
            document.add(new Paragraph("Date: " + selected.getDateCreation(), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Description:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
            document.add(new Paragraph(selected.getDescription(), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("RECOMMANDATIONS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            String[] headers = { "Description", "Priorité", "Résolue" };
            for (String h : headers) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(Color.GRAY);
                table.addCell(cell);
            }
            for (Recommandation reco : selected.getRecommandations()) {
                table.addCell(new Phrase(reco.getDescription(), normalFont));
                table.addCell(new Phrase(reco.getPriorite(), normalFont));
                table.addCell(new Phrase(reco.isResolue() ? "OUI" : "NON", normalFont));
            }
            document.add(table);
            document.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF Réussi");
            alert.setContentText("Le rapport PDF a été généré avec succès !");
            alert.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void switchAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/audit/auditaifx/main-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rapportsTable.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }
}