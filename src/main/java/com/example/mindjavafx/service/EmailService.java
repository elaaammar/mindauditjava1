package com.example.mindjavafx.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

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
     * Envoie un email d'alerte de connexion de manière asynchrone.
     * @param userName Le nom de l'utilisateur qui s'est connecté.
     * @param userEmail L'email de l'utilisateur qui s'est connecté.
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

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        String host = config.getProperty("mail.smtp.host");
        String port = config.getProperty("mail.smtp.port");
        String username = config.getProperty("mail.username");
        String password = config.getProperty("mail.password");

        if (username == null || password == null || username.contains("VOTRE_EMAIL")) {
            throw new MessagingException("Configuration SMTP manquante dans config.properties");
        }

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
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);
    }
}
