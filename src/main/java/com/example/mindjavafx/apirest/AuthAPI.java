package com.example.mindjavafx.apirest;

import com.example.mindjavafx.apirest.dto.ApiResponse;
import com.example.mindjavafx.apirest.dto.LoginRequest;
import com.example.mindjavafx.apirest.dto.UserDTO;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuthenticationService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * API REST pour l'authentification
 * Endpoints: POST /api/auth/login, POST /api/auth/logout, GET /api/auth/current
 */
public class AuthAPI {
    
    private AuthenticationService authService;
    private Gson gson;
    private Map<String, User> sessions; // Simple session storage

    public AuthAPI() {
        this.authService = new AuthenticationService();
        this.gson = new Gson();
        this.sessions = new HashMap<>();
    }

    public String login(String requestBody) {
        try {
            LoginRequest loginReq = gson.fromJson(requestBody, LoginRequest.class);
            
            if (loginReq.getEmail() == null || loginReq.getPassword() == null) {
                return gson.toJson(new ApiResponse(false, "Email et mot de passe requis"));
            }
            
            boolean success = authService.login(loginReq.getEmail(), loginReq.getPassword());
            
            if (success) {
                User user = authService.getCurrentUser();
                
                // Créer une session simple (token = email pour simplifier)
                String token = "token_" + System.currentTimeMillis();
                sessions.put(token, user);
                
                UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getNom(),
                    user.getEmail(),
                    user.getAge(),
                    user.getRole() != null ? user.getRole().getNom() : "Unknown",
                    user.isActif(),
                    user.getTelephone()
                );
                
                Map<String, Object> data = new HashMap<>();
                data.put("user", userDTO);
                data.put("token", token);
                
                return gson.toJson(new ApiResponse(true, "Connexion réussie", data));
            } else {
                String errorMsg = authService.getLastErrorMessage();
                return gson.toJson(new ApiResponse(false, errorMsg != null ? errorMsg : "Identifiants incorrects"));
            }
            
        } catch (Exception e) {
            return gson.toJson(new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }

    public String logout(String token) {
        try {
            if (token != null && sessions.containsKey(token)) {
                sessions.remove(token);
                return gson.toJson(new ApiResponse(true, "Déconnexion réussie"));
            } else {
                return gson.toJson(new ApiResponse(false, "Token invalide"));
            }
            
        } catch (Exception e) {
            return gson.toJson(new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }

    public String getCurrentUser(String token) {
        try {
            if (token == null || !sessions.containsKey(token)) {
                return gson.toJson(new ApiResponse(false, "Non authentifié"));
            }
            
            User user = sessions.get(token);
            
            UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getNom(),
                user.getEmail(),
                user.getAge(),
                user.getRole() != null ? user.getRole().getNom() : "Unknown",
                user.isActif(),
                user.getTelephone()
            );
            
            return gson.toJson(new ApiResponse(true, "Utilisateur connecté", userDTO));
            
        } catch (Exception e) {
            return gson.toJson(new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }
}
