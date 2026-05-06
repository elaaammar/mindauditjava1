package com.example.mindjavafx.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class EmailService {

    private final Properties config = new Properties();

    public EmailService() {
        loadConfig();
    }

    private void loadConfig() {
        String[] possiblePaths = {"config.properties", "src/main/resources/config.properties", "../config.properties"};
        boolean loaded = false;

        for (String path : possiblePaths) {
            java.io.File configFile = new java.io.File(path);
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    config.load(fis);
                    System.out.println("[EmailService] config.properties chargé depuis: " + configFile.getAbsolutePath());
                    loaded = true;
                    break;
                } catch (IOException e) {
                    System.err.println("[EmailService] Erreur lors du chargement de " + path + ": " + e.getMessage());
                }
            }
        }

        if (!loaded) {
            // Tentative via ClassLoader
            try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (is != null) {
                    config.load(is);
                    System.out.println("[EmailService] config.properties chargé via ClassLoader.");
                    loaded = true;
                }
            } catch (Exception e) {
                System.err.println("[EmailService] Erreur via ClassLoader: " + e.getMessage());
            }
        }

        if (!loaded) {
            System.err.println("[EmailService] ATTENTION: Impossible de trouver config.properties. L'envoi d'emails risque d'échouer.");
        }
    }

    /**
     * Envoie un code de réinitialisation de mot de passe de manière asynchrone.
     */
    public void sendPasswordResetCodeAsync(String userEmail, String code) {
        System.out.println("[EmailService] Envoi du code de réinitialisation à: " + userEmail);
        new Thread(() -> {
            try {
                sendEmail(
                    userEmail,
                    "🔑 Code de Réinitialisation - MindAudit",
                    "Bonjour,\n\n" +
                    "Vous avez demandé la réinitialisation de votre mot de passe MindAudit.\n\n" +
                    "Votre code de vérification est : " + code + "\n\n" +
                    "Ce code est personnel et ne doit pas être partagé. Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer cet email.\n\n" +
                    "L'équipe MindAudit"
                );
                System.out.println("[EmailService] Code de réinitialisation envoyé avec succès.");
            } catch (Exception e) {
                System.err.println("[EmailService] Échec de l'envoi du code: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Envoie un email d'alerte de connexion de manière asynchrone au client ET à l'administrateur.
     */
    public void sendLoginAlertAsync(String userName, String userEmail) {
        System.out.println("[EmailService] Préparation de l'envoi d'alerte pour: " + userName);
        new Thread(() -> {
            try {
                String adminEmail = config.getProperty("mail.admin.target", "eleammar21@gmail.com");
                
                String subject = "🚨 Alerte Connexion - MindAudit";
                String content = "Bonjour,\n\n" +
                                "Une nouvelle connexion a été détectée sur votre compte MindAudit :\n" +
                                "- Utilisateur : " + userName + "\n" +
                                "- Email : " + userEmail + "\n" +
                                "- Heure : " + java.time.LocalDateTime.now() + "\n\n" +
                                "Si vous n'êtes pas à l'origine de cette connexion, veuillez changer votre mot de passe immédiatement.\n\n" +
                                "L'équipe MindAudit";

                // 1. Envoyer à l'utilisateur
                sendEmail(userEmail, subject, content);
                
                // 2. Envoyer à l'admin (si différent)
                if (!userEmail.equalsIgnoreCase(adminEmail)) {
                    sendEmail(adminEmail, "[ADMIN] Alerte Connexion Utilisateur", 
                             "Une connexion utilisateur a eu lieu :\n- Nom: " + userName + "\n- Email: " + userEmail);
                }
                
                System.out.println("[EmailService] Emails d'alerte envoyés avec succès.");
            } catch (Exception e) {
                System.err.println("[EmailService] Échec de l'envoi de l'email: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        // Paramètres GMAIL (plus fiables que Brevo sans validation de domaine)
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "benharizfourat88@gmail.com"; 
        String password = "djyeeqjfckteoopl"; // App Password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.trust", host);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "MindAudit"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            System.out.println("[EmailService] SUCCESS: Email envoyé à " + to);
            
        } catch (MessagingException e) {
            System.err.println("[EmailService] ERROR: " + e.getMessage());
            
            // Afficher une alerte visuelle pour que l'utilisateur voit l'erreur
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'Envoi Email");
                alert.setHeaderText("Gmail a refusé la connexion");
                alert.setContentText("Détail technique : " + e.getMessage() + 
                                   "\n\nVérifiez que votre connexion internet est active et que le port 587 n'est pas bloqué.");
                alert.show();
            });
            throw e;
        }
    }
}
