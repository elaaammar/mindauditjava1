package com.example.mindjavafx.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Client HTTP simple pour appeler l'API REST
 */
public class ApiClient {
    
    private static final String API_BASE_URL = "http://localhost:9000/api";
    private static final Gson gson = new Gson();
    
    /**
     * Envoie une requête POST à l'API
     */
    public static JsonObject post(String endpoint, Map<String, String> data) {
        HttpURLConnection conn = null;
        try {
            System.out.println("[API Client] POST " + API_BASE_URL + endpoint);
            System.out.println("[API Client] Data: " + gson.toJson(data));
            
            URL url = new URL(API_BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            // Envoyer les données
            String jsonData = gson.toJson(data);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.flush();
            }
            
            // Lire la réponse
            int responseCode = conn.getResponseCode();
            System.out.println("[API Client] Response code: " + responseCode);
            
            BufferedReader in;
            if (responseCode >= 400) {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            }
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            System.out.println("[API Client] Response: " + response.toString());
            
            return gson.fromJson(response.toString(), JsonObject.class);
            
        } catch (Exception e) {
            System.err.println("[API Client] Erreur: " + e.getMessage());
            e.printStackTrace();
            
            // Retourner une erreur JSON
            JsonObject error = new JsonObject();
            error.addProperty("success", false);
            error.addProperty("message", "Erreur de connexion à l'API: " + e.getMessage());
            return error;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    /**
     * Envoie une requête GET à l'API
     */
    public static JsonObject get(String endpoint) {
        try {
            URL url = new URL(API_BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            
            // Lire la réponse
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                    StandardCharsets.UTF_8
                )
            );
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            return gson.fromJson(response.toString(), JsonObject.class);
            
        } catch (Exception e) {
            System.err.println("[API Client] Erreur: " + e.getMessage());
            e.printStackTrace();
            
            // Retourner une erreur JSON
            JsonObject error = new JsonObject();
            error.addProperty("success", false);
            error.addProperty("message", "Erreur de connexion à l'API: " + e.getMessage());
            return error;
        }
    }
}
