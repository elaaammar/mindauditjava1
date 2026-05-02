package com.audit.auditaifx.model;

import java.util.UUID;

public class Risque {
    private String id;
    private String description;
    private String niveau; // Critique, Élevé, Moyen, Faible
    private String impact;

    public Risque() {
        this.id = UUID.randomUUID().toString();
    }

    public Risque(String description, String niveau, String impact) {
        this();
        this.description = description;
        this.niveau = niveau;
        this.impact = impact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String n) {
        this.niveau = n;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String i) {
        this.impact = i;
    }

    @Override
    public String toString() {
        return "[" + niveau + "] " + description;
    }
}