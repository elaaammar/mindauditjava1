package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import models.QuestionAudit;
import models.Rapport;
import models.ReponseQuestion;
import services.ReponseQuestionService;
import services.EmailService;
import services.ScoringService;

import java.io.IOException;
import java.util.List;

public class PerformAuditController {

    @FXML
    private Button btnFinish;

    @FXML
    private Button btnPrev;

    @FXML
    private Button btnSaveNext;

    @FXML
    private Label lblProgress;

    @FXML
    private Label lblQuestion;

    @FXML
    private Label lblReportTitle;

    @FXML
    private Label lblTimer;

    @FXML
    private TextArea taAnswer;

    private Rapport currentRapport;
    private List<QuestionAudit> questions;
    private int currentIndex = 0;
    private String currentRole;
    private Timeline timeline;
    private int secondsRemaining;
    
    private final ReponseQuestionService reponseService = new ReponseQuestionService();
    private final EmailService emailService = new EmailService();
    private final ScoringService scoringService = new ScoringService();

    public void initData(Rapport rapport, String role) {
        this.currentRapport = rapport;
        this.currentRole = role;
        this.questions = rapport.getQuestions();
        this.lblReportTitle.setText("Report: " + rapport.getTitle());
        
        if (questions == null || questions.isEmpty()) {
            lblQuestion.setText("No questions found for this report type.");
            taAnswer.setDisable(true);
            btnSaveNext.setDisable(true);
            btnFinish.setDisable(false);
        } else {
            loadQuestion(0);
        }
    }

    private void startQuestionTimer(int seconds) {
        if (timeline != null) timeline.stop();
        
        if (seconds <= 0) {
            lblTimer.setText("⏱ No Limit");
            lblTimer.setStyle("-fx-text-fill: #bdc3c7; -fx-font-weight: bold; -fx-font-size: 18px;");
            return;
        }

        secondsRemaining = seconds;
        updateTimerLabel();
        lblTimer.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 18px;");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            updateTimerLabel();

            if (secondsRemaining <= 0) {
                timeline.stop();
                handleQuestionTimeOut();
            } else if (secondsRemaining <= 5) {
                lblTimer.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 18px;");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerLabel() {
        int mins = secondsRemaining / 60;
        int secs = secondsRemaining % 60;
        lblTimer.setText(String.format("⏱ %02d:%02d", mins, secs));
    }

    private void handleQuestionTimeOut() {
        saveCurrentAnswer();
        if (currentIndex < questions.size() - 1) {
            // Auto-advance to next question
            loadQuestion(currentIndex + 1);
        } else {
            // End of audit
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Time Out");
            alert.setHeaderText("Last Question Time Elapsed");
            alert.setContentText("Time is up for the last question! The audit has been automatically saved.");
            alert.showAndWait();
            
            // Send notification
            sendNotification();
            
            closeAndGoBack();
        }
    }

    private void closeAndGoBack() {
        if (timeline != null) timeline.stop();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListRapports.fxml"));
            Parent root = loader.load();
            ListRapportsController controller = loader.getController();
            controller.setRole(this.currentRole != null ? this.currentRole : "ADMIN");

            javafx.scene.layout.StackPane contentArea = (javafx.scene.layout.StackPane) lblTimer.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadQuestion(int index) {
        this.currentIndex = index;
        QuestionAudit q = questions.get(index);
        
        lblQuestion.setText(q.getContent());
        lblProgress.setText("Question " + (index + 1) + " of " + questions.size());
        
        // Start Timer for this specific question
        startQuestionTimer(q.getTimeLimit());
        
        // Load existing answer if any
        ReponseQuestion rq = reponseService.getAnswerForQuestionAndReport(q.getId(), currentRapport.getId());
        if (rq != null) {
            taAnswer.setText(rq.getReponse());
        } else {
            taAnswer.clear();
        }
        
        // Update Buttons
        btnPrev.setDisable(index == 0);
        if (index == questions.size() - 1) {
            btnSaveNext.setText("Save Current");
        } else {
            btnSaveNext.setText("Save & Next ➡");
        }
    }

    @FXML
    void handleSaveNext(ActionEvent event) {
        saveCurrentAnswer();
        if (currentIndex < questions.size() - 1) {
            loadQuestion(currentIndex + 1);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Audit Progress", "You have reached the end of the questions. Click 'Finish' to complete.");
        }
    }

    @FXML
    void handlePrevious(ActionEvent event) {
        saveCurrentAnswer();
        if (currentIndex > 0) {
            loadQuestion(currentIndex - 1);
        }
    }

    private void saveCurrentAnswer() {
        if (questions == null || questions.isEmpty()) return;
        
        QuestionAudit q = questions.get(currentIndex);
        String answerText = taAnswer.getText();
        
        ReponseQuestion existing = reponseService.getAnswerForQuestionAndReport(q.getId(), currentRapport.getId());
        if (existing != null) {
            existing.setReponse(answerText);
            reponseService.update(existing);
        } else {
            ReponseQuestion newAns = new ReponseQuestion(q.getId(), currentRapport.getId(), answerText, "");
            reponseService.add(newAns);
        }
    }

    @FXML
    void handleFinish(ActionEvent event) {
        if (timeline != null) timeline.stop();
        saveCurrentAnswer();
        
        // Send notification
        sendNotification();
        
        showAlert(Alert.AlertType.INFORMATION, "Success", "Audit session completed and saved.");
        closeAndGoBack();
    }

    private void sendNotification() {
        if (questions == null || questions.isEmpty()) return;

        // Calculate Average Score
        int total = 0;
        int graded = 0;
        for (QuestionAudit q : questions) {
            if (q.getBonneReponse() == null || q.getBonneReponse().isEmpty()) continue;
            ReponseQuestion rq = reponseService.getAnswerForQuestionAndReport(q.getId(), currentRapport.getId());
            String userAnswer = (rq != null) ? rq.getReponse() : "";
            int score = scoringService.calculateScore(q.getBonneReponse(), userAnswer);
            if (score >= 0) {
                total += score;
                graded++;
            }
        }

        int avgScore = (graded > 0) ? (total / graded) : 0;
        
        // Send Email
        emailService.sendAuditCompletionEmail("benharizfourat88@gmail.com", currentRapport.getTitle(), avgScore);
    }

    @FXML
    void handleBackToDashboard(ActionEvent event) {
        if (timeline != null) timeline.stop();
        closeAndGoBack();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
