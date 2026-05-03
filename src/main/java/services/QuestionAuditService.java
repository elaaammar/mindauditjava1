package services;

import models.QuestionAudit;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionAuditService implements IService<QuestionAudit> {
    Connection conn;

    public QuestionAuditService() {
        this.conn = DBConnection.getInstance().getConn();
        ensureBonneReponseColumnExists();
    }

    private void ensureBonneReponseColumnExists() {
        if (conn == null) {
            System.out.println("Warning: Database connection is null. Skipping table schema check.");
            return;
        }
        try (Statement st = conn.createStatement()) {
            st.execute("ALTER TABLE question_audit ADD COLUMN IF NOT EXISTS bonne_reponse TEXT NULL");
            st.execute("ALTER TABLE question_audit ADD COLUMN IF NOT EXISTS time_limit INT DEFAULT 0");
        } catch (SQLException e) {
            // Column may already exist
        }
    }

    @Override
    public void add(QuestionAudit questionAudit) {
        String SQL = "INSERT INTO question_audit (content, type, bonne_reponse, time_limit) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, questionAudit.getContent());
            pstmt.setString(2, questionAudit.getType());
            pstmt.setString(3, questionAudit.getBonneReponse());
            pstmt.setInt(4, questionAudit.getTimeLimit());
            pstmt.executeUpdate();
            System.out.println("QuestionAudit added successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateBonneReponse(int questionId, String bonneReponse) {
        String SQL = "UPDATE question_audit SET bonne_reponse = ? WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, bonneReponse);
            pstmt.setInt(2, questionId);
            pstmt.executeUpdate();
            System.out.println("Bonne reponse updated for question " + questionId);
        } catch (SQLException e) {
            System.out.println("Error updating bonne reponse: " + e.getMessage());
        }
    }

    @Override
    public void update(QuestionAudit questionAudit) {
        String SQL = "UPDATE question_audit SET content = ?, type = ?, bonne_reponse = ?, time_limit = ? WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, questionAudit.getContent());
            pstmt.setString(2, questionAudit.getType());
            pstmt.setString(3, questionAudit.getBonneReponse());
            pstmt.setInt(4, questionAudit.getTimeLimit());
            pstmt.setInt(5, questionAudit.getId());
            pstmt.executeUpdate();
            System.out.println("QuestionAudit updated successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(QuestionAudit questionAudit) {
        String SQL = "DELETE FROM question_audit WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, questionAudit.getId());
            pstmt.executeUpdate();
            System.out.println("QuestionAudit deleted successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<QuestionAudit> getAll() {
        String SQL = "SELECT * FROM question_audit";
        List<QuestionAudit> questionAudits = new ArrayList<>();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(SQL);
            while (rs.next()) {
                QuestionAudit q = new QuestionAudit();
                q.setId(rs.getInt("id"));
                q.setContent(rs.getString("content"));
                q.setType(rs.getString("type"));
                q.setBonneReponse(rs.getString("bonne_reponse"));
                q.setTimeLimit(rs.getInt("time_limit"));
                questionAudits.add(q);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return questionAudits;
    }

    public List<QuestionAudit> getByType(String type) {
        String SQL = "SELECT * FROM question_audit WHERE type = ?";
        List<QuestionAudit> filteredList = new ArrayList<>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                QuestionAudit q = new QuestionAudit();
                q.setId(rs.getInt("id"));
                q.setContent(rs.getString("content"));
                q.setType(rs.getString("type"));
                q.setBonneReponse(rs.getString("bonne_reponse"));
                q.setTimeLimit(rs.getInt("time_limit"));
                filteredList.add(q);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return filteredList;
    }
}
