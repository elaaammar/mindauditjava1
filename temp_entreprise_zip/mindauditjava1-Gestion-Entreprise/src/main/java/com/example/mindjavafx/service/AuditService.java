package com.example.mindjavafx.service;

import com.example.mindjavafx.model.Audit;
import com.example.mindjavafx.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditService {

    /**
     * Get all audits for a specific user
     */
    public List<Audit> getAuditsByUserId(int userId) {
        List<Audit> audits = new ArrayList<>();
        String query = "SELECT * FROM audit WHERE user_id = ? ORDER BY audit_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching audits for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return audits;
    }

    /**
     * Get a specific audit by ID
     */
    public Audit getAuditById(int auditId) {
        String query = "SELECT * FROM audit WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, auditId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAudit(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching audit " + auditId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create a new audit
     */
    public int createAudit(Audit audit) {
        String query = "INSERT INTO audit (user_id, name, category, global_score, " +
                      "security_score, compliance_score, performance_score, findings, status) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, audit.getUserId());
            stmt.setString(2, audit.getName());
            stmt.setString(3, audit.getCategory());
            stmt.setInt(4, audit.getGlobalScore());
            stmt.setInt(5, audit.getSecurityScore());
            stmt.setInt(6, audit.getComplianceScore());
            stmt.setInt(7, audit.getPerformanceScore());
            stmt.setString(8, audit.getFindings());
            stmt.setString(9, audit.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating audit: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Update an existing audit
     */
    public boolean updateAudit(Audit audit) {
        String query = "UPDATE audit SET name = ?, category = ?, global_score = ?, " +
                      "security_score = ?, compliance_score = ?, performance_score = ?, " +
                      "findings = ?, status = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, audit.getName());
            stmt.setString(2, audit.getCategory());
            stmt.setInt(3, audit.getGlobalScore());
            stmt.setInt(4, audit.getSecurityScore());
            stmt.setInt(5, audit.getComplianceScore());
            stmt.setInt(6, audit.getPerformanceScore());
            stmt.setString(7, audit.getFindings());
            stmt.setString(8, audit.getStatus());
            stmt.setInt(9, audit.getId());
            stmt.setInt(10, audit.getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating audit: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete an audit
     */
    public boolean deleteAudit(int auditId) {
        String query = "DELETE FROM audit WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, auditId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting audit: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get the count of audits for a user
     */
    public int getAuditCountByUserId(int userId) {
        String query = "SELECT COUNT(*) FROM audit WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting audits: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get the most recent audit for a user
     */
    public Audit getMostRecentAudit(int userId) {
        String query = "SELECT * FROM audit WHERE user_id = ? ORDER BY audit_date DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAudit(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching most recent audit: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get audits within a date range
     */
    public List<Audit> getAuditsByDateRange(int userId, LocalDate start, LocalDate end) {
        List<Audit> audits = new ArrayList<>();
        String query = "SELECT * FROM audit WHERE user_id = ? AND DATE(audit_date) BETWEEN ? AND ? " +
                      "ORDER BY audit_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching audits by date range: " + e.getMessage());
            e.printStackTrace();
        }

        return audits;
    }

    /**
     * Helper method to map ResultSet to Audit object
     */
    private Audit mapResultSetToAudit(ResultSet rs) throws SQLException {
        Audit audit = new Audit();
        audit.setId(rs.getInt("id"));
        audit.setUserId(rs.getInt("user_id"));
        audit.setName(rs.getString("name"));
        audit.setCategory(rs.getString("category"));
        audit.setGlobalScore(rs.getInt("global_score"));
        audit.setSecurityScore(rs.getInt("security_score"));
        audit.setComplianceScore(rs.getInt("compliance_score"));
        audit.setPerformanceScore(rs.getInt("performance_score"));
        audit.setFindings(rs.getString("findings"));
        audit.setStatus(rs.getString("status"));

        Timestamp auditDateTs = rs.getTimestamp("audit_date");
        if (auditDateTs != null) {
            audit.setAuditDate(auditDateTs.toLocalDateTime());
        }

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            audit.setCreatedAt(createdAtTs.toLocalDateTime());
        }

        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            audit.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }

        return audit;
    }
}
