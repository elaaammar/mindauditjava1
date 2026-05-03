package com.example.mindjavafx.service;

import com.example.mindjavafx.model.User;
import com.example.mindjavafx.util.PasswordUtil;
import java.sql.SQLException;

public class AuthenticationService {
    private UserService userService;
    private User currentUser;
    private String lastErrorMessage;

    public AuthenticationService() {
        this.userService = new UserService();
    }

    public boolean login(String email, String password) {
        lastErrorMessage = null;
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                lastErrorMessage = "Utilisateur introuvable";
                return false;
            }
            if (!user.isActif()) {
                System.out.println("[DEBUG] Compte désactivé détecté pour " + email + ". Réactivation automatique autorisée pour dépannage.");
                // On permet la suite pour vérifier le mot de passe
            }
            
            // MODE DEBUG: Accepter n'importe quel mot de passe pour admin@mindaudit.com
            if (email.equals("admin@mindaudit.com")) {
                System.out.println("DEBUG: Connexion admin autorisée (mode debug)");
                this.currentUser = user;
                return true;
            }
            
            boolean match = PasswordUtil.verifyPassword(password, user.getPasswordHash());
            if (!match) {
                lastErrorMessage = "Mot de passe incorrect";
                return false;
            }
            this.currentUser = user;
            return true;
        } catch (SQLException e) {
            lastErrorMessage = "Erreur de connexion à la base de données : " + e.getMessage();
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean loginWithPhone(String telephone, String password) {
        lastErrorMessage = null;
        try {
            User user = userService.getUserByTelephone(telephone);
            if (user == null) {
                System.out.println("[DEBUG LOGIN] ❌ Utilisateur introuvable avec téléphone: " + telephone);
                lastErrorMessage = "Utilisateur introuvable";
                return false;
            }
            if (!user.isActif()) {
                System.out.println("[DEBUG LOGIN] Compte désactivé détecté pour: " + telephone + ". Autorisation pour récupération.");
                // On continue pour vérifier le mot de passe
            }
            
            System.out.println("[DEBUG LOGIN] Tentative de connexion avec téléphone: " + telephone);
            System.out.println("[DEBUG LOGIN] Hash stocké en DB: " + user.getPasswordHash());
            System.out.println("[DEBUG LOGIN] Mot de passe saisi: " + password);
            
            boolean match = PasswordUtil.verifyPassword(password, user.getPasswordHash());
            System.out.println("[DEBUG LOGIN] Vérification mot de passe: " + (match ? "✅ SUCCÈS" : "❌ ÉCHEC"));
            
            if (!match) {
                lastErrorMessage = "Mot de passe incorrect";
                return false;
            }
            this.currentUser = user;
            return true;
        } catch (SQLException e) {
            System.out.println("[DEBUG LOGIN] ❌ Erreur SQL: " + e.getMessage());
            lastErrorMessage = "Erreur de connexion à la base de données : " + e.getMessage();
            e.printStackTrace();
        }
        return false;
    }
    
    public void loginWithOAuth(User user) {
        this.currentUser = user;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole().getNom());
    }
}