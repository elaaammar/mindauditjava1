package com.gestionaudit.models;

import java.time.LocalDateTime;

public class ReponseReclamation {
    private int id;
    private String contenu;
    private LocalDateTime dateCreation;
    private int reclamationId;
    private String auteurType;
    private String avisUtilisateur;
    private String nom;

    public ReponseReclamation() {
        this.dateCreation = LocalDateTime.now();
    }

    public ReponseReclamation(int id, String contenu, LocalDateTime dateCreation, int reclamationId, String auteurType, String avisUtilisateur, String nom) {
        this.id = id;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
        this.reclamationId = reclamationId;
        this.auteurType = auteurType;
        this.avisUtilisateur = avisUtilisateur;
        this.nom = nom;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public int getReclamationId() { return reclamationId; }
    public void setReclamationId(int reclamationId) { this.reclamationId = reclamationId; }

    public String getAuteurType() { return auteurType; }
    public void setAuteurType(String auteurType) { this.auteurType = auteurType; }

    public String getAvisUtilisateur() { return avisUtilisateur; }
    public void setAvisUtilisateur(String avisUtilisateur) { this.avisUtilisateur = avisUtilisateur; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
