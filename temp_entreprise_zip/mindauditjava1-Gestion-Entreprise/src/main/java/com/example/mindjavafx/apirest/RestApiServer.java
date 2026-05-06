package com.example.mindjavafx.apirest;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Serveur API REST simple utilisant HttpServer natif de Java
 * Port: 9000
 */
public class RestApiServer {
    
    private static HttpServer server;
    private static boolean isRunning = false;
    private static Gson gson = new Gson();
    
    // Instances des API
    private static UserAPI userAPI;
    private static StatsAPI statsAPI;
    private static AuthAPI authAPI;
    private static PasswordResetAPI passwordResetAPI;

    public static void start() {
        if (isRunning) {
            System.out.println("[API] Serveur déjà démarré");
            return;
        }

        try {
            server = HttpServer.create(new InetSocketAddress(9000), 0);
            
            // Initialiser les API
            userAPI = new UserAPI();
            statsAPI = new StatsAPI();
            authAPI = new AuthAPI();
            passwordResetAPI = new PasswordResetAPI();
            
            // Routes de test
            server.createContext("/api/ping", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                String response = "{\"success\": true, \"message\": \"API MindAudit fonctionne!\"}";
                sendResponse(exchange, 200, response);
            });
            
            // Routes Authentification
            server.createContext("/api/auth/login", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                if ("POST".equals(exchange.getRequestMethod())) {
                    String body = readRequestBody(exchange);
                    String response = authAPI.login(body);
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Méthode non autorisée\"}");
                }
            });
            
            server.createContext("/api/auth/logout", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                if ("POST".equals(exchange.getRequestMethod())) {
                    String token = exchange.getRequestHeaders().getFirst("Authorization");
                    String response = authAPI.logout(token);
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Méthode non autorisée\"}");
                }
            });
            
            server.createContext("/api/auth/current", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                if ("GET".equals(exchange.getRequestMethod())) {
                    String token = exchange.getRequestHeaders().getFirst("Authorization");
                    String response = authAPI.getCurrentUser(token);
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Méthode non autorisée\"}");
                }
            });
            
            // Routes Utilisateurs
            server.createContext("/api/users", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                if ("GET".equals(exchange.getRequestMethod())) {
                    String response = userAPI.getAllUsers();
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Méthode non autorisée\"}");
                }
            });
            
            // Routes Mot de passe oublié
            server.createContext("/api/password-reset/request", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                if ("POST".equals(exchange.getRequestMethod())) {
                    String body = readRequestBody(exchange);
                    String response = passwordResetAPI.requestCode(body);
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Méthode non autorisée\"}");
                }
            });
            
            server.createContext("/api/password-reset/code", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                if ("GET".equals(exchange.getRequestMethod())) {
                    // Récupérer le contact depuis les paramètres de l'URL
                    String query = exchange.getRequestURI().getQuery();
                    String contact = null;
                    if (query != null && query.contains("contact=")) {
                        contact = query.split("contact=")[1].split("&")[0];
                        contact = java.net.URLDecoder.decode(contact, "UTF-8");
                    }
                    String response = passwordResetAPI.getVerificationCode(contact);
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Méthode non autorisée\"}");
                }
            });
            
            server.createContext("/api/password-reset/verify", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                if ("POST".equals(exchange.getRequestMethod())) {
                    String body = readRequestBody(exchange);
                    String response = passwordResetAPI.verifyCode(body);
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Méthode non autorisée\"}");
                }
            });
            
            server.createContext("/api/password-reset/reset", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                if ("POST".equals(exchange.getRequestMethod())) {
                    String body = readRequestBody(exchange);
                    String response = passwordResetAPI.resetPassword(body);
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Méthode non autorisée\"}");
                }
            });
            
            // Routes Statistiques
            server.createContext("/api/stats/all", exchange -> {
                // Gérer les requêtes OPTIONS (preflight CORS)
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                if ("GET".equals(exchange.getRequestMethod())) {
                    String response = statsAPI.getAllStats();
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Méthode non autorisée\"}");
                }
            });
            
            server.setExecutor(null);
            server.start();
            
            isRunning = true;
            
            System.out.println("========================================");
            System.out.println("🚀 API REST MindAudit démarrée!");
            System.out.println("📍 URL: http://localhost:9000/");
            System.out.println("========================================");
            System.out.println("\n📋 Endpoints disponibles:");
            System.out.println("\n🔐 AUTHENTIFICATION:");
            System.out.println("  POST   http://localhost:9000/api/auth/login");
            System.out.println("  POST   http://localhost:9000/api/auth/logout");
            System.out.println("  GET    http://localhost:9000/api/auth/current");
            System.out.println("\n🔑 MOT DE PASSE OUBLIÉ:");
            System.out.println("  POST   http://localhost:9000/api/password-reset/request");
            System.out.println("  GET    http://localhost:9000/api/password-reset/code?contact=xxx@xxx.com");
            System.out.println("  POST   http://localhost:9000/api/password-reset/verify");
            System.out.println("  POST   http://localhost:9000/api/password-reset/reset");
            System.out.println("\n👥 UTILISATEURS:");
            System.out.println("  GET    http://localhost:9000/api/users");
            System.out.println("\n📊 STATISTIQUES:");
            System.out.println("  GET    http://localhost:9000/api/stats/all");
            System.out.println("\n🧪 TEST:");
            System.out.println("  GET    http://localhost:9000/api/ping");
            System.out.println("\n💡 Utilisez Postman ou curl pour tester l'API");
            System.out.println("========================================\n");
            
        } catch (IOException e) {
            System.err.println("Erreur démarrage API: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (server != null && isRunning) {
            server.stop(0);
            isRunning = false;
            System.out.println("[API] Serveur arrêté");
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }
    
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        // Headers CORS pour permettre l'accès depuis le navigateur
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
