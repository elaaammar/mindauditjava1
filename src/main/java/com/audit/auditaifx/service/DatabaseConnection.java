package com.audit.auditaifx.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3307/auditai_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // vide par défaut sur XAMPP

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion MySQL réussie !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur connexion : " + e.getMessage());
        }
        return connection;
    }
}