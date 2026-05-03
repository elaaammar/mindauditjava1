package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.QuestionAudit;
import models.Rapport;
import services.RapportService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ListRapportsController {

    @FXML
    private TableColumn<Rapport, String> colDate;

    @FXML
    private TableColumn<Rapport, String> colTitle;

    @FXML
    private TableColumn<Rapport, String> colType;

    @FXML
    private TableView<QuestionAudit> tvQuestions;

    @FXML
    private TableColumn<QuestionAudit, String> colQuestionContent;

    @FXML
    private TableColumn<QuestionAudit, String> colBonneReponse;

    @FXML
    private TableColumn<QuestionAudit, Void> colScore;

    @FXML
    private TableColumn<QuestionAudit, Void> colActions;

    @FXML
    private TableColumn<QuestionAudit, Void> colAuditResponse;

    @FXML
    private Button btnStartAudit;

    @FXML
    private Button btnCalculerScore;

    @FXML
    private TableView<Rapport> tvRapports;

    @FXML
    private Label lblSummary;

    @FXML
    private Label lblScore;

    private final RapportService rapportService = new RapportService();
    private final services.ReponseQuestionService reponseService = new services.ReponseQuestionService();
    private final services.QuestionAuditService questionAuditService = new services.QuestionAuditService();
    private final services.ScoringService scoringService = new services.ScoringService();
    private ObservableList<Rapport> rapportList = FXCollections.observableArrayList();
    private String currentRole = "ADMIN";

    public void setRole(String role) {
        this.currentRole = role;
        applyRoleRestrictions();
    }

    private void applyRoleRestrictions() {
        if ("USER".equals(currentRole)) {
            colActions.setVisible(false);
            colBonneReponse.setVisible(false);
            colScore.setVisible(false);           // User cannot see scores
            btnCalculerScore.setVisible(false);
            btnCalculerScore.setManaged(false);
            btnStartAudit.setVisible(true);
            btnStartAudit.setManaged(true);
        } else {
            // ADMIN: full access + scoring tools
            colActions.setVisible(true);
            colBonneReponse.setVisible(true);
            colScore.setVisible(true);
            btnCalculerScore.setVisible(true);
            btnCalculerScore.setManaged(true);
            btnStartAudit.setVisible(true);
            btnStartAudit.setManaged(true);
        }
    }

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colQuestionContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        setupActionButtons();
        setupBonneReponseColumn();
        setupAuditResponseColumn();
        setupScoreColumn();

        loadRapports();

        tvRapports.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayQuestions(newSelection);
            }
        });

        // Apply role restrictions immediately (default = ADMIN, shows all buttons)
        applyRoleRestrictions();
    }

    private void loadRapports() {
        rapportList.clear();
        rapportList.addAll(rapportService.getAll());
        tvRapports.setItems(rapportList);
    }

    private void displayQuestions(Rapport rapport) {
        tvQuestions.getItems().clear();
        List<QuestionAudit> questions = rapport.getQuestions();
        if (questions == null || questions.isEmpty()) {
            lblSummary.setText("No questions found for the " + rapport.getType() + " category.");
        } else {
            tvQuestions.setItems(FXCollections.observableArrayList(questions));
            lblSummary.setText("Showing " + questions.size() + " " + rapport.getType() + " questions.");
        }
    }

    private void setupActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");
            private final HBox container = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                
                btnEdit.setOnAction(event -> {
                    QuestionAudit q = getTableView().getItems().get(getIndex());
                    handleEditQuestion(q);
                });
                
                btnDelete.setOnAction(event -> {
                    QuestionAudit q = getTableView().getItems().get(getIndex());
                    handleDeleteQuestion(q);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void setupScoreColumn() {
        colScore.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); setStyle(""); return; }
                QuestionAudit q = getTableView().getItems().get(getIndex());
                Rapport selectedRapport = tvRapports.getSelectionModel().getSelectedItem();
                if (selectedRapport == null || q.getBonneReponse() == null || q.getBonneReponse().isEmpty()) {
                    setText("—");
                    setStyle("-fx-text-fill: #95a5a6;");
                    return;
                }
                models.ReponseQuestion rq = reponseService.getAnswerForQuestionAndReport(q.getId(), selectedRapport.getId());
                String userAnswer = (rq != null) ? rq.getReponse() : "";
                int score = scoringService.calculateScore(q.getBonneReponse(), userAnswer);
                setText(scoringService.getScoreLabel(score));
                setStyle("-fx-text-fill: " + scoringService.getScoreColor(score) + "; -fx-font-weight: bold;");
            }
        });
    }

    @FXML
    void handleCalculerScore(ActionEvent event) {
        Rapport selected = tvRapports.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un rapport !").show();
            return;
        }
        tvQuestions.refresh();
        String scoreText = scoringService.calculateReportScore(
            selected.getQuestions(), selected.getId(), reponseService
        );
        lblScore.setText("Score global : " + scoreText);
        lblScore.setVisible(true);
        lblScore.setManaged(true);
    }

    private void setupBonneReponseColumn() {
        // Admin: shows correct answer as simple green text
        colBonneReponse.setCellValueFactory(new PropertyValueFactory<>("bonneReponse"));
        colBonneReponse.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setText("—");
                    setStyle("-fx-text-fill: #95a5a6;");
                } else {
                    setText("✅ " + item);
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void handleEditQuestion(QuestionAudit question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddQuestion.fxml"));
            Parent root = loader.load();
            
            AddQuestionController controller = loader.getController();
            controller.initData(question);
            
            javafx.scene.layout.StackPane contentArea = (javafx.scene.layout.StackPane) tvQuestions.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupAuditResponseColumn() {
        colAuditResponse.setCellFactory(param -> new TableCell<>() {
            // For USER: show a text field to type the answer
            private final TextField tfAnswer = new TextField();
            private final Button btnSave = new Button("💾 Sauvegarder");
            private final HBox userContainer = new HBox(5, tfAnswer, btnSave);

            {
                tfAnswer.setPromptText("Votre réponse...");
                tfAnswer.setPrefWidth(110);
                btnSave.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11;");
                btnSave.setOnAction(e -> {
                    QuestionAudit q = getTableView().getItems().get(getIndex());
                    Rapport selectedRapport = tvRapports.getSelectionModel().getSelectedItem();
                    if (selectedRapport != null && !tfAnswer.getText().trim().isEmpty()) {
                        models.ReponseQuestion existing = reponseService.getAnswerForQuestionAndReport(q.getId(), selectedRapport.getId());
                        if (existing != null) {
                            existing.setReponse(tfAnswer.getText().trim());
                            reponseService.update(existing);
                        } else {
                            models.ReponseQuestion rq = new models.ReponseQuestion(q.getId(), selectedRapport.getId(), tfAnswer.getText().trim(), "");
                            reponseService.add(rq);
                        }
                        btnSave.setText("✅ Enregistré");
                        btnSave.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11;");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                QuestionAudit q = getTableView().getItems().get(getIndex());
                Rapport selectedRapport = tvRapports.getSelectionModel().getSelectedItem();

                if ("ADMIN".equals(currentRole)) {
                    // Admin: see the user's submitted answer as text
                    setGraphic(null);
                    if (selectedRapport != null) {
                        models.ReponseQuestion rq = reponseService.getAnswerForQuestionAndReport(q.getId(), selectedRapport.getId());
                        if (rq != null && rq.getReponse() != null && !rq.getReponse().isEmpty()) {
                            setText("📝 " + rq.getReponse());
                            setStyle("-fx-text-fill: #2980b9; -fx-font-style: italic;");
                        } else {
                            setText("Aucune réponse");
                            setStyle("-fx-text-fill: #95a5a6;");
                        }
                    }
                } else {
                    // User (exam mode): show input field to type answer
                    setText(null);
                    if (selectedRapport != null) {
                        models.ReponseQuestion rq = reponseService.getAnswerForQuestionAndReport(q.getId(), selectedRapport.getId());
                        if (rq != null && rq.getReponse() != null && !rq.getReponse().isEmpty()) {
                            tfAnswer.setText(rq.getReponse());
                            btnSave.setText("✅ Enregistré");
                            btnSave.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11;");
                        } else {
                            tfAnswer.clear();
                            btnSave.setText("💾 Sauvegarder");
                            btnSave.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11;");
                        }
                    }
                    setGraphic(userContainer);
                }
            }
        });
    }

    @FXML
    void handleStartAudit(ActionEvent event) {
        Rapport selected = tvRapports.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a report first!");
            alert.show();
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/PerformAudit.fxml"));
            Parent root = loader.load();
            
            PerformAuditController controller = loader.getController();
            controller.initData(selected, currentRole);
            
            javafx.scene.layout.StackPane contentArea = (javafx.scene.layout.StackPane) tvRapports.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleGeneratePDF(ActionEvent event) {
        Rapport selected = tvRapports.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un rapport d'audit dans la liste !").show();
            return;
        }

        try {
            services.PDFService pdfService = new services.PDFService();
            pdfService.generateReport(selected);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("PDF Généré");
            alert.setContentText("Le rapport PDF pour '" + selected.getTitle() + "' a été enregistré à la racine du projet.");
            alert.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la génération : " + e.getMessage()).show();
        }
    }

    private void handleDeleteQuestion(QuestionAudit question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this question?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                new services.QuestionAuditService().delete(question);
                Rapport selected = tvRapports.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selected.getQuestions().remove(question);
                    displayQuestions(selected);
                }
            }
        });
    }
}