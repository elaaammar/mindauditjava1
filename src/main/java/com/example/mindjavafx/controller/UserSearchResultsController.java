package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

public class UserSearchResultsController {

    @FXML private VBox resultsContainer;
    @FXML private Label resultCountLabel;
    @FXML private Label searchQueryLabel;
    @FXML private Label noResultsLabel;

    @FXML
    public void initialize() {
        // Initialization if needed
    }

    public void setResults(String query, List<User> users) {
        searchQueryLabel.setText("Personnes correspondant à \"" + query + "\"");
        resultCountLabel.setText(users.size() + " résultat(s)");
        
        resultsContainer.getChildren().clear();
        
        if (users.isEmpty()) {
            noResultsLabel.setVisible(true);
            resultsContainer.getChildren().add(noResultsLabel);
            return;
        }

        noResultsLabel.setVisible(false);

        for (User user : users) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user-item-card.fxml"));
                Parent card = loader.load();
                
                // Get components from the card
                Label initialsLabel = (Label) card.lookup("#initialsLabel");
                Label userNameLabel = (Label) card.lookup("#userNameLabel");
                Label roleLabel = (Label) card.lookup("#roleLabel");
                Label statusLabel = (Label) card.lookup("#statusLabel");
                Circle statusCircle = (Circle) card.lookup("#statusCircle");

                // Fill data
                userNameLabel.setText(user.getNom());
                roleLabel.setText(user.getRole().getNom());
                
                // Set initials
                String[] names = user.getNom().split(" ");
                String initials = "";
                if (names.length >= 2) {
                    initials = names[0].substring(0, 1).toUpperCase() + names[1].substring(0, 1).toUpperCase();
                } else if (names.length == 1) {
                    initials = names[0].substring(0, Math.min(2, names[0].length())).toUpperCase();
                }
                initialsLabel.setText(initials);

                // Status logic
                if (user.isActif()) {
                    statusLabel.setText("Actif");
                    statusCircle.setFill(Color.web("#27ae60"));
                } else {
                    statusLabel.setText("Inactif");
                    statusCircle.setFill(Color.web("#e74c3c"));
                }

                resultsContainer.getChildren().add(card);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
