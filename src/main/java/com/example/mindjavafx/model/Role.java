package com.example.mindjavafx.model;

import java.util.ArrayList;
import java.util.List;

public class Role {
    private int id;
    private String nom;
    private String description;
    private List<Permission> permissions;

    public Role(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.permissions = new ArrayList<>();
    }

    public Role(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.permissions = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(Permission permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    @Override
    public String toString() {
        return nom;
    }
}