package com.example.mindjavafx.apirest.dto;

public class UserDTO {
    private int id;
    private String nom;
    private String email;
    private int age;
    private String role;
    private boolean actif;
    private String telephone;

    public UserDTO() {
    }

    public UserDTO(int id, String nom, String email, int age, String role, boolean actif, String telephone) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.age = age;
        this.role = role;
        this.actif = actif;
        this.telephone = telephone;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
