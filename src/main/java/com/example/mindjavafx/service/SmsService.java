package com.example.mindjavafx.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service simple pour gérer les codes de vérification
 * SANS avoir besoin d'envoyer de vrais SMS
 */
public class SmsService {
    
    // Stocke les codes générés (en mémoire)
    private Map<String, String> verificationCodes = new HashMap<>();
    
    /**
     * "Envoie" un code de vérification (en réalité, le stocke juste)
     * @param toPhoneNumber Numéro de téléphone
     * @param verificationCode Code à 6 chiffres
     * @return true toujours (simulation)
     */
    public boolean sendVerificationCode(String toPhoneNumber, String verificationCode) {
        // Stocker le code
        verificationCodes.put(toPhoneNumber, verificationCode);
        
        System.out.println("═══════════════════════════════════════");
        System.out.println("📱 CODE DE VÉRIFICATION");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Téléphone: " + toPhoneNumber);
        System.out.println("Code: " + verificationCode);
        System.out.println("═══════════════════════════════════════");
        
        return true;
    }
    
    /**
     * "Envoie" un code par email (simulation)
     * @param toEmail Email du destinataire
     * @param verificationCode Code à 6 chiffres
     * @return true toujours (simulation)
     */
    public boolean sendVerificationEmail(String toEmail, String verificationCode) {
        // Stocker le code
        verificationCodes.put(toEmail, verificationCode);
        
        System.out.println("═══════════════════════════════════════");
        System.out.println("📧 CODE DE VÉRIFICATION");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Email: " + toEmail);
        System.out.println("Code: " + verificationCode);
        System.out.println("═══════════════════════════════════════");
        
        return true;
    }
    
    /**
     * Récupère le dernier code généré pour un contact
     */
    public String getLastCode(String contact) {
        return verificationCodes.get(contact);
    }
}
