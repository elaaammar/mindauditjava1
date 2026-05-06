# 🎓 Guide API REST - Pour le Professeur

## 📋 Résumé du Projet

**Fonctionnalité:** Système de réinitialisation de mot de passe via API REST

**Technologies utilisées:**
- ✅ **JAX-RS (Jersey 3.1.3)** - Standard Java pour API REST
- ✅ **Grizzly HTTP Server** - Serveur HTTP léger
- ✅ **Jackson** - Sérialisation JSON
- ✅ **Architecture en couches** - DTO, Service, API

---

## 🏗️ Architecture de l'API

```
┌─────────────────────────────────────────┐
│         Client (Postman/curl)           │
└──────────────┬──────────────────────────┘
               │ HTTP POST (JSON)
               ▼
┌─────────────────────────────────────────┐
│    ForgotPasswordAPI (Controller)       │
│  - @Path("/api/forgot-password")        │
│  - Validation des requêtes              │
│  - Gestion des réponses HTTP            │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│         Services (Business Logic)       │
│  - UserService (CRUD utilisateurs)      │
│  - SmsService (envoi codes)             │
│  - PasswordUtil (hachage BCrypt)        │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│      Base de données MySQL              │
│  - Table: userjava                      │
│  - Champ: password_hash                 │
└─────────────────────────────────────────┘
```

---

## 📁 Structure des Fichiers API

```
src/main/java/com/example/mindjavafx/
├── api/
│   ├── ForgotPasswordAPI.java       ← Endpoints REST
│   ├── ApiServer.java               ← Serveur HTTP
│   ├── CorsFilter.java              ← Filtre CORS
│   └── dto/
│       ├── ForgotPasswordRequest.java
│       ├── VerifyCodeRequest.java
│       ├── ResetPasswordRequest.java
│       └── ApiResponse.java
├── service/
│   ├── UserService.java             ← Logique métier
│   └── SmsService.java
└── util/
    └── PasswordUtil.java            ← Hachage BCrypt
```

---

## 🔄 Flow Complet (3 Étapes)

### Étape 1: Demande de code
```
Client → POST /api/forgot-password/send-code
Body: { "contact": "admin@mindaudit.com" }

API:
1. Valide l'email/téléphone
2. Cherche l'utilisateur en base
3. Génère code 6 chiffres (ex: 123456)
4. Stocke le code en mémoire
5. Envoie le code (console pour dev)

Response: { "success": true, "data": { "code": "123456" } }
```

### Étape 2: Vérification du code
```
Client → POST /api/forgot-password/verify-code
Body: { "contact": "admin@mindaudit.com", "code": "123456" }

API:
1. Récupère le code stocké
2. Compare avec le code saisi
3. Valide ou rejette

Response: { "success": true, "message": "Code vérifié" }
```

### Étape 3: Réinitialisation
```
Client → POST /api/forgot-password/reset
Body: { 
  "contact": "admin@mindaudit.com", 
  "code": "123456",
  "newPassword": "newpass123"
}

API:
1. Vérifie le code une dernière fois
2. Hache le nouveau mot de passe (BCrypt)
3. Met à jour en base de données
4. Nettoie le code temporaire

Response: { "success": true, "message": "Mot de passe réinitialisé" }
```

---

## 🧪 Comment Tester (3 Méthodes)

### Méthode 1: Postman (Recommandé)
1. Importe `MindAudit_API.postman_collection.json`
2. Lance l'application JavaFX
3. Exécute les requêtes dans l'ordre (1→2→3)

### Méthode 2: Script Batch
```bash
# Lance l'app JavaFX d'abord
# Puis double-clique sur:
test-api.bat
```

### Méthode 3: curl (Terminal)
```bash
# Étape 1
curl -X POST http://localhost:8080/api/forgot-password/send-code \
  -H "Content-Type: application/json" \
  -d "{\"contact\":\"admin@mindaudit.com\"}"

# Étape 2 (remplace 123456 par le code reçu)
curl -X POST http://localhost:8080/api/forgot-password/verify-code \
  -H "Content-Type: application/json" \
  -d "{\"contact\":\"admin@mindaudit.com\",\"code\":\"123456\"}"

# Étape 3
curl -X POST http://localhost:8080/api/forgot-password/reset \
  -H "Content-Type: application/json" \
  -d "{\"contact\":\"admin@mindaudit.com\",\"code\":\"123456\",\"newPassword\":\"newpass123\"}"
```

---

## ✅ Points Clés pour l'Évaluation

### 1. Respect des Standards REST
- ✅ Utilisation de JAX-RS (standard Java EE)
- ✅ Méthodes HTTP appropriées (POST)
- ✅ Codes de statut HTTP corrects (200, 400, 404, 500)
- ✅ Format JSON pour requêtes/réponses
- ✅ URLs RESTful (`/api/forgot-password/...`)

### 2. Architecture Propre
- ✅ Séparation des couches (API → Service → DAO)
- ✅ DTOs pour les requêtes/réponses
- ✅ Validation des données
- ✅ Gestion des erreurs

### 3. Sécurité
- ✅ Hachage BCrypt pour les mots de passe
- ✅ Validation des inputs (email, téléphone, mot de passe)
- ✅ Codes de vérification à 6 chiffres
- ✅ PreparedStatement (protection SQL injection)

### 4. Documentation
- ✅ Documentation complète (API_DOCUMENTATION.md)
- ✅ Exemples Postman, curl, JavaScript
- ✅ Collection Postman exportable
- ✅ Commentaires dans le code

---

## 🔍 Vérification Rapide

### 1. L'API démarre-t-elle?
Lance l'app JavaFX, tu dois voir dans la console:
```
========================================
🚀 API REST MindAudit démarrée!
📍 URL: http://localhost:8080/
========================================
```

### 2. Les endpoints répondent-ils?
```bash
curl http://localhost:8080/api/forgot-password/send-code
# Doit retourner une erreur 400 (pas de body)
```

### 3. Le flow complet fonctionne-t-il?
Utilise Postman ou `test-api.bat` pour tester les 3 étapes.

---

## 📊 Endpoints Disponibles

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/forgot-password/send-code` | Envoie le code de vérification |
| POST | `/api/forgot-password/verify-code` | Vérifie le code saisi |
| POST | `/api/forgot-password/reset` | Réinitialise le mot de passe |
| POST | `/api/forgot-password/resend-code` | Renvoie un nouveau code |

---

## 🛠️ Dépendances Maven Ajoutées

```xml
<!-- JAX-RS (Jersey) -->
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-grizzly2-http</artifactId>
    <version>3.1.3</version>
</dependency>

<!-- Injection de dépendances -->
<dependency>
    <groupId>org.glassfish.jersey.inject</groupId>
    <artifactId>jersey-hk2</artifactId>
    <version>3.1.3</version>
</dependency>

<!-- JSON (Jackson) -->
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-jackson</artifactId>
    <version>3.1.3</version>
</dependency>
```

---

## 💡 Améliorations Possibles (Bonus)

1. **Authentification JWT** - Sécuriser les endpoints
2. **Rate Limiting** - Limiter les tentatives
3. **Expiration des codes** - TTL de 5 minutes
4. **Redis** - Stockage distribué des codes
5. **Swagger/OpenAPI** - Documentation interactive
6. **Tests unitaires** - JUnit pour les endpoints
7. **Logs structurés** - SLF4J + Logback
8. **HTTPS** - SSL/TLS en production

---

## 📞 Support

**Fichiers de référence:**
- `API_DOCUMENTATION.md` - Documentation complète
- `GUIDE_API_PROF.md` - Ce guide
- `MindAudit_API.postman_collection.json` - Collection Postman
- `test-api.bat` - Script de test automatique

**Code source:**
- `src/main/java/com/example/mindjavafx/api/` - Tous les fichiers API

---

## ✅ Checklist Finale

- [ ] L'application JavaFX démarre sans erreur
- [ ] L'API démarre automatiquement (message dans console)
- [ ] Les 4 endpoints répondent correctement
- [ ] Le flow complet fonctionne (send → verify → reset)
- [ ] Les codes sont générés et affichés dans la console
- [ ] Le mot de passe est bien mis à jour en base de données
- [ ] La documentation est complète et claire
- [ ] Les exemples Postman/curl fonctionnent

---

**Date:** Avril 2026  
**Projet:** MindAudit - Système de gestion d'audit d'entreprise  
**Fonctionnalité:** API REST pour réinitialisation de mot de passe
