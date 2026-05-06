package com.gestion.util;

public class MailService {
    public static void sendEntrepriseValidee(String email, String nom, String matricule, String secteur) {
        System.out.println("Email sent to " + email + ": Entreprise validated.");
    }
    public static void sendEntrepriseRejetee(String email, String nom, String matricule, String secteur) {
        System.out.println("Email sent to " + email + ": Entreprise rejected.");
    }
}
