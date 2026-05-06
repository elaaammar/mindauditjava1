package com.example.mindjavafx;

import com.example.mindjavafx.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CleanupNotifications {
    public static void main(String[] args) {
        String query = "DELETE FROM notification WHERE title = 'Alerte de Connexion'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int deleted = stmt.executeUpdate();
            System.out.println("✅ " + deleted + " notifications de connexion supprimées avec succès.");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du nettoyage: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
