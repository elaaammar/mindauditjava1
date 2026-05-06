package tn.esprit;

import util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class CreateReponseTable {
    public static void main(String[] args) {
        String sql = "CREATE TABLE IF NOT EXISTS reponse_question (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "question_id INT NOT NULL, " +
                "rapport_id INT NOT NULL, " +
                "reponse TEXT, " +
                "date_reponse TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (question_id) REFERENCES question_audit(id) ON DELETE CASCADE" +
                ")";
        
        try (Connection conn = DBConnection.getInstance().getConn();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'reponse_question' created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
