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
        try {
            java.io.File configFile = new java.io.File("config.properties");
            System.out.println("[EmailService] Recherche du fichier config dans: " + configFile.getAbsolutePath());
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    config.load(fis);
                    System.out.println("[EmailService] config.properties chargé avec succès.");
                }
            } else {
                System.err.println("[EmailService] ERREUR: config.properties introuvable à " + configFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("[EmailService] Erreur lors du chargement: " + e.getMessage());
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
     * Envoie un email d'alerte de connexion de manière asynchrone.
     */
    public void sendLoginAlertAsync(String userName, String userEmail) {
        System.out.println("[EmailService] Préparation de l'envoi d'alerte pour: " + userName);
        new Thread(() -> {
            try {
                sendEmail(
                    config.getProperty("mail.admin.target", "eleammar21@gmail.com"),
                    "🚨 Alerte Connexion - MindAudit",
                    "Bonjour,\n\n" +
                    "Une nouvelle connexion a été détectée sur MindAudit :\n" +
                    "- Utilisateur : " + userName + "\n" +
                    "- Email : " + userEmail + "\n" +
                    "- Heure : " + java.time.LocalDateTime.now() + "\n\n" +
                    "Ceci est un message automatique de sécurité."
                );
                System.out.println("[EmailService] Email d'alerte envoyé avec succès.");
            } catch (Exception e) {
                System.err.println("[EmailService] Échec de l'envoi de l'email: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        String host = config.getProperty("mail.smtp.host");
        String port = config.getProperty("mail.smtp.port");
        String username = config.getProperty("mail.username");
        String password = config.getProperty("mail.password");

        if (username == null || password == null || username.contains("VOTRE_EMAIL")) {
            throw new MessagingException("Configuration SMTP manquante dans config.properties");
        }

        String fromEmail = config.getProperty("mail.admin.target", username);

        Properties props = new Properties();
        props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.trust", host);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail, "MindAudit"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);
    }
}
