package com.example.mindjavafx.apirest;

import com.example.mindjavafx.apirest.dto.ApiResponse;
import com.example.mindjavafx.apirest.dto.UserDTO;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.UserService;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * API REST pour la gestion des utilisateurs
 * Endpoints: GET, POST, PUT, DELETE /api/users
 */
public class UserAPI {
    
    private UserService userService;
    private Gson gson;

    public UserAPI() {
        this.userService = new UserService();
        this.gson = new Gson();
    }

    public String getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            List<UserDTO> userDTOs = new ArrayList<>();
            
            for (User user : users) {
                UserDTO dto = new UserDTO(
                    user.getId(),
                    user.getNom(),
                    user.getEmail(),
                    user.getAge(),
                    user.getRole() != null ? user.getRole().getNom() : "Unknown",
                    user.isActif(),
                    user.getTelephone()
                );
                userDTOs.add(dto);
            }
            
            return gson.toJson(new ApiResponse(true, "Utilisateurs récupérés", userDTOs));
        } catch (SQLException e) {
            // Si erreur base de données, retourner un message d'erreur clair
            return gson.toJson(new ApiResponse(false, "Erreur base de données: " + e.getMessage()));
        } catch (Exception e) {
            return gson.toJson(new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }
}
