package com.gestionaudit.controllers;

import com.gestionaudit.models.Reclamation;
import com.gestionaudit.models.ReponseReclamation;
import com.gestionaudit.services.MailService;
import com.gestionaudit.services.ReclamationService;
import com.gestionaudit.services.ReponseReclamationService;
import com.gestionaudit.utils.BadWordEnforcement;
import com.gestionaudit.utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.sql.SQLException;
import java.util.List;

public class ChatController {

    @FXML private Label chatTitle;
    @FXML private Label statusLabel;
    @FXML private VBox messageContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private TextArea messageInput;
    @FXML private ComboBox<String> statusUpdateCombo; // Only visible/used by Admin
    @FXML private HBox adminControls;

    private Reclamation reclamation;
    private boolean isAdmin;
    private ReponseReclamationService reponseService = new ReponseReclamationService();
    private ReclamationService reclamationService = new ReclamationService();
    private MailService mailService = new MailService();

    @FXML
    public void initialize() {
        statusUpdateCombo.getItems().addAll(Reclamation.STATUT_EN_ATTENTE, Reclamation.STATUT_EN_COURS, Reclamation.STATUT_RESOLUE, Reclamation.STATUT_CLOTUREE);
        
        // Auto-scroll to bottom whenever height of messageContainer changes
        messageContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }

    public void initData(Reclamation r, boolean isAdmin) {
        this.reclamation = r;
        this.isAdmin = isAdmin;
        
        chatTitle.setText("Chat: " + r.getTitre());
        updateStatusBadge();
        
        adminControls.setVisible(isAdmin);
        adminControls.setManaged(isAdmin);
        
        if (isAdmin) {
            statusUpdateCombo.setValue(r.getStatut());
        }

        loadMessages();
    }

    private void updateStatusBadge() {
        statusLabel.setText("Statut: " + reclamation.getStatut());
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().addAll("status-badge", "status-" + reclamation.getStatut().toLowerCase());
    }

    public void loadMessages() {
        messageContainer.getChildren().clear();
        try {
            List<ReponseReclamation> reponses = reponseService.getByReclamationId(reclamation.getId());
            for (ReponseReclamation res : reponses) {
                addMessageToChat(res);
            }
            // Trigger auto-scroll
            javafx.application.Platform.runLater(() -> {
                scrollPane.setVvalue(1.0);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMessageToChat(ReponseReclamation res) {
        boolean isMyMessage = (isAdmin && "Admin".equals(res.getAuteurType())) || (!isAdmin && "Client".equals(res.getAuteurType()));

        HBox row = new HBox();
        row.setAlignment(isMyMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        VBox bubble = new VBox(5);
        bubble.getStyleClass().add(isMyMessage ? "chat-bubble-client" : "chat-bubble-admin");
        
        Label authorLabel = new Label(res.getNom());
        authorLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (isMyMessage ? "#e0e7ff" : "#6b7280") + ";");

        Label content = new Label(res.getContenu());
        content.setWrapText(true);
        content.setMaxWidth(400);
        content.getStyleClass().add("chat-bubble-text");
        if(isMyMessage) content.setStyle("-fx-text-fill: white;");

        Label date = new Label(res.getDateCreation().toString());
        date.getStyleClass().add("chat-date");
        if(isMyMessage) date.setStyle("-fx-text-fill: #c7d2fe;");

        bubble.getChildren().addAll(authorLabel, content, date);

        // Edit/Delete for own messages
        if (isMyMessage) {
            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER_RIGHT);
            Button btnEdit = new Button("Edit");
            btnEdit.setStyle("-fx-background-color: transparent; -fx-text-fill: #a5b4fc; -fx-cursor: hand; -fx-font-size: 10px;");
            btnEdit.setOnAction(e -> handleEditMessage(res));

            Button btnDelete = new Button("Delete");
            btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #fca5a5; -fx-cursor: hand; -fx-font-size: 10px;");
            btnDelete.setOnAction(e -> handleDeleteMessage(res));

            actions.getChildren().addAll(btnEdit, btnDelete);
            bubble.getChildren().add(actions);
        }

        row.getChildren().add(bubble);
        row.setStyle("-fx-padding: 5px;");
        messageContainer.getChildren().add(row);
    }

    @FXML
    private void handleSendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;

        if (BadWordEnforcement.blockIfViolating(text, messageInput.getScene() != null ? messageInput.getScene().getWindow() : null)) {
            return;
        }

        ReponseReclamation res = new ReponseReclamation();
        res.setContenu(text);
        res.setReclamationId(reclamation.getId());
        res.setAuteurType(isAdmin ? "Admin" : "Client");
        res.setNom(isAdmin ? "Administrateur" : reclamation.getNom());

        try {
            reponseService.add(res);
            messageInput.clear();
            
            // If admin changes status simultaneously
            if (isAdmin && !statusUpdateCombo.getValue().equals(reclamation.getStatut())) {
                reclamation.setStatut(statusUpdateCombo.getValue());
                reclamationService.update(reclamation);
                updateStatusBadge();

                Notifications.create()
                    .title("Statut Mis à Jour")
                    .text("Le statut de la réclamation est passé à : " + reclamation.getStatut())
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.BOTTOM_RIGHT)
                    .showInformation();
                
                // Send email
                new Thread(() -> {
                    mailService.sendEmail(reclamation.getEmail(), 
                        "Mise à jour: " + reclamation.getTitre(),
                        "Bonjour,\n\nNouveau message de l'admin: " + text + "\nNouveau Statut: " + reclamation.getStatut());
                }).start();
            }

            loadMessages();
            
            Notifications.create()
                .title("Message Envoyé")
                .text("Votre réponse a été enregistrée avec succès.")
                .hideAfter(Duration.seconds(2))
                .position(Pos.BOTTOM_RIGHT)
                .showConfirm();
        } catch (SQLException e) {
            DialogUtils.showError("Error", "Could not send message: " + e.getMessage());
        }
    }

    private void handleEditMessage(ReponseReclamation res) {
        TextInputDialog dialog = new TextInputDialog(res.getContenu());
        dialog.setTitle("Modifier");
        dialog.setHeaderText("Modifier votre message");
        dialog.showAndWait().ifPresent(newText -> {
            if (!newText.trim().isEmpty()) {
                if (BadWordEnforcement.blockIfViolating(newText.trim(),
                        messageInput.getScene() != null ? messageInput.getScene().getWindow() : null)) {
                    return;
                }
                res.setContenu(newText.trim());
                try {
                    reponseService.update(res);
                    loadMessages();
                    
                    Notifications.create()
                        .title("Message Modifié")
                        .text("Le contenu de votre message a été mis à jour.")
                        .hideAfter(Duration.seconds(2))
                        .position(Pos.BOTTOM_RIGHT)
                        .showInformation();
                } catch (SQLException e) {
                    DialogUtils.showError("Erreur", "Impossible de modifier le message: " + e.getMessage());
                }
            }
        });
    }

    private void handleDeleteMessage(ReponseReclamation res) {
        if (DialogUtils.showConfirmation("Suppression", "Supprimer ce message ?")) {
            try {
                reponseService.delete(res.getId());
                loadMessages();
                
                Notifications.create()
                    .title("Message Supprimé")
                    .text("Le message a été retiré de la conversation.")
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.BOTTOM_RIGHT)
                    .showWarning();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
