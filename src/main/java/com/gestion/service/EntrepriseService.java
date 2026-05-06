package com.gestion.service;

import com.gestion.entity.Entreprise;
import com.example.mindjavafx.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrepriseService {
    
    public List<Entreprise> findAll() throws SQLException {
        List<Entreprise> list = new ArrayList<>();
        String sql = "SELECT * FROM entreprises";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToEntreprise(rs));
            }
        }
        return list;
    }

    public List<Entreprise> findByOwnerId(int ownerId) throws SQLException {
        List<Entreprise> list = new ArrayList<>();
        String sql = "SELECT * FROM entreprises WHERE owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntreprise(rs));
                }
            }
        }
        return list;
    }

    public void add(Entreprise e) throws SQLException {
        String sql = "INSERT INTO entreprises (nom, matricule_fiscale, secteur, taille, pays, email, telephone, adresse, statut, owner_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, e.getNom());
            pstmt.setString(2, e.getMatriculeFiscale());
            pstmt.setString(3, e.getSecteur());
            pstmt.setString(4, e.getTaille());
            pstmt.setString(5, e.getPays());
            pstmt.setString(6, e.getEmail());
            pstmt.setString(7, e.getTelephone());
            pstmt.setString(8, e.getAdresse());
            pstmt.setString(9, e.getStatut());
            pstmt.setInt(10, e.getOwnerId());
            pstmt.executeUpdate();
        }
    }

    public void update(Entreprise e) throws SQLException {
        String sql = "UPDATE entreprises SET nom=?, matricule_fiscale=?, secteur=?, taille=?, pays=?, email=?, telephone=?, adresse=?, statut=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, e.getNom());
            pstmt.setString(2, e.getMatriculeFiscale());
            pstmt.setString(3, e.getSecteur());
            pstmt.setString(4, e.getTaille());
            pstmt.setString(5, e.getPays());
            pstmt.setString(6, e.getEmail());
            pstmt.setString(7, e.getTelephone());
            pstmt.setString(8, e.getAdresse());
            pstmt.setString(9, e.getStatut());
            pstmt.setInt(10, e.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM entreprises WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Entreprise mapResultSetToEntreprise(ResultSet rs) throws SQLException {
        Entreprise e = new Entreprise();
        e.setId(rs.getInt("id"));
        e.setNom(rs.getString("nom"));
        e.setMatriculeFiscale(rs.getString("matricule_fiscale"));
        e.setSecteur(rs.getString("secteur"));
        e.setTaille(rs.getString("taille"));
        e.setPays(rs.getString("pays"));
        e.setEmail(rs.getString("email"));
        e.setTelephone(rs.getString("telephone"));
        e.setAdresse(rs.getString("adresse"));
        e.setStatut(rs.getString("statut"));
        e.setOwnerId(rs.getInt("owner_id"));
        return e;
    }
}
