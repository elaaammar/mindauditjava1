package com.gestionaudit.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reclamation {
    private int id;
    private String titre;
    private String description;
    private LocalDateTime dateCreation;
    private String statut;
    private String priorite;
    private String categorie;
    private String nom;
    private String email;
    private String telephone;
    private List<ReponseReclamation> reponses;

    public static final String STATUT_EN_ATTENTE = "en_attente";
    public static final String STATUT_EN_COURS = "en_cours";
    public static final String STATUT_RESOLUE = "resolue";
    public static final String STATUT_CLOTUREE = "cloturee";

    public static final String PRIORITE_BASSE = "basse";
    public static final String PRIORITE_MOYENNE = "moyenne";
    public static final String PRIORITE_HAUTE = "haute";

    public Reclamation() {
        this.dateCreation = LocalDateTime.now();
        this.statut = STATUT_EN_ATTENTE;
        this.priorite = PRIORITE_MOYENNE;
        this.reponses = new ArrayList<>();
    }

    public Reclamation(int id, String titre, String description, LocalDateTime dateCreation, String statut, String priorite, String categorie, String nom, String email, String telephone) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.priorite = priorite;
        this.categorie = categorie;
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.reponses = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public List<ReponseReclamation> getReponses() { return reponses; }
    public void setReponses(List<ReponseReclamation> reponses) { this.reponses = reponses; }

    @Override
    public String toString() {
        return titre;
    }
}
