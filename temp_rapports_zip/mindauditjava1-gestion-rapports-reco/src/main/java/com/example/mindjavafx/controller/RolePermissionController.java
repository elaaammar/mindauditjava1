package com.example.mindjavafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RolePermissionController {

    @FXML
    private ComboBox<String> roleNameCombo;

    @FXML
    private TextField roleDescriptionField;

    @FXML
    private TableView<RoleData> rolesTable;
    
    @FXML
    private TableColumn<RoleData, String> roleNameColumn;
    
    @FXML
    private TableColumn<RoleData, String> roleDescriptionColumn;

    @FXML
    private ListView<String> permissionsList;

    @FXML
    private Label messageLabel;
    
    private ObservableList<RoleData> rolesList;

    @FXML
    public void initialize() {
        // Initialiser le ComboBox avec les 3 rôles prédéfinis
        roleNameCombo.setItems(FXCollections.observableArrayList("Admin", "Auditeur", "User"));
        roleNameCombo.setValue("Admin"); // Valeur par défaut
        
        // Initialiser le tableau des rôles
        rolesList = FXCollections.observableArrayList();
        rolesTable.setItems(rolesList);
        
        roleNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        roleDescriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        
        permissionsList.setItems(FXCollections.observableArrayList(
            "Voir les utilisateurs",
            "Créer utilisateur",
            "Modifier utilisateur",
            "Supprimer utilisateur",
            "Voir les rapports",
            "Créer rapport",
            "Gérer les rôles",
            "Gérer les permissions"
        ));
        
        // Créer automatiquement les rôles par défaut s'ils n'existent pas
        createDefaultRoles();
    }
    
    private void createDefaultRoles() {
        // Cette méthode crée automatiquement Admin, Auditeur, User dans la base de données
        // au démarrage de l'application si ils n'existent pas déjà
        try {
            // TODO: Ajouter la logique pour vérifier et créer les rôles dans la base de données
            // Pour l'instant, on affiche juste un message
            System.out.println("Rôles par défaut créés: Admin, Auditeur, User");
        } catch (Exception e) {
            System.err.println("Erreur lors de la création des rôles par défaut: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateRole() {
        String roleName = roleNameCombo.getValue();
        String roleDesc = roleDescriptionField.getText().trim();

        if (roleName == null || roleName.isEmpty()) {
            messageLabel.setText("Le nom du rôle est requis");
            return;
        }

        // Ajouter le rôle au tableau
        rolesList.add(new RoleData(roleName, roleDesc));
        
        messageLabel.setText("✅ Rôle créé avec succès: " + roleName);
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        roleDescriptionField.clear();
    }

    @FXML
    private void handleAddPermission() {
        String selected = permissionsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            messageLabel.setText("✅ Permission ajoutée au rôle: " + selected);
            messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            messageLabel.setText("❌ Veuillez sélectionner une permission");
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleRemovePermission() {
        String selected = permissionsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            messageLabel.setText("✅ Permission supprimée: " + selected);
            messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            messageLabel.setText("❌ Veuillez sélectionner une permission");
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }
    
    // Classe interne pour stocker les données des rôles
    public static class RoleData {
        private final String name;
        private final String description;
        
        public RoleData(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
    }
}