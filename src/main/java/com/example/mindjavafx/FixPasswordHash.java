package com.example.mindjavafx;

import com.example.mindjavafx.util.DatabaseConnection;
import com.example.mindjavafx.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FixPasswordHash {
    
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("✓ Connexion à la base de données réussie");
            
            // Vérifier le hash actuel
            String checkSql = "SELECT id, email, password_hash, actif FROM userjava WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                stmt.setString(1, "admin@mindaudit.com");
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String email = rs.getString("email");
                    String currentHash = rs.getString("password_hash");
                    boolean actif = rs.getBoolean("actif");
                    
                    System.out.println("\n=== Utilisateur trouvé ===");
                    System.out.println("ID: " + id);
                    System.out.println("Email: " + email);
                    System.out.println("Actif: " + actif);
                    System.out.println("Hash actuel: " + currentHash);
                    
                    // Générer le bon hash pour "admin123"
                    String correctHash = PasswordUtil.hashPassword("admin123");
                    System.out.println("Hash correct pour 'admin123': " + correctHash);
                    
                    if (!correctHash.equals(currentHash)) {
                        System.out.println("\n⚠ Le hash est incorrect! Mise à jour...");
                        
                        // Mettre à jour le hash
                        String updateSql = "UPDATE userjava SET password_hash = ?, actif = 1 WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, correctHash);
                            updateStmt.setInt(2, id);
                            int updated = updateStmt.executeUpdate();
                            
                            if (updated > 0) {
                                System.out.println("✓ Hash mis à jour avec succès!");
                                System.out.println("\nVous pouvez maintenant vous connecter avec:");
                                System.out.println("Email: admin@mindaudit.com");
                                System.out.println("Mot de passe: admin123");
                            }
                        }
                    } else {
                        System.out.println("\n✓ Le hash est déjà correct!");
                    }
                } else {
                    System.out.println("\n⚠ Aucun utilisateur trouvé avec l'email admin@mindaudit.com");
                    System.out.println("Création de l'utilisateur admin...");
                    
                    // Créer l'utilisateur admin
                    String insertSql = "INSERT INTO userjava (nom, email, password_hash, age, role_id, actif, telephone) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, "Administrateur");
                        insertStmt.setString(2, "admin@mindaudit.com");
                        insertStmt.setString(3, PasswordUtil.hashPassword("admin123"));
                        insertStmt.setInt(4, 30);
                        insertStmt.setInt(5, 1);
                        insertStmt.setBoolean(6, true);
                        insertStmt.setString(7, "0000000000");
                        
                        int inserted = insertStmt.executeUpdate();
                        if (inserted > 0) {
                            System.out.println("✓ Utilisateur admin créé avec succès!");
                            System.out.println("\nVous pouvez maintenant vous connecter avec:");
                            System.out.println("Email: admin@mindaudit.com");
                            System.out.println("Mot de passe: admin123");
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("✗ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
