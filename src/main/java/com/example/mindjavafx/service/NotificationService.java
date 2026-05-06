package com.example.mindjavafx.service;

import com.example.mindjavafx.model.Notification;
import com.example.mindjavafx.model.Recommendation;
import com.example.mindjavafx.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    /**
     * Get all notifications for a specific user
     */
    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
            e.printStackTrace();
        }

        return notifications;
    }

    /**
     * Get the count of unread notifications for a user
     */
    public int getUnreadCount(int userId) {
        String query = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND is_read = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting unread notifications: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Mark a notification as read
     */
    public boolean markAsRead(int notificationId) {
        String query = "UPDATE notification SET is_read = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, notificationId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Mark all notifications as read for a user
     */
    public boolean markAllAsRead(int userId) {
        String query = "UPDATE notification SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error marking all notifications as read: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Create a new notification
     */
    public int createNotification(Notification notification) {
        System.out.println("[DEBUG] Création notification pour user ID: " + notification.getUserId());
        String query = "INSERT INTO notification (user_id, title, message, type, related_entity_type, related_entity_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getTitle());
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getType());
            stmt.setString(5, notification.getRelatedEntityType());
            
            if (notification.getRelatedEntityId() != null) {
                stmt.setInt(6, notification.getRelatedEntityId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            System.out.println("[DEBUG] Notification insérée, lignes affectées: " + affectedRows);

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("[DEBUG] ID notification généré: " + id);
                    return id;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public boolean deleteConnectionAlerts() {
        String query = "DELETE FROM notification WHERE title = 'Alerte de Connexion'";
        try (Connection conn = com.example.mindjavafx.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int rows = stmt.executeUpdate();
            System.out.println("[DEBUG] " + rows + " alertes de connexion supprimées.");
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting alerts: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate notifications for recommendations
     */
    public void generateRecommendationNotifications(int userId, List<Recommendation> recommendations) {
        for (Recommendation rec : recommendations) {
            Notification notification = new Notification(
                userId,
                "Nouvelle Recommandation",
                rec.getRecommendationText(),
                "recommendation"
            );
            notification.setRelatedEntityType("recommendation");
            notification.setRelatedEntityId(rec.getId());
            
            createNotification(notification);
        }
    }

    /**
     * Helper method to map ResultSet to Notification object
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setType(rs.getString("type"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setRelatedEntityType(rs.getString("related_entity_type"));
        
        int relatedEntityId = rs.getInt("related_entity_id");
        if (!rs.wasNull()) {
            notification.setRelatedEntityId(relatedEntityId);
        }

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            notification.setCreatedAt(createdAtTs.toLocalDateTime());
        }

        return notification;
    }
}
