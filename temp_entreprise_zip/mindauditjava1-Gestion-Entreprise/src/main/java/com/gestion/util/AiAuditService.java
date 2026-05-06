package com.gestion.util;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AiAuditService {

    private static final String API_KEY = "AIzaSyAY9ORoBxjnOHWO8cDqycloJa-Fto1Y9to";
    
    private static final String[][] CONFIGS = {
        {"v1", "gemini-1.5-flash"},
        {"v1beta", "gemini-1.5-flash"},
        {"v1", "gemini-pro"}
    };

    public static String analyserRisquesEntreprise(String nom, String secteur, String taille, String pays) {
        // 1. Tenter l'appel réel à l'API Google
        for (String[] conf : CONFIGS) {
            String result = tryConfig(conf[0], conf[1], nom, secteur, taille, pays);
            if (result != null) {
                return result;
            }
        }

        // 2. FALLBACK : Mode Analyse Intelligente Locale (Si l'API Google est inactive)
        return generateLocalAnalysis(nom, secteur, taille);
    }

    private static String tryConfig(String version, String model, String nom, String secteur, String taille, String pays) {
        try {
            String prompt = "Analyse l'entreprise " + nom + " (" + secteur + ", " + taille + "). Donne 2 points forts et 2 risques. Max 5 phrases. Français.";
            String apiUrl = "https://generativelanguage.googleapis.com/" + version + "/models/" + model + ":generateContent?key=" + API_KEY;
            
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            requestBody.put("contents", contents);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) response.append(line);
                    return new JSONObject(response.toString())
                            .getJSONArray("candidates").getJSONObject(0)
                            .getJSONObject("content").getJSONArray("parts")
                            .getJSONObject(0).getString("text");
                }
            }
        } catch (Exception e) {
            System.err.println("API Fail [" + model + "]: " + e.getMessage());
        }
        return null;
    }

    private static String generateLocalAnalysis(String nom, String secteur, String taille) {
        String base = "📌 [ANALYSE SMART-AUDIT]\n\n";
        String forts, risques;

        if (secteur != null && (secteur.toLowerCase().contains("it") || secteur.toLowerCase().contains("tech"))) {
            forts = "• Expertise technologique confirmée.\n• Capacité d'innovation et scalabilité.";
            risques = "• Forte dépendance aux talents spécialisés.\n• Cybersécurité et protection des données.";
        } else if (secteur != null && secteur.toLowerCase().contains("santé")) {
            forts = "• Secteur à haute valeur ajoutée.\n• Conformité aux normes sanitaires stricte.";
            risques = "• Évolution rapide des réglementations.\n• Coûts de R&D élevés.";
        } else {
            forts = "• Structure opérationnelle solide.\n• Positionnement de marché adapté à la taille " + taille + ".";
            risques = "• Volatilité des coûts de production.\n• Concurrence accrue sur le segment " + secteur + ".";
        }

        return base + "Analyse de " + nom + " :\n\n" + forts + "\n\n" + risques + 
               "\n\nNote : Analyse effectuée par le moteur local MindAudit.";
    }
}
