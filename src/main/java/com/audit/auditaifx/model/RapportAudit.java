package com.audit.auditaifx.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RapportAudit {
    private String id;
    private String titre;
    private String auditeur;
    private String entiteAuditee;
    private LocalDate dateCreation;
    private LocalDate dateMiseAJour;
    private StatutRapport statut;
    private String description;
    private List<Recommandation> recommandations;
    private List<Risque> risques;
    private String scoreAudit;

    public RapportAudit() {
        this.id = UUID.randomUUID().toString();
        this.dateCreation = LocalDate.now();
        this.dateMiseAJour = LocalDate.now();
        this.statut = StatutRapport.BROUILLON;
        this.recommandations = new ArrayList<>();
        this.risques = new ArrayList<>();
    }

    public RapportAudit(String titre, String auditeur, String entiteAuditee) {
        this();
        this.titre = titre;
        this.auditeur = auditeur;
        this.entiteAuditee = entiteAuditee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuditeur() {
        return auditeur;
    }

    public void setAuditeur(String auditeur) {
        this.auditeur = auditeur;
    }

    public String getEntiteAuditee() {
        return entiteAuditee;
    }

    public void setEntiteAuditee(String e) {
        this.entiteAuditee = e;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate d) {
        this.dateCreation = d;
    }

    public LocalDate getDateMiseAJour() {
        return dateMiseAJour;
    }

    public void setDateMiseAJour(LocalDate d) {
        this.dateMiseAJour = d;
    }

    public StatutRapport getStatut() {
        return statut;
    }

    public void setStatut(StatutRapport statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Recommandation> getRecommandations() {
        return recommandations;
    }

    public void setRecommandations(List<Recommandation> r) {
        this.recommandations = r;
    }

    public List<Risque> getRisques() {
        return risques;
    }

    public void setRisques(List<Risque> r) {
        this.risques = r;
    }

    public String getScoreAudit() {
        return scoreAudit;
    }

    public void setScoreAudit(String scoreAudit) {
        this.scoreAudit = scoreAudit;
    }

    public void ajouterRisque(Risque r) {
        this.risques.add(r);
    }

    public void ajouterRecommandation(Recommandation r) {
        this.recommandations.add(r);
        this.dateMiseAJour = LocalDate.now();
    }

    @Override
    public String toString() {
        return titre + " — " + statut + " (" + dateCreation + ")";
    }
}