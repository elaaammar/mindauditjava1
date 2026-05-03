package com.example.mindjavafx.apirest;

import com.example.mindjavafx.apirest.dto.ApiResponse;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.UserService;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST pour les statistiques
 * Endpoints: GET /api/stats/*
 */
public class StatsAPI {
    
    private UserService userService;
    private Gson gson;

    public StatsAPI() {
        this.userService = new UserService();
        this.gson = new Gson();
    }

    public String getAllStats() {
        try {
            int total = userService.getTotalUsers();
            int active = userService.getActiveUsers();
            List<User> users = userService.getAllUsers();
            
            Map<String, Integer> roleCount = new HashMap<>();
            roleCount.put("Admin", 0);
            roleCount.put("User", 0);
            roleCount.put("Auditeur", 0);
            
            for (User user : users) {
                String role = user.getRole() != null ? user.getRole().getNom() : "Unknown";
                roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);
            }
            
            Map<String, Object> allStats = new HashMap<>();
            allStats.put("total", total);
            allStats.put("active", active);
            allStats.put("inactive", total - active);
            allStats.put("roles", roleCount);
            
            return gson.toJson(new ApiResponse(true, "Toutes les statistiques", allStats));
        } catch (SQLException e) {
            return gson.toJson(new ApiResponse(false, "Erreur base de données: " + e.getMessage()));
        } catch (Exception e) {
            return gson.toJson(new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }
}
