package com.example.mindjavafx.apirest;

import com.example.mindjavafx.apirest.dto.ApiResponse;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.SmsService;
import com.example.mindjavafx.service.UserService;
import com.example.mindjavafx.util.PasswordUtil;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * API REST pour la réinitialisation de mot de passe
 * Gère tout le processus: génération code, envoi, vérification, reset
 */
public class PasswordResetAPI {
    
    private Gson gson;
    private UserService userService;
    private SmsService smsService;
    
    // Stockage temporaire des codes: email -> code
    private static Map<String, String> verificationCodes = new HashMap<>();
    // Stockage des timestamps pour expiration
    private static Map<String, Long> codeTimestamps = new HashMap<>();
    // Stockage des utilisateurs associés aux codes
    private static Map<String, User> codeUsers = new HashMap<>();
    
    // Durée de validité du code: 10 minutes
    private static final long CODE_VALIDITY_MS = 10 * 60 * 1000;

    public PasswordResetAPI() {
        this.gson = new Gson();
        this.userService = new UserService();
        this.smsService = new SmsService();
    }

    /**
     * Étape 1: Demander un code de vérification
     * POST /api/password-reset/request
     * Body: {"contact": "email@example.com" ou "0612345678"}
     */
    public String requestCode(String requestBody) {
        try {
            System.out.println("[API] Requête reçue: " + requestBody);
            
            Map<String, String> request = gson.fromJson(requestBody, Map.class);
            String contact = request.get("contact");
            
            System.out.println("[API] Contact: " + contact);
            
            if (contact == null || contact.isEmpty()) {
                return gson.toJson(new ApiResponse(false, "Email ou téléphone requis"));
            }
            
            // Chercher l'utilisateur
            User user = null;
            try {
                if (contact.contains("@")) {
                    user = userService.getUserByEmail(contact);
                } else {
                    user = userService.getUserByTelephone(contact);
                }
            } catch (Exception e) {
                System.err.println("[API] Erreur recherche utilisateur: " + e.getMessage());
                e.printStackTrace();
                return gson.toJson(new ApiResponse(false, "Erreur lors de la recherche: " + e.getMessage()));
            }
            
            if (user == null) {
                return gson.toJson(new ApiResponse(false, "Aucun compte trouvé avec ces informations"));
            }
            
            System.out.println("[API] Utilisateur trouvé: " + user.getEmail());
            
            // Générer un code à 6 chiffres
            String code = generateVerificationCode();
            
            // Stocker le code
            verificationCodes.put(contact, code);
            codeTimestamps.put(contact, System.currentTimeMillis());
            codeUsers.put(contact, user);
            
            System.out.println("[API] Code généré pour " + contact + ": " + code);
            
            // Envoyer le code (ne pas bloquer si ça échoue)
            try {
                if (contact.contains("@")) {
                    smsService.sendVerificationEmail(contact, code);
                } else {
                    smsService.sendVerificationCode(contact, code);
                }
            } catch (Exception e) {
                System.err.println("[API] Erreur envoi (ignorée): " + e.getMessage());
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("contact", maskContact(contact));
            data.put("code", code); // Pour le développement
            data.put("expiresIn", CODE_VALIDITY_MS / 1000);
            
            String response = gson.toJson(new ApiResponse(true, "Code envoyé avec succès", data));
            System.out.println("[API] Réponse: " + response);
            
            return response;
            
        } catch (Exception e) {
            System.err.println("[API] ERREUR GLOBALE: " + e.getMessage());
            e.printStackTrace();
            return gson.toJson(new ApiResponse(false, "Erreur serveur: " + e.getMessage()));
        }
    }

    /**
     * Étape 2: Vérifier le code
     * POST /api/password-reset/verify
     * Body: {"contact": "email@example.com", "code": "123456"}
     */
    public String verifyCode(String requestBody) {
        try {
            Map<String, String> request = gson.fromJson(requestBody, Map.class);
            String contact = request.get("contact");
            String code = request.get("code");
            
            if (contact == null || code == null) {
                return gson.toJson(new ApiResponse(false, "Contact et code requis"));
            }
            
            if (!verificationCodes.containsKey(contact)) {
                return gson.toJson(new ApiResponse(false, "Aucun code trouvé. Demandez d'abord un code."));
            }
            
            // Vérifier l'expiration
            long timestamp = codeTimestamps.get(contact);
            if (System.currentTimeMillis() - timestamp > CODE_VALIDITY_MS) {
                verificationCodes.remove(contact);
                codeTimestamps.remove(contact);
                codeUsers.remove(contact);
                return gson.toJson(new ApiResponse(false, "Le code a expiré. Demandez un nouveau code."));
            }
            
            String storedCode = verificationCodes.get(contact);
            
            if (!storedCode.equals(code)) {
                return gson.toJson(new ApiResponse(false, "Code incorrect"));
            }
            
            return gson.toJson(new ApiResponse(true, "Code vérifié avec succès"));
            
        } catch (Exception e) {
            return gson.toJson(new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }
    
    /**
     * Étape 3: Réinitialiser le mot de passe
     * POST /api/password-reset/reset
     * Body: {"contact": "email@example.com", "code": "123456", "newPassword": "newpass123"}
     */
    public String resetPassword(String requestBody) {
        try {
            Map<String, String> request = gson.fromJson(requestBody, Map.class);
            String contact = request.get("contact");
            String code = request.get("code");
            String newPassword = request.get("newPassword");
            
            if (contact == null || code == null || newPassword == null) {
                return gson.toJson(new ApiResponse(false, "Contact, code et nouveau mot de passe requis"));
            }
            
            if (newPassword.length() < 6) {
                return gson.toJson(new ApiResponse(false, "Le mot de passe doit contenir au moins 6 caractères"));
            }
            
            if (!verificationCodes.containsKey(contact)) {
                return gson.toJson(new ApiResponse(false, "Aucun code trouvé. Demandez d'abord un code."));
            }
            
            // Vérifier l'expiration
            long timestamp = codeTimestamps.get(contact);
            if (System.currentTimeMillis() - timestamp > CODE_VALIDITY_MS) {
                verificationCodes.remove(contact);
                codeTimestamps.remove(contact);
                codeUsers.remove(contact);
                return gson.toJson(new ApiResponse(false, "Le code a expiré"));
            }
            
            String storedCode = verificationCodes.get(contact);
            
            if (!storedCode.equals(code)) {
                return gson.toJson(new ApiResponse(false, "Code incorrect"));
            }
            
            // Récupérer l'utilisateur
            User user = codeUsers.get(contact);
            if (user == null) {
                return gson.toJson(new ApiResponse(false, "Utilisateur introuvable"));
            }
            
            // Mettre à jour le mot de passe
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            user.setPasswordHash(hashedPassword);
            
            boolean updated = userService.updateUser(user);
            
            if (updated) {
                // Nettoyer les codes utilisés
                verificationCodes.remove(contact);
                codeTimestamps.remove(contact);
                codeUsers.remove(contact);
                
                System.out.println("[API] Mot de passe réinitialisé pour: " + contact);
                
                return gson.toJson(new ApiResponse(true, "Mot de passe réinitialisé avec succès"));
            } else {
                return gson.toJson(new ApiResponse(false, "Erreur lors de la mise à jour du mot de passe"));
            }
            
        } catch (SQLException e) {
            return gson.toJson(new ApiResponse(false, "Erreur base de données: " + e.getMessage()));
        } catch (Exception e) {
            return gson.toJson(new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Récupère le code de vérification pour un contact (pour affichage web)
     * GET /api/password-reset/code?contact=xxx@xxx.com
     */
    public String getVerificationCode(String contact) {
        try {
            if (contact == null || contact.isEmpty()) {
                return gson.toJson(new ApiResponse(false, "Contact requis"));
            }
            
            if (!verificationCodes.containsKey(contact)) {
                return gson.toJson(new ApiResponse(false, "Aucun code trouvé. Demandez d'abord un code depuis l'application."));
            }
            
            // Vérifier l'expiration
            long timestamp = codeTimestamps.get(contact);
            long now = System.currentTimeMillis();
            
            if (now - timestamp > CODE_VALIDITY_MS) {
                verificationCodes.remove(contact);
                codeTimestamps.remove(contact);
                codeUsers.remove(contact);
                return gson.toJson(new ApiResponse(false, "Le code a expiré. Demandez un nouveau code."));
            }
            
            String code = verificationCodes.get(contact);
            
            Map<String, Object> data = new HashMap<>();
            data.put("contact", contact);
            data.put("code", code);
            data.put("expiresIn", (CODE_VALIDITY_MS - (now - timestamp)) / 1000);
            
            return gson.toJson(new ApiResponse(true, "Code de vérification", data));
            
        } catch (Exception e) {
            return gson.toJson(new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }
    
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    private String maskContact(String contact) {
        if (contact.contains("@")) {
            String[] parts = contact.split("@");
            return parts[0].charAt(0) + "***@" + parts[1];
        } else {
            return contact.substring(0, 2) + "****" + contact.substring(contact.length() - 4);
        }
    }
}
