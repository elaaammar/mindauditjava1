package com.audit.auditaifx.model;

public class Recommandation {
    private String id;
    private String description;
    private String priorite; // "Haute", "Moyenne", "Faible"
    private boolean resolue;

    public Recommandation() {
    }

    public Recommandation(String description, String priorite) {
        this.description = description;
        this.priorite = priorite;
        this.resolue = false;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public boolean isResolue() {
        return resolue;
    }

    public void setResolue(boolean resolue) {
        this.resolue = resolue;
    }

    @Override
    public String toString() {
        return "[" + priorite + "] " + description + (resolue ? " ✓" : "");
    }
}