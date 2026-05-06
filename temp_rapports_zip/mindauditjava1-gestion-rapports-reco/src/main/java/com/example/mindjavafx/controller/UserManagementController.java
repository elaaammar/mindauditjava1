package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.Role;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuthenticationService;
import com.example.mindjavafx.service.UserService;
import com.example.mindjavafx.util.PasswordUtil;
import com.example.mindjavafx.util.Validation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class UserManagementController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nomColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> telephoneColumn;

    @FXML
    private TableColumn<User, Integer> ageColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<User, String> actifColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> searchTypeCombo;

    @FXML
    private ComboBox<String> sortCombo;

    @FXML
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField telephoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private CheckBox actifCheckBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    private UserService userService;
    private AuthenticationService authService;
    private ObservableList<User> userList;

    @FXML
    public void initialize() {
        System.out.println("[DEBUG] ========== INITIALISATION UserManagementController ==========");
        userService = new UserService();
        
        // Message visible pour l'utilisateur
        if (successLabel != null) {
            successLabel.setText("⏳ Chargement des utilisateurs...");
            successLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
        }
        
        setupTableColumns();
        setupComboBoxes();
        
        System.out.println("[DEBUG] Appel de loadUsers() au démarrage...");
        loadUsers();
        
        // Mettre à jour le message après le chargement
        if (successLabel != null && userList != null) {
            successLabel.setText("✅ " + userList.size() + " utilisateur(s) chargé(s)");
            successLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
        
        System.out.println("[DEBUG] ========== FIN INITIALISATION ==========");
    }
    


    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    private void setupTableColumns() {
        System.out.println("[DEBUG] Configuration des colonnes du tableau...");
        
        idColumn.setCellValueFactory(cellData -> {
            System.out.println("[DEBUG] idColumn - User ID: " + cellData.getValue().getId());
            return new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject();
        });
        
        nomColumn.setCellValueFactory(cellData -> {
            System.out.println("[DEBUG] nomColumn - User Nom: " + cellData.getValue().getNom());
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom());
        });
        
        emailColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail());
        });
        
        telephoneColumn.setCellValueFactory(cellData -> {
            String telephone = cellData.getValue().getTelephone();
            return new javafx.beans.property.SimpleStringProperty(telephone != null ? telephone : "N/A");
        });
        
        ageColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getAge()).asObject();
        });
        
        roleColumn.setCellValueFactory(cellData -> {
            String roleName = cellData.getValue().getRole() != null ? cellData.getValue().getRole().getNom() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(roleName);
        });
        
        passwordColumn.setCellValueFactory(cellData -> {
            // Afficher des étoiles pour masquer le mot de passe
            String password = cellData.getValue().getPasswordHash();
            return new javafx.beans.property.SimpleStringProperty(password != null ? "••••••" : "N/A");
        });
        
        actifColumn.setCellValueFactory(cellData -> {
            String statut = cellData.getValue().isActif() ? "Actif" : "Inactif";
            return new javafx.beans.property.SimpleStringProperty(statut);
        });
        
        System.out.println("[DEBUG] Colonnes configurées");
    }

    private void setupComboBoxes() {
        searchTypeCombo.setItems(FXCollections.observableArrayList("Nom", "Âge", "ID"));
        searchTypeCombo.setValue("Nom");

        sortCombo.setItems(FXCollections.observableArrayList("Nom (A-Z)", "Nom (Z-A)", "Âge (Croissant)", "Âge (Décroissant)"));
        sortCombo.setValue("Nom (A-Z)");

        roleCombo.setItems(FXCollections.observableArrayList("Admin", "User", "Auditeur"));
        roleCombo.setValue("User");
    }

    private void loadUsers() {
        try {
            System.out.println("[DEBUG] Chargement des utilisateurs...");
            List<User> users = userService.getAllUsers();
            System.out.println("[DEBUG] Nombre d'utilisateurs chargés: " + users.size());
            
            if (users.isEmpty()) {
                System.out.println("[ATTENTION] Aucun utilisateur trouvé dans la base de données !");
                successLabel.setText("⚠️ Aucun utilisateur dans la base de données");
                successLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
            }
            
            // Créer une nouvelle liste observable
            userList = FXCollections.observableArrayList(users);
            
            // Assigner au tableau
            userTable.setItems(userList);
            
            // Forcer le rafraîchissement
            userTable.refresh();
            
            System.out.println("[DEBUG] Tableau.getItems().size() = " + userTable.getItems().size());
            System.out.println("[DEBUG] Tableau rafraîchi avec " + userList.size() + " utilisateurs");
            
            // Afficher les utilisateurs dans la console pour debug
            for (User u : users) {
                System.out.println("[DEBUG] User: ID=" + u.getId() + ", Nom=" + u.getNom() + ", Email=" + u.getEmail());
            }
            
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors du chargement des utilisateurs: " + e.getMessage());
            System.err.println("[ERREUR] SQLException dans loadUsers: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            errorLabel.setText("Erreur inattendue: " + e.getMessage());
            System.err.println("[ERREUR] Exception dans loadUsers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String searchValue = searchField.getText().trim();
        String searchType = searchTypeCombo.getValue();

        System.out.println("DEBUG: Recherche - Type: " + searchType + ", Valeur: " + searchValue);

        if (searchValue.isEmpty()) {
            try {
                List<User> users = userService.getAllUsers();
                if (userList == null) {
                    userList = FXCollections.observableArrayList(users);
                    userTable.setItems(userList);
                } else {
                    userList.clear();
                    userList.addAll(users);
                }
            } catch (SQLException e) {
                errorLabel.setText("Erreur lors du chargement");
                e.printStackTrace();
            }
            return;
        }

        try {
            List<User> results = null;
            if ("Nom".equals(searchType)) {
                System.out.println("DEBUG: Recherche par nom...");
                results = userService.searchByName(searchValue);
                System.out.println("DEBUG: Résultats trouvés: " + results.size());
            } else if ("Âge".equals(searchType)) {
                try {
                    int age = Integer.parseInt(searchValue);
                    System.out.println("DEBUG: Recherche par âge: " + age);
                    results = userService.searchByAge(age);
                    System.out.println("DEBUG: Résultats trouvés: " + results.size());
                } catch (NumberFormatException e) {
                    errorLabel.setText("Âge invalide");
                    successLabel.setText("");
                    return;
                }
            } else if ("ID".equals(searchType)) {
                try {
                    int id = Integer.parseInt(searchValue);
                    System.out.println("DEBUG: Recherche par ID: " + id);
                    User user = userService.getUserById(id);
                    results = user != null ? List.of(user) : List.of();
                    System.out.println("DEBUG: Résultats trouvés: " + results.size());
                } catch (NumberFormatException e) {
                    errorLabel.setText("ID invalide");
                    successLabel.setText("");
                    return;
                }
            }

            if (results != null) {
                userList = FXCollections.observableArrayList(results);
                userTable.setItems(null);
                userTable.setItems(userList);
                userTable.refresh();
                errorLabel.setText("");
                successLabel.setText("✅ " + results.size() + " résultat(s) trouvé(s)");
                successLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            }
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors de la recherche: " + e.getMessage());
            successLabel.setText("");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleResetSearch() {
        searchField.clear();
        try {
            List<User> users = userService.getAllUsers();
            userList = FXCollections.observableArrayList(users);
            userTable.setItems(null);
            userTable.setItems(userList);
            userTable.refresh();
            errorLabel.setText("");
            successLabel.setText("✅ Table rechargée - Tous les utilisateurs affichés");
            successLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors du chargement");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSort() {
        String sortType = sortCombo.getValue();
        userList.sort((u1, u2) -> {
            switch (sortType) {
                case "Nom (A-Z)":
                    return u1.getNom().compareTo(u2.getNom());
                case "Nom (Z-A)":
                    return u2.getNom().compareTo(u1.getNom());
                case "Âge (Croissant)":
                    return Integer.compare(u1.getAge(), u2.getAge());
                case "Âge (Décroissant)":
                    return Integer.compare(u2.getAge(), u1.getAge());
                default:
                    return 0;
            }
        });
        userTable.setItems(userList);
    }

    @FXML
    private void handleAddUser() {
        clearFields();
        errorLabel.setText("");
        successLabel.setText("✏️ Remplissez le formulaire ci-dessous pour ajouter un utilisateur");
        successLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
    }

    @FXML
    private void handleSaveUser() {
        // Vérifier si on est en mode modification (si un utilisateur est sélectionné dans le tableau)
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            System.out.println("[DEBUG] Redirection vers handleUpdateUser car un utilisateur est sélectionné");
            handleUpdateUser();
            return;
        }

        // Sinon, continuer avec la logique d'ajout habituelle
        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String ageStr = ageField.getText().trim();
        String telephone = telephoneField.getText().trim();

        if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || ageStr.isEmpty()) {
            errorLabel.setText("Tous les champs requis doivent être remplis");
            return;
        }

        if (!Validation.isValidName(nom)) {
            errorLabel.setText("Nom invalide");
            return;
        }

        if (!Validation.isValidEmail(email)) {
            errorLabel.setText("Email invalide");
            return;
        }

        if (!Validation.isValidPassword(password)) {
            errorLabel.setText("Mot de passe invalide (min 6 caractères)");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            errorLabel.setText("Âge invalide");
            return;
        }

        if (!Validation.isValidAge(age)) {
            errorLabel.setText("Âge doit être entre 18 et 100");
            return;
        }

        if (!Validation.isValidTelephone(telephone)) {
            errorLabel.setText("Téléphone invalide");
            return;
        }

        try {
            // Vérifier si l'email existe déjà AVANT d'ajouter
            if (userService.getUserByEmail(email) != null) {
                errorLabel.setText("❌ Cet email est déjà utilisé par un autre utilisateur");
                successLabel.setText("");
                return;
            }

            // Déterminer le role_id basé sur le nom du rôle
            String roleName = roleCombo.getValue();
            int roleId;
            switch (roleName) {
                case "Admin": roleId = 1; break;
                case "User": roleId = 2; break;
                case "Auditeur": roleId = 3; break;
                default: roleId = 2;
            }
            
            Role role = new Role(roleId, roleName);
            User user = new User(nom, email, PasswordUtil.hashPassword(password), age, role);
            user.setTelephone(telephone);
            user.setActif(actifCheckBox.isSelected());

            int userId = userService.addUser(user);
            
            if (userId > 0) {
                successLabel.setText("✅ Utilisateur ajouté avec succès!");
                successLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                errorLabel.setText("");
                loadUsers();
                clearFields();
            } else {
                errorLabel.setText("❌ Erreur lors de l'ajout");
            }
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("duplicate")) {
                errorLabel.setText("❌ Cet email est déjà utilisé");
            } else {
                errorLabel.setText("Erreur base de données: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            errorLabel.setText("Veuillez sélectionner un utilisateur");
            successLabel.setText("");
            return;
        }

        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String ageStr = ageField.getText().trim();
        String telephone = telephoneField.getText().trim();

        if (nom.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
            errorLabel.setText("Tous les champs requis doivent être remplis");
            successLabel.setText("");
            return;
        }

        if (!Validation.isValidName(nom)) {
            errorLabel.setText("Nom invalide");
            successLabel.setText("");
            return;
        }

        if (!Validation.isValidEmail(email)) {
            errorLabel.setText("Email invalide");
            successLabel.setText("");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            errorLabel.setText("Âge invalide");
            successLabel.setText("");
            return;
        }

        if (!Validation.isValidAge(age)) {
            errorLabel.setText("Âge doit être entre 18 et 100");
            successLabel.setText("");
            return;
        }

        if (!Validation.isValidTelephone(telephone)) {
            errorLabel.setText("Téléphone invalide");
            successLabel.setText("");
            return;
        }

        try {
            // DEBUG: Afficher l'ID de l'utilisateur sélectionné
            System.out.println("[DEBUG UPDATE] ID utilisateur sélectionné: " + selectedUser.getId());
            System.out.println("[DEBUG UPDATE] Email actuel: " + selectedUser.getEmail());
            System.out.println("[DEBUG UPDATE] Nouvel email: " + email);
            
            // Vérifier si l'email est déjà utilisé par un AUTRE utilisateur
            User existingUser = userService.getUserByEmail(email);
            
            if (existingUser != null) {
                System.out.println("[DEBUG UPDATE] Email trouvé dans la base - ID: " + existingUser.getId());
                System.out.println("[DEBUG UPDATE] Comparaison: " + existingUser.getId() + " != " + selectedUser.getId());
            }
            
            if (existingUser != null && existingUser.getId() != selectedUser.getId()) {
                errorLabel.setText("❌ Cet email est déjà utilisé par un autre utilisateur");
                successLabel.setText("");
                return;
            }

            // Déterminer le role_id basé sur le nom du rôle
            String roleName = roleCombo.getValue();
            int roleId;
            switch (roleName) {
                case "Admin":
                    roleId = 1;
                    break;
                case "User":
                    roleId = 2;
                    break;
                case "Auditeur":
                    roleId = 3;
                    break;
                default:
                    roleId = 2; // Par défaut User
            }

            selectedUser.setNom(nom);
            selectedUser.setEmail(email);
            selectedUser.setAge(age);
            selectedUser.setTelephone(telephone);
            selectedUser.setRole(new Role(roleId, roleName));
            selectedUser.setActif(actifCheckBox.isSelected());
            
            // Mettre à jour le mot de passe seulement s'il est fourni
            if (!password.isEmpty()) {
                if (!Validation.isValidPassword(password)) {
                    errorLabel.setText("Mot de passe invalide (min 6 caractères)");
                    successLabel.setText("");
                    return;
                }
                selectedUser.setPasswordHash(PasswordUtil.hashPassword(password));
            }

            if (userService.updateUser(selectedUser)) {
                successLabel.setText("✅ Utilisateur mis à jour avec succès");
                successLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                errorLabel.setText("");
                loadUsers();
                clearFields();
            } else {
                errorLabel.setText("❌ Erreur lors de la mise à jour");
                successLabel.setText("");
            }
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("duplicate")) {
                errorLabel.setText("❌ Cet email est déjà utilisé par un autre utilisateur");
            } else {
                errorLabel.setText("Erreur base de données: " + e.getMessage());
            }
            successLabel.setText("");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            errorLabel.setText("Veuillez sélectionner un utilisateur");
            return;
        }

        try {
            if (userService.deleteUser(selectedUser.getId())) {
                successLabel.setText("Utilisateur supprimé avec succès");
                loadUsers();
            } else {
                errorLabel.setText("Erreur lors de la suppression");
            }
        } catch (SQLException e) {
            errorLabel.setText("Erreur base de données");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSelectUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            System.out.println("[DEBUG SELECT] Utilisateur sélectionné - ID: " + selectedUser.getId() + ", Nom: " + selectedUser.getNom());
            nomField.setText(selectedUser.getNom());
            emailField.setText(selectedUser.getEmail());
            ageField.setText(String.valueOf(selectedUser.getAge()));
            telephoneField.setText(selectedUser.getTelephone() != null ? selectedUser.getTelephone() : "");
            roleCombo.setValue(selectedUser.getRole().getNom());
            actifCheckBox.setSelected(selectedUser.isActif());
            passwordField.clear();
            
            errorLabel.setText("");
            successLabel.setText("✏️ Modification de: " + selectedUser.getNom() + " (ID: " + selectedUser.getId() + ")");
            successLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
        }
    }

    private void clearFields() {
        nomField.clear();
        emailField.clear();
        ageField.clear();
        telephoneField.clear();
        passwordField.clear();
        roleCombo.setValue("User");
        actifCheckBox.setSelected(true);
    }
}