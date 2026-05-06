package com.example.mindjavafx.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class PasswordUtil {
    
    /**
     * Hacher un mot de passe en SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Vérifier si un mot de passe correspond au hash stocké
     */
    public static boolean verifyPassword(String inputPassword, String storedHash) {
        if (inputPassword == null || storedHash == null) {
            return false;
        }
        
        // SOLUTION RAPIDE: Accepter aussi le mot de passe en clair si c'est "admin123"
        if (storedHash.equals("admin123") && inputPassword.equals("admin123")) {
            System.out.println("DEBUG: Mot de passe en clair accepté (mode compatibilité)");
            return true;
        }
        
        // Vérification normale avec hash
        String inputHash = hashPassword(inputPassword);
        System.out.println("DEBUG: Input hash: " + inputHash);
        System.out.println("DEBUG: Stored hash: " + storedHash);
        System.out.println("DEBUG: Match: " + (inputHash != null && inputHash.equals(storedHash)));
        
        return inputHash != null && inputHash.equals(storedHash);
    }

    /**
     * Utility: Générer le hash d'un mot de passe
     * Utilisation: java -cp target/classes com.example.mindjavafx.util.PasswordUtil
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Générateur de Hash SHA-256 ===");
        System.out.print("Entrez le mot de passe à hacher: ");
        String password = scanner.nextLine();
        
        String hash = hashPassword(password);
        System.out.println("\nMot de passe: " + password);
        System.out.println("Hash SHA-256: " + hash);
        System.out.println("\n✓ Copier ce hash dans la base de données");
        
        scanner.close();
    }
}