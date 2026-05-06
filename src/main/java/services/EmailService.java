package services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.util.Properties;

public class EmailService {

    // IMPORTANT: For Gmail, you must use an "App Password" 
    // Go to: https://myaccount.google.com/apppasswords
    private final String username = "benharizfourat88@gmail.com"; 
    private final String password = "djyeeqjfckteoopl"; 

    public void sendAuditCompletionEmail(String recipientEmail, String reportTitle, int score) {
        System.out.println("DEBUG: Preparing to send email to " + recipientEmail + " for report: " + reportTitle);
        
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Mandatory for some Java versions

        Session session = Session.getInstance(prop, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("🔔 Alerte Audit : " + reportTitle);

            String status = (score >= 75) ? "EXCELLENT" : (score >= 50) ? "PASSABLE" : "NON-CONFORME";
            String color = (score >= 75) ? "#27ae60" : (score >= 50) ? "#f39c12" : "#e74c3c";
            
            String body = "<div style='font-family: Arial, sans-serif; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #2c3e50;'>Rapport d'Audit Terminé</h2>"
                    + "<p>L'audit pour <b>" + reportTitle + "</b> vient d'être finalisé.</p>"
                    + "<div style='background: " + color + "; color: white; padding: 15px; border-radius: 5px; font-size: 18px;'>"
                    + "Score Global : <b>" + score + "%</b> (" + status + ")"
                    + "</div>"
                    + "<p style='margin-top: 20px;'>Consultez le dashboard pour voir le détail des réponses.</p>"
                    + "<hr><p style='font-size: 11px; color: #7f8c8d;'>Notification automatique - Antigravity Systems</p>"
                    + "</div>";

            message.setContent(body, "text/html; charset=utf-8");

            Thread mailThread = new Thread(() -> {
                try {
                    System.out.println("DEBUG: Attempting to connect to Gmail SMTP server...");
                    Transport.send(message);
                    System.out.println("✅ SUCCESS: Email sent successfully to " + recipientEmail);
                } catch (MessagingException e) {
                    System.err.println("❌ ERROR: Failed to send email!");
                    e.printStackTrace();
                    
                    // Show error alert in the UI
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur de Notification");
                        alert.setHeaderText("Gmail a refusé l'envoi");
                        String errorMsg = e.getMessage();
                        if (errorMsg.contains("username and password not accepted")) {
                            alert.setContentText("Votre mot de passe d'application est incorrect ou expiré.\n\nVérifiez la clé 'djyeeqjfkteoopl' dans EmailService.java");
                        } else {
                            alert.setContentText("Détail de l'erreur : " + errorMsg);
                        }
                        alert.show();
                    });
                }
            });
            mailThread.setDaemon(true);
            mailThread.start();

        } catch (MessagingException e) {
            System.err.println("❌ ERROR: Could not create MIME message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
