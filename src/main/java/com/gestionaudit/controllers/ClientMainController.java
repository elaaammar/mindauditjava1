package com.gestionaudit.controllers;

import com.gestionaudit.models.Reclamation;
import com.gestionaudit.services.ReclamationService;
import com.gestionaudit.MainFx;
import com.gestionaudit.utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ClientMainController {

    @FXML private VBox reclamationContainer;
    @FXML private ScrollPane scrollPane;

    private ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        refreshList();
    }

    @FXML
    private void refreshList() {
        reclamationContainer.getChildren().clear();
        try {
            List<Reclamation> reclamations = reclamationService.getAll();
            for (Reclamation r : reclamations) {
                addReclamationCard(r);
            }
        } catch (SQLException e) {
            DialogUtils.showError("Database Error", "Could not fetch reclamations: " + e.getMessage());
        }
    }

    private void addReclamationCard(Reclamation r) {
        VBox card = new VBox(10);
        card.getStyleClass().add("premium-card");
        
        HBox header = new HBox();
        Label title = new Label(r.getTitre());
        title.getStyleClass().add("card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label status = new Label("Statut: " + r.getStatut());
        String statusClass = "status-" + r.getStatut().toLowerCase().replace(" ", "_");
        status.getStyleClass().addAll("status-badge", statusClass);
        header.getChildren().addAll(title, spacer, status);
        
        Label desc = new Label(r.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #4b5563;");

        Label date = new Label("Date: " + r.getDateCreation().toString());
        date.getStyleClass().add("card-date");

        HBox actions = new HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        Button btnChat = new Button("Messagerie");
        btnChat.getStyleClass().addAll("btn", "btn-primary");
        btnChat.setOnAction(e -> openChat(r));

        Button btnEdit = new Button("Modifier");
        btnEdit.getStyleClass().addAll("btn", "btn-outline");
        btnEdit.setOnAction(e -> openForm(r));

        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().addAll("btn", "btn-danger");
        btnDelete.setOnAction(e -> handleDelete(r));

        actions.getChildren().addAll(btnChat, btnEdit, btnDelete);

        card.getChildren().addAll(header, desc, date, actions);
        
        reclamationContainer.getChildren().add(card);
    }

    private void handleDelete(Reclamation r) {
        if (DialogUtils.showConfirmation("Supprimer", "Voulez-vous vraiment supprimer cette réclamation ?")) {
            try {
                reclamationService.delete(r.getId());
                refreshList();
            } catch (SQLException e) {
                DialogUtils.showError("Erreur", "Impossible de supprimer: " + e.getMessage());
            }
        }
    }

    private void openChat(Reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chat_view.fxml"));
            Parent root = loader.load();
            ChatController controller = loader.getController();
            controller.initData(r, false); // false = client

            Stage stage = new Stage();
            stage.setTitle("Chat: " + r.getTitre());
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            MainFx.attachModernStylesheet(scene);
            stage.setScene(scene);
            stage.showAndWait();
            refreshList(); // Refresh in case status changed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddReclamation() {
        openForm(null);
    }

    private void openForm(Reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reclamation_form.fxml"));
            Parent root = loader.load();
            
            ReclamationFormController controller = loader.getController();
            controller.setReclamation(r);

            Stage stage = new Stage();
            stage.setTitle(r == null ? "Nouvelle Réclamation" : "Détails Réclamation");
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            MainFx.attachModernStylesheet(scene);
            stage.setScene(scene);
            stage.showAndWait();
            
            refreshList();
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showError("Error", "Could not open form: " + e.getMessage());
        }
    }
}
