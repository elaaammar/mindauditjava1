package com.example.mindjavafx.service;

import com.example.mindjavafx.model.Role;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public UserService() {
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT u.id, u.nom, u.email, u.password_hash, u.age, u.role_id, u.actif, u.telephone, r.nom AS role_nom " +
                     "FROM userjava u LEFT JOIN role r ON u.role_id = r.id WHERE u.email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        }
    }

    public User getUserById(int id) throws SQLException {
        String sql = "SELECT u.id, u.nom, u.email, u.password_hash, u.age, u.role_id, u.actif, u.telephone, r.nom AS role_nom " +
                     "FROM userjava u LEFT JOIN role r ON u.role_id = r.id WHERE u.id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        }
    }
    
    public User getUserByTelephone(String telephone) throws SQLException {
        String sql = "SELECT u.id, u.nom, u.email, u.password_hash, u.age, u.role_id, u.actif, u.telephone, r.nom AS role_nom " +
                     "FROM userjava u LEFT JOIN role r ON u.role_id = r.id WHERE u.telephone = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, telephone);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        }
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT u.id, u.nom, u.email, u.password_hash, u.age, u.role_id, u.actif, u.telephone, r.nom AS role_nom " +
                     "FROM userjava u LEFT JOIN role r ON u.role_id = r.id";
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    public List<User> searchByName(String name) throws SQLException {
        String sql = "SELECT u.id, u.nom, u.email, u.password_hash, u.age, u.role_id, u.actif, u.telephone, r.nom AS role_nom " +
                     "FROM userjava u LEFT JOIN role r ON u.role_id = r.id WHERE u.nom LIKE ?";
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        }
        return users;
    }

    public List<User> searchByAge(int age) throws SQLException {
        String sql = "SELECT u.id, u.nom, u.email, u.password_hash, u.age, u.role_id, u.actif, u.telephone, r.nom AS role_nom " +
                     "FROM userjava u LEFT JOIN role r ON u.role_id = r.id WHERE u.age = ?";
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, age);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        }
        return users;
    }

    public int addUser(User user) throws SQLException {
        String sql = "INSERT INTO userjava (nom, email, password_hash, age, role_id, actif, telephone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setInt(4, user.getAge());
            if (user.getRole() != null) {
                stmt.setInt(5, user.getRole().getId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setBoolean(6, user.isActif());
            stmt.setString(7, user.getTelephone());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE userjava SET nom = ?, email = ?, age = ?, role_id = ?, actif = ?, telephone = ?, password_hash = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.getAge());
            if (user.getRole() != null) {
                stmt.setInt(4, user.getRole().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setBoolean(5, user.isActif());
            stmt.setString(6, user.getTelephone());
            stmt.setString(7, user.getPasswordHash());
            stmt.setInt(8, user.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM userjava WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public int getTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) AS total_users FROM userjava";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt("total_users") : 0;
        }
    }

    public int getActiveUsers() throws SQLException {
        String sql = "SELECT COUNT(*) AS active_users FROM userjava WHERE actif = TRUE";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt("active_users") : 0;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        int age = rs.getInt("age");
        boolean actif = rs.getBoolean("actif");
        String telephone = rs.getString("telephone");
        String roleNom = rs.getString("role_nom");
        int roleId = rs.getInt("role_id");

        Role role = roleNom != null ? new Role(roleId, roleNom) : new Role(0, "Unknown");
        User user = new User(id, nom, email, age, role, actif);
        user.setPasswordHash(passwordHash);
        user.setTelephone(telephone);
        return user;
    }
}
