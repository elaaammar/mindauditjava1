package com.example.mindjavafx.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mindaudit_java";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        initializeTables(conn);
        return conn;
    }

    private static void initializeTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Create entreprises table
            stmt.execute("CREATE TABLE IF NOT EXISTS entreprises (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom VARCHAR(255) NOT NULL, " +
                    "matricule_fiscale VARCHAR(255), " +
                    "secteur VARCHAR(255), " +
                    "taille VARCHAR(255), " +
                    "pays VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "telephone VARCHAR(255), " +
                    "adresse TEXT, " +
                    "statut VARCHAR(50) DEFAULT 'en_attente', " +
                    "owner_id INT, " +
                    "latitude DOUBLE, " +
                    "longitude DOUBLE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Create documents table
            stmt.execute("CREATE TABLE IF NOT EXISTS documents (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom VARCHAR(255) NOT NULL, " +
                    "type VARCHAR(50), " +
                    "path TEXT, " +
                    "entreprise_id INT, " +
                    "FOREIGN KEY (entreprise_id) REFERENCES entreprises(id) ON DELETE CASCADE)");
        } catch (SQLException e) {
            System.err.println("Table initialization failed: " + e.getMessage());
        }
    }
}
