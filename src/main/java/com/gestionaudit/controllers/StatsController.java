package com.gestionaudit.controllers;

import com.gestionaudit.models.Reclamation;
import com.gestionaudit.services.ReclamationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatsController {

    @FXML private PieChart statusPieChart;
    @FXML private BarChart<String, Number> priorityBarChart;
    @FXML private CategoryAxis priorityXAxis;
    @FXML private NumberAxis priorityYAxis;
    @FXML private LineChart<String, Number> timelineLineChart;
    @FXML private NumberAxis timelineYAxis;
    @FXML private CategoryAxis timelineXAxis;
    @FXML private AreaChart<String, Number> cumulativeAreaChart;
    @FXML private BarChart<String, Number> categoryBarChart;
    
    private ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        try {
            List<Reclamation> list = reclamationService.getAll();
            updateCharts(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCharts(List<Reclamation> list) {
        Map<String, Integer> statusCount = new HashMap<>();
        Map<String, Integer> priorityCount = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Integer> dateCount = new TreeMap<>(); // TreeMap to sort by date

        for (Reclamation r : list) {
            // Status
            statusCount.put(r.getStatut(), statusCount.getOrDefault(r.getStatut(), 0) + 1);
            
            // Priority
            String p = r.getPriorite() != null ? r.getPriorite() : "Non défini";
            priorityCount.put(p, priorityCount.getOrDefault(p, 0) + 1);
            
            // Category
            String cat = r.getCategorie() != null && !r.getCategorie().trim().isEmpty() ? r.getCategorie() : "Autre";
            categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);
            
            // Timeline
            String dateKey = r.getDateCreation().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dateCount.put(dateKey, dateCount.getOrDefault(dateKey, 0) + 1);
        }

        // 1. Status Pie Chart
        ObservableList<PieChart.Data> statusData = FXCollections.observableArrayList();
        statusCount.forEach((k, v) -> statusData.add(new PieChart.Data(k, v)));
        statusPieChart.setData(statusData);

        // 2. Priority Bar Chart
        XYChart.Series<String, Number> prioritySeries = new XYChart.Series<>();
        prioritySeries.setName("Nombre de Réclamations");
        priorityCount.forEach((k, v) -> prioritySeries.getData().add(new XYChart.Data<>(k, v)));
        priorityBarChart.getData().clear();
        priorityBarChart.getData().add(prioritySeries);

        // 3. Category Bar Chart
        XYChart.Series<String, Number> catSeries = new XYChart.Series<>();
        catSeries.setName("Catégories");
        categoryCount.forEach((k, v) -> catSeries.getData().add(new XYChart.Data<>(k, v)));
        categoryBarChart.getData().clear();
        categoryBarChart.getData().add(catSeries);

        // 4 & 5. Timeline Line Chart & Cumulative Area Chart
        XYChart.Series<String, Number> timelineSeries = new XYChart.Series<>();
        timelineSeries.setName("Réclamations par jour");
        
        XYChart.Series<String, Number> cumulativeSeries = new XYChart.Series<>();
        cumulativeSeries.setName("Évolution Globale");
        
        int totalSoFar = 0;
        for (Map.Entry<String, Integer> entry : dateCount.entrySet()) {
            timelineSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            totalSoFar += entry.getValue();
            cumulativeSeries.getData().add(new XYChart.Data<>(entry.getKey(), totalSoFar));
        }
        
        timelineLineChart.getData().clear();
        timelineLineChart.getData().add(timelineSeries);
        
        cumulativeAreaChart.getData().clear();
        cumulativeAreaChart.getData().add(cumulativeSeries);
    }
}
