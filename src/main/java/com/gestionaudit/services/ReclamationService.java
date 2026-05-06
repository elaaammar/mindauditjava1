package com.gestionaudit.services;

import com.gestionaudit.models.Reclamation;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {

    public void add(Reclamation r) throws SQLException {
        String query = "INSERT INTO reclamation (titre, description, date_creation, statut, priorite, categorie, nom, email, telephone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, r.getTitre());
            pstmt.setString(2, r.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(r.getDateCreation()));
            pstmt.setString(4, r.getStatut());
            pstmt.setString(5, r.getPriorite());
            pstmt.setString(6, r.getCategorie());
            pstmt.setString(7, r.getNom());
            pstmt.setString(8, r.getEmail());
            pstmt.setString(9, r.getTelephone());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    r.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Reclamation r) throws SQLException {
        String query = "UPDATE reclamation SET titre=?, description=?, statut=?, priorite=?, categorie=?, nom=?, email=?, telephone=? WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, r.getTitre());
            pstmt.setString(2, r.getDescription());
            pstmt.setString(3, r.getStatut());
            pstmt.setString(4, r.getPriorite());
            pstmt.setString(5, r.getCategorie());
            pstmt.setString(6, r.getNom());
            pstmt.setString(7, r.getEmail());
            pstmt.setString(8, r.getTelephone());
            pstmt.setInt(9, r.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        Connection conn = DBConnection.getInstance().getConn();
        try {
            conn.setAutoCommit(false);
            // First delete all messages (responses) associated with this reclamation
            String deleteResponses = "DELETE FROM reponse_reclamation WHERE reclamation_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteResponses)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            // Then delete the reclamation itself
            String deleteRec = "DELETE FROM reclamation WHERE id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteRec)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Reclamation> getAll() throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        String query = "SELECT * FROM reclamation ORDER BY date_creation DESC";
        try (Connection conn = DBConnection.getInstance().getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getTimestamp("date_creation").toLocalDateTime(),
                        rs.getString("statut"),
                        rs.getString("priorite"),
                        rs.getString("categorie"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("telephone")
                );
                list.add(r);
            }
        }
        return list;
    }

    public Reclamation getById(int id) throws SQLException {
        String query = "SELECT * FROM reclamation WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Reclamation(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getTimestamp("date_creation").toLocalDateTime(),
                            rs.getString("statut"),
                            rs.getString("priorite"),
                            rs.getString("categorie"),
                            rs.getString("nom"),
                            rs.getString("email"),
                            rs.getString("telephone")
                    );
                }
            }
        }
        return null;
    }
}
