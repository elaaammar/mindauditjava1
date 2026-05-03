package com.example.mindjavafx.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String nom;
    private String email;
    private String passwordHash;
    private int age;
    private Role role;
    private boolean actif;
    private LocalDateTime dateCreation;
    private String telephone;
    private boolean allowedOnce; // Pour compatibilité

    // Constructeur simple pour tests
    public User(String nom) {
        this.nom = nom;
        this.allowedOnce = false;
        this.actif = true;
    }

    public User(int id, String nom, String email, int age, Role role, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.age = age;
        this.role = role;
        this.actif = actif;
    }

    public User(String nom, String email, String passwordHash, int age, Role role) {
        this.nom = nom;
        this.email = email;
        this.passwordHash = passwordHash;
        this.age = age;
        this.role = role;
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    // Méthodes de compatibilité
    public boolean isAllowedOnce() {
        return allowedOnce;
    }

    public void setAllowedOnce(boolean allowedOnce) {
        this.allowedOnce = allowedOnce;
    }

    public String getName() {
        return nom;
    }

    public void setName(String name) {
        this.nom = name;
    }

    @Override
    public String toString() {
        return nom + (email != null ? " (" + email + ")" : "");
    }
}