package com.audit.auditaifx.model;

public enum StatutRapport {
    BROUILLON("Brouillon"),
    EN_COURS("En cours"),
    FINALISE("Finalisé"),
    ARCHIVE("Archivé");

    private final String libelle;

    StatutRapport(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}