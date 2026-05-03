package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import models.Rapport;
import models.QuestionAudit;
import services.RapportService;
import services.ReponseQuestionService;
import services.ScoringService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController {

    @FXML
    private BarChart<String, Number> barChartScores;

    @FXML
    private Label lblAverageScore;

    @FXML
    private Label lblComplianceStatus;

    @FXML
    private Label lblTotalAudits;

    @FXML
    private PieChart pieChartCategories;

    private final RapportService rapportService = new RapportService();
    private final ReponseQuestionService reponseService = new ReponseQuestionService();
    private final ScoringService scoringService = new ScoringService();

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        List<Rapport> rapports = rapportService.getAll();
        if (rapports == null || rapports.isEmpty()) {
            lblTotalAudits.setText("0");
            lblAverageScore.setText("0%");
            lblComplianceStatus.setText("No Data");
            return;
        }

        lblTotalAudits.setText(String.valueOf(rapports.size()));

        // 1. Logic for Pie Chart (Categories)
        Map<String, Integer> categoryCounts = new HashMap<>();
        double globalTotalScore = 0;
        int gradedRapportsCount = 0;

        XYChart.Series<String, Number> scoreSeries = new XYChart.Series<>();
        scoreSeries.setName("Audit Scores");

        for (Rapport r : rapports) {
            // Count categories
            String cat = r.getType() != null ? r.getType() : "Unknown";
            categoryCounts.put(cat, categoryCounts.getOrDefault(cat, 0) + 1);

            // Calculate score for each report for the Bar Chart
            int reportScore = calculateFinalScore(r);
            if (reportScore >= 0) {
                scoreSeries.getData().add(new XYChart.Data<>(r.getTitle().substring(0, Math.min(r.getTitle().length(), 10)) + "...", reportScore));
                globalTotalScore += reportScore;
                gradedRapportsCount++;
            }
        }

        // Set Pie Chart Data
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        categoryCounts.forEach((cat, count) -> pieData.add(new PieChart.Data(cat, count)));
        pieChartCategories.setData(pieData);

        // Set Bar Chart Data
        barChartScores.getData().add(scoreSeries);

        // Calculate Global KPIs
        if (gradedRapportsCount > 0) {
            int avgGlobal = (int) (globalTotalScore / gradedRapportsCount);
            lblAverageScore.setText(avgGlobal + "%");
            
            if (avgGlobal >= 75) {
                lblComplianceStatus.setText("EXCELLENT");
                lblComplianceStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else if (avgGlobal >= 50) {
                lblComplianceStatus.setText("COMPLIANT");
                lblComplianceStatus.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
            } else {
                lblComplianceStatus.setText("NON-COMPLIANT");
                lblComplianceStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        }
    }

    private int calculateFinalScore(Rapport rapport) {
        List<QuestionAudit> questions = rapport.getQuestions();
        if (questions == null || questions.isEmpty()) return -1;

        int total = 0;
        int count = 0;
        for (QuestionAudit q : questions) {
            models.ReponseQuestion rq = reponseService.getAnswerForQuestionAndReport(q.getId(), rapport.getId());
            String userAnswer = (rq != null) ? rq.getReponse() : "";
            int score = scoringService.calculateScore(q.getBonneReponse(), userAnswer);
            if (score >= 0) {
                total += score;
                count++;
            }
        }
        return (count > 0) ? (total / count) : 0;
    }
}
