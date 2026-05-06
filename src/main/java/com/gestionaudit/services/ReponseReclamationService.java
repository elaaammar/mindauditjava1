package com.gestionaudit.services;

import com.gestionaudit.models.ReponseReclamation;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseReclamationService {

    public void add(ReponseReclamation r) throws SQLException {
        String query = "INSERT INTO reponse_reclamation (contenu, date_creation, reclamation_id, auteur_type, avis_utilisateur, nom) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, r.getContenu());
            pstmt.setTimestamp(2, Timestamp.valueOf(r.getDateCreation()));
            pstmt.setInt(3, r.getReclamationId());
            pstmt.setString(4, r.getAuteurType());
            pstmt.setString(5, r.getAvisUtilisateur());
            pstmt.setString(6, r.getNom());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    r.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<ReponseReclamation> getByReclamationId(int reclamationId) throws SQLException {
        List<ReponseReclamation> list = new ArrayList<>();
        String query = "SELECT * FROM reponse_reclamation WHERE reclamation_id = ? ORDER BY date_creation ASC";
        try (Connection conn = DBConnection.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, reclamationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReponseReclamation r = new ReponseReclamation(
                            rs.getInt("id"),
                            rs.getString("contenu"),
                            rs.getTimestamp("date_creation").toLocalDateTime(),
                            rs.getInt("reclamation_id"),
                            rs.getString("auteur_type"),
                            rs.getString("avis_utilisateur"),
                            rs.getString("nom")
                    );
                    list.add(r);
                }
            }
        }
        return list;
    }

    public void update(ReponseReclamation r) throws SQLException {
        String query = "UPDATE reponse_reclamation SET contenu=? WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, r.getContenu());
            pstmt.setInt(2, r.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM reponse_reclamation WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
