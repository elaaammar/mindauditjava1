package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private String url = "jdbc:mysql://127.0.0.1:3306/mindaudit_java?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    private String user = "root";
    private String password = "";
    private Connection conn;
    private static DBConnection instance;

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConn() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, user, password);
                initializeTables();
                System.out.println("Connection re-established");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private DBConnection() {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
            initializeTables();
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.out.println("Error establishing connection: " + e.getMessage());
        }
    }

    private void initializeTables() {
        if (conn == null) return;
        try (java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS rapport (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "description TEXT, " +
                    "date VARCHAR(255), " +
                    "type VARCHAR(255), " +
                    "duration INT DEFAULT 0)");

            stmt.execute("CREATE TABLE IF NOT EXISTS question_audit (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "content TEXT NOT NULL, " +
                    "type VARCHAR(255), " +
                    "bonne_reponse VARCHAR(255), " +
                    "score_possible INT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS reclamation (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "titre VARCHAR(255) NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "statut VARCHAR(50) DEFAULT 'en_attente', " +
                    "priorite VARCHAR(50) DEFAULT 'moyenne', " +
                    "categorie VARCHAR(255), " +
                    "nom VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "telephone VARCHAR(50))");

            stmt.execute("CREATE TABLE IF NOT EXISTS reponse_reclamation (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "contenu TEXT NOT NULL, " +
                    "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "reclamation_id INT NOT NULL, " +
                    "auteur_type VARCHAR(50), " +
                    "avis_utilisateur VARCHAR(255), " +
                    "nom VARCHAR(255), " +
                    "FOREIGN KEY (reclamation_id) REFERENCES reclamation(id) ON DELETE CASCADE)");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}
