package com.gestionaudit.controllers;

import com.gestionaudit.models.Reclamation;
import com.gestionaudit.services.ReclamationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminMessagesController {

    @FXML private TextField searchField;
    @FXML private VBox inboxContainer;
    @FXML private StackPane chatArea;

    private ReclamationService reclamationService = new ReclamationService();
    private List<Reclamation> allReclamations;
    private VBox selectedItem = null;

    @FXML
    public void initialize() {
        loadData();
    }

    private void loadData() {
        try {
            allReclamations = reclamationService.getAll();
            filterAndDisplay();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        filterAndDisplay();
    }

    private void filterAndDisplay() {
        if (allReclamations == null) return;

        String search = searchField.getText().toLowerCase();
        List<Reclamation> filtered = allReclamations.stream()
                .filter(r -> r.getTitre().toLowerCase().contains(search) || r.getNom().toLowerCase().contains(search))
                .collect(Collectors.toList());

        inboxContainer.getChildren().clear();
        for (Reclamation r : filtered) {
            createInboxItem(r);
        }
    }

    private void createInboxItem(Reclamation r) {
        VBox item = new VBox(5);
        item.setStyle("-fx-padding: 10px; -fx-background-color: #f8fafc; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-cursor: hand;");
        
        Label nameLabel = new Label(r.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label titleLabel = new Label(r.getTitre());
        titleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        
        HBox header = new HBox(nameLabel);
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Label date = new Label(r.getDateCreation().toLocalDate().toString());
        date.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        header.getChildren().addAll(spacer, date);

        item.getChildren().addAll(header, titleLabel);

        item.setOnMouseClicked(e -> {
            if (selectedItem != null) {
                selectedItem.setStyle("-fx-padding: 10px; -fx-background-color: #f8fafc; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-cursor: hand;");
            }
            item.setStyle("-fx-padding: 10px; -fx-background-color: #e0e7ff; -fx-border-color: #818cf8 transparent #818cf8 transparent; -fx-cursor: hand;");
            selectedItem = item;
            
            openChatPanel(r);
        });

        inboxContainer.getChildren().add(item);
    }

    private void openChatPanel(Reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chat_view.fxml"));
            Parent chatRoot = loader.load();
            
            ChatController controller = loader.getController();
            controller.initData(r, true); // true = isAdmin

            chatArea.getChildren().clear();
            chatArea.getChildren().add(chatRoot);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
