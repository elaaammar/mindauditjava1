# 🚀 Guide API REST MindAudit

## ✅ CE QUI A ÉTÉ CRÉÉ

### 3 API complètes:
1. **API Authentification** - Login/Logout
2. **API Utilisateurs** - CRUD complet
3. **API Statistiques** - Données agrégées

**Port:** `9000` (différent de JavaFX)

---

## 🎯 DÉMARRAGE

### 1️⃣ Recharge Maven
```
IntelliJ → Clic droit pom.xml → Maven → Reload Project
Attends 2-3 minutes (téléchargement Spark Java + Gson)
```

### 2️⃣ Lance l'application
```
Run → Main.java
```

**Tu verras:**
```
🚀 API REST MindAudit démarrée!
📍 URL: http://localhost:9000/
```

---

## 📋 ENDPOINTS DISPONIBLES

### 🔐 AUTHENTIFICATION

#### POST /api/auth/login
**Connexion**
```json
Request:
{
  "email": "admin@mindaudit.com",
  "password": "admin123"
}

Response:
{
  "success": true,
  "message": "Connexion réussie",
  "data": {
    "user": {...},
    "token": "token_1234567890"
  }
}
```

#### GET /api/auth/current
**Utilisateur connecté**
```
Headers: Authorization: token_1234567890

Response:
{
  "success": true,
  "data": {...}
}
```

#### POST /api/auth/logout
**Déconnexion**
```
Headers: Authorization: token_1234567890
```

---

### 👥 UTILISATEURS

#### GET /api/users
**Liste tous les utilisateurs**
```json
Response:
{
  "success": true,
  "message": "Utilisateurs récupérés",
  "data": [
    {
      "id": 1,
      "nom": "Admin",
      "email": "admin@mindaudit.com",
      "age": 30,
      "role": "Admin",
      "actif": true,
      "telephone": "0612345678"
    }
  ]
}
```

#### GET /api/users/:id
**Un utilisateur par ID**
```
GET /api/users/1
```

#### POST /api/users
**Créer un utilisateur**
```json
Request:
{
  "nom": "Nouveau User",
  "email": "nouveau@example.com",
  "age": 25,
  "actif": true,
  "telephone": "0612345678"
}
```

#### PUT /api/users/:id
**Modifier un utilisateur**
```json
PUT /api/users/1

Request:
{
  "nom": "Nom Modifié",
  "email": "modifie@example.com",
  "age": 26,
  "actif": true,
  "telephone": "0612345678"
}
```

#### DELETE /api/users/:id
**Supprimer un utilisateur**
```
DELETE /api/users/1
```

---

### 📊 STATISTIQUES

#### GET /api/stats/total
**Total utilisateurs**
```json
Response:
{
  "success": true,
  "data": {
    "total": 10
  }
}
```

#### GET /api/stats/active
**Utilisateurs actifs**
```json
Response:
{
  "success": true,
  "data": {
    "active": 8
  }
}
```

#### GET /api/stats/roles
**Distribution par rôle**
```json
Response:
{
  "success": true,
  "data": {
    "Admin": 2,
    "User": 6,
    "Auditeur": 2
  }
}
```

#### GET /api/stats/all
**Toutes les statistiques**
```json
Response:
{
  "success": true,
  "data": {
    "total": 10,
    "active": 8,
    "inactive": 2,
    "roles": {
      "Admin": 2,
      "User": 6,
      "Auditeur": 2
    }
  }
}
```

---

## 🧪 TESTER L'API

### Méthode 1: Postman (Recommandé)
```
1. Ouvre Postman
2. Import → MindAudit_REST_API.postman_collection.json
3. Teste les endpoints
```

### Méthode 2: curl
```bash
# Test ping
curl http://localhost:9000/api/ping

# Liste utilisateurs
curl http://localhost:9000/api/users

# Login
curl -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@mindaudit.com","password":"admin123"}'

# Statistiques
curl http://localhost:9000/api/stats/all
```

### Méthode 3: Navigateur
```
Ouvre dans Chrome:
http://localhost:9000/api/ping
http://localhost:9000/api/users
http://localhost:9000/api/stats/all
```

---

## ✅ AVANTAGES

### Pourquoi ces API?

1. **Séparation des préoccupations**
   - JavaFX = Interface utilisateur
   - API REST = Logique métier accessible

2. **Multi-plateforme**
   - Site web peut utiliser l'API
   - App mobile peut utiliser l'API
   - Autres services peuvent utiliser l'API

3. **Testabilité**
   - Teste avec Postman
   - Pas besoin de l'interface graphique

4. **Standard professionnel**
   - Format JSON
   - Codes HTTP appropriés
   - Architecture REST

---

## 🎓 POUR TON PROF

### Montre-lui:

**1. Lance l'application**
```
Run → Main.java
Console affiche: "API REST MindAudit démarrée!"
```

**2. Ouvre Postman**
```
Teste GET /api/users
Montre la réponse JSON
```

**3. Explique**
```
"J'ai créé 3 API REST:
- Authentification (login/logout)
- Utilisateurs (CRUD complet)
- Statistiques (données agrégées)

L'API utilise Spark Java, format JSON, et suit les standards REST."
```

---

## 📊 RÉSUMÉ

**Fichiers créés:** 8 fichiers
- 3 API Controllers
- 3 DTOs
- 1 Serveur
- 1 Collection Postman

**Endpoints:** 14 endpoints
- 3 Authentification
- 5 Utilisateurs
- 4 Statistiques
- 1 Test
- 1 Ping

**Technologies:**
- Spark Java (serveur HTTP)
- Gson (JSON)
- Port 9000

**Aucun fichier existant modifié!** ✅
(Sauf Main.java pour démarrer l'API)

---

## 🎉 C'EST PRÊT!

Ton application a maintenant des **API REST complètes**!

**Prochaine étape:**
1. Recharge Maven
2. Lance l'application
3. Teste avec Postman
4. Présente au prof

**BONNE CHANCE!** 🚀
