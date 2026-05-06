package com.gestionaudit.controllers;

import com.gestionaudit.MainFx;
import com.gestionaudit.models.Reclamation;
import com.gestionaudit.services.ReclamationService;
import com.gestionaudit.utils.DialogUtils;
import com.gestionaudit.utils.ExportUtils;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminMainController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private VBox reclamationContainer;

    private ReclamationService reclamationService = new ReclamationService();
    private List<Reclamation> allReclamations;

    @FXML
    public void initialize() {
        statusFilter.getItems().addAll("Tous", Reclamation.STATUT_EN_ATTENTE, Reclamation.STATUT_EN_COURS, Reclamation.STATUT_RESOLUE, Reclamation.STATUT_CLOTUREE);
        statusFilter.setValue("Tous");

        loadData();
    }

    private void loadData() {
        try {
            allReclamations = reclamationService.getAll();
            filterAndDisplay();
        } catch (SQLException e) {
            DialogUtils.showError("Erreur SQL", "Impossible de charger les réclamations: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        filterAndDisplay();
    }

    private void filterAndDisplay() {
        if (allReclamations == null) return;

        String search = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();

        List<Reclamation> filtered = allReclamations.stream()
                .filter(r -> (r.getTitre().toLowerCase().contains(search) || r.getNom().toLowerCase().contains(search)))
                .filter(r -> "Tous".equals(status) || r.getStatut().equals(status))
                .collect(Collectors.toList());

        reclamationContainer.getChildren().clear();
        for (Reclamation r : filtered) {
            createAdminCard(r);
        }
    }

    private void createAdminCard(Reclamation r) {
        VBox card = new VBox(15);
        card.getStyleClass().add("premium-card");
        card.setMaxWidth(1000); 
        card.setPadding(new javafx.geometry.Insets(20));
        Label title = new Label(r.getTitre());
        title.getStyleClass().add("card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label status = new Label(r.getStatut());
        String statusClass = "status-" + r.getStatut().toLowerCase().replace(" ", "_");
        status.getStyleClass().addAll("status-badge", statusClass);
        HBox header = new HBox();
        header.getChildren().addAll(title, spacer, status);

        Label clientInfo = new Label("👤 " + r.getNom() + " • ✉ " + r.getEmail());
        clientInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #475569;");

        Label desc = new Label(r.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #4b5563; -fx-padding: 5px 0;");

        Label date = new Label("Créé le: " + r.getDateCreation().toLocalDate().toString());
        date.getStyleClass().add("card-date");

        HBox actions = new HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button btnChat = new Button("Ouvrir Chat");
        btnChat.getStyleClass().addAll("btn", "btn-primary");
        btnChat.setOnAction(e -> openChat(r));

        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().addAll("btn", "btn-danger");
        btnDelete.setOnAction(e -> handleDelete(r));

        Button btnPdf = new Button("PDF");
        btnPdf.getStyleClass().addAll("btn", "btn-outline");
        btnPdf.setOnAction(e -> handleExportPdf(r));

        Button btnExcel = new Button("Excel");
        btnExcel.getStyleClass().addAll("btn", "btn-outline");
        btnExcel.setOnAction(e -> handleExportExcel(r));

        actions.getChildren().addAll(btnChat, btnPdf, btnExcel, btnDelete);

        card.getChildren().addAll(header, clientInfo, desc, date, actions);
        reclamationContainer.getChildren().add(card);
    }

    private void handleDelete(Reclamation r) {
        if (DialogUtils.showConfirmation("Supprimer", "Confirmer la suppression de cette réclamation de " + r.getNom() + " ?")) {
            try {
                reclamationService.delete(r.getId());
                loadData();
            } catch (SQLException e) {
                DialogUtils.showError("Erreur SQL", "Impossible de supprimer: " + e.getMessage());
            }
        }
    }

    private void openChat(Reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chat_view.fxml"));
            Parent root = loader.load();

            ChatController controller = loader.getController();
            controller.initData(r, true); // Admin is true

            Stage stage = new Stage();
            stage.setTitle("Chat (Admin): " + r.getTitre());
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            MainFx.attachModernStylesheet(scene);
            stage.setScene(scene);
            stage.showAndWait();

            // Refresh cards to reflect potential status changes from the chat
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showError("Erreur", "Erreur lors de l'ouverture du chat: " + e.getMessage());
        }
    }

    private void handleExportPdf(Reclamation r) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer en PDF");
        fileChooser.setInitialFileName("Reclamation_" + r.getId() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(reclamationContainer.getScene().getWindow());
        
        if (file != null) {
            try {
                ExportUtils.exportToPDF(r, file.getAbsolutePath());
                Notifications.create()
                    .title("Export PDF Réussi")
                    .text("Le fichier PDF a été généré avec succès.")
                    .position(javafx.geometry.Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(3))
                    .showConfirm();
            } catch (Exception e) {
                DialogUtils.showError("Erreur", "Impossible de générer le PDF: " + e.getMessage());
            }
        }
    }

    private void handleExportExcel(Reclamation r) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer en Excel");
        fileChooser.setInitialFileName("Reclamation_" + r.getId() + ".xlsx");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(reclamationContainer.getScene().getWindow());
        
        if (file != null) {
            try {
                ExportUtils.exportToExcel(r, file.getAbsolutePath());
                Notifications.create()
                    .title("Export Excel Réussi")
                    .text("Le fichier Excel a été généré avec succès.")
                    .position(javafx.geometry.Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(3))
                    .showConfirm();
            } catch (Exception e) {
                DialogUtils.showError("Erreur", "Impossible de générer l'Excel: " + e.getMessage());
            }
        }
    }
}
