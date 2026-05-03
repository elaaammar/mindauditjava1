package com.example.mindjavafx.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service d'authentification Facebook OAuth 2.0
 */
public class FacebookOAuthService {
    
    // Identifiants Facebook OAuth - MindAudit App
    private static final String APP_ID = "1071983706004510";
    private static final String APP_SECRET = "571e512ecf9abf4277d39d0829ecb49b";
    private static final String REDIRECT_URI = "http://localhost:8082/";
    private static final String AUTH_URL = "https://www.facebook.com/v18.0/dialog/oauth";
    private static final String TOKEN_URL = "https://graph.facebook.com/v18.0/oauth/access_token";
    private static final String USERINFO_URL = "https://graph.facebook.com/me";
    
    private static final Gson gson = new Gson();
    
    /**
     * Démarre le processus d'authentification Facebook
     * Retourne les informations de l'utilisateur Facebook
     */
    public static Map<String, String> authenticate(String emailHint) {
        ServerSocket serverSocket = null;
        try {
            // 1. Démarrer un serveur local pour recevoir le callback
            serverSocket = new ServerSocket(8082);
            System.out.println("[Facebook OAuth] Serveur local démarré sur le port 8082");
            
            // 2. Ouvrir le navigateur pour l'authentification Facebook
            String authorizationUrl = buildAuthorizationUrl(emailHint);
            Desktop.getDesktop().browse(new URI(authorizationUrl));
            System.out.println("[Facebook OAuth] Navigateur ouvert pour l'authentification");
            
            // 3. Attendre le callback de Facebook
            System.out.println("[Facebook OAuth] En attente du callback...");
            Socket socket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // 4. Lire la requête HTTP
            String line = in.readLine();
            String authorizationCode = null;
            
            if (line != null && line.startsWith("GET")) {
                String[] parts = line.split(" ");
                if (parts.length > 1) {
                    String path = parts[1];
                    if (path.contains("code=")) {
                        authorizationCode = path.substring(path.indexOf("code=") + 5);
                        if (authorizationCode.contains("&")) {
                            authorizationCode = authorizationCode.substring(0, authorizationCode.indexOf("&"));
                        }
                        // Décoder l'URL pour enlever les %2F, %3A, etc.
                        authorizationCode = java.net.URLDecoder.decode(authorizationCode, "UTF-8");
                    }
                }
            }
            
            // 5. Envoyer une réponse au navigateur
            OutputStream out = socket.getOutputStream();
            String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/html\r\n" +
                            "\r\n" +
                            "<html><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                            "<h1 style='color: #1877f2;'>✅ Authentification Facebook réussie!</h1>" +
                            "<script>setTimeout(function(){ window.close(); }, 100);</script>" +
                            "</body></html>";
            out.write(response.getBytes());
            out.flush();
            socket.close();
            
            if (authorizationCode == null) {
                System.err.println("[Facebook OAuth] Code d'autorisation non reçu");
                return null;
            }
            
            System.out.println("[Facebook OAuth] Code d'autorisation reçu: " + authorizationCode.substring(0, 10) + "...");
            
            // 6. Échanger le code contre un access token
            String accessToken = exchangeCodeForToken(authorizationCode);
            if (accessToken == null) {
                System.err.println("[Facebook OAuth] Échec de l'obtention du token");
                return null;
            }
            
            System.out.println("[Facebook OAuth] Access token obtenu");
            
            // 7. Récupérer les informations de l'utilisateur
            Map<String, String> userInfo = getUserInfo(accessToken);
            System.out.println("[Facebook OAuth] Informations utilisateur récupérées");
            
            return userInfo;
            
        } catch (Exception e) {
            System.err.println("[Facebook OAuth] Erreur: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (Exception e) {
                    // Ignorer
                }
            }
        }
    }
    
    /**
     * Construit l'URL d'autorisation Facebook
     */
    private static String buildAuthorizationUrl(String emailHint) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", APP_ID);
        params.put("redirect_uri", REDIRECT_URI);
        params.put("response_type", "code");
        params.put("scope", "public_profile");
        
        StringBuilder url = new StringBuilder(AUTH_URL + "?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(entry.getKey()).append("=")
               .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
               .append("&");
        }
        
        return url.toString();
    }
    
    /**
     * Échange le code d'autorisation contre un access token
     */
    private static String exchangeCodeForToken(String authorizationCode) throws Exception {
        URL url = new URL(TOKEN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        
        // Construire les paramètres
        String params = "code=" + URLEncoder.encode(authorizationCode, "UTF-8") +
                       "&client_id=" + URLEncoder.encode(APP_ID, "UTF-8") +
                       "&client_secret=" + URLEncoder.encode(APP_SECRET, "UTF-8") +
                       "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8");
        
        // Envoyer la requête
        OutputStream os = conn.getOutputStream();
        os.write(params.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
        
        // Lire la réponse
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            System.err.println("[Facebook OAuth] Erreur lors de l'échange du code: " + responseCode);
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println(errorLine);
            }
            return null;
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        // Parser la réponse JSON
        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
        return jsonResponse.get("access_token").getAsString();
    }
    
    /**
     * Récupère les informations de l'utilisateur avec l'access token
     */
    private static Map<String, String> getUserInfo(String accessToken) throws Exception {
        String urlString = USERINFO_URL + "?fields=id,name,email,picture&access_token=" + accessToken;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        // Lire la réponse
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        // Parser la réponse JSON
        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
        
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", jsonResponse.has("email") ? jsonResponse.get("email").getAsString() : "");
        userInfo.put("name", jsonResponse.get("name").getAsString());
        userInfo.put("id", jsonResponse.get("id").getAsString());
        if (jsonResponse.has("picture") && jsonResponse.get("picture").isJsonObject()) {
            JsonObject picture = jsonResponse.getAsJsonObject("picture");
            if (picture.has("data") && picture.getAsJsonObject("data").has("url")) {
                userInfo.put("picture", picture.getAsJsonObject("data").get("url").getAsString());
            }
        }
        
        return userInfo;
    }
}