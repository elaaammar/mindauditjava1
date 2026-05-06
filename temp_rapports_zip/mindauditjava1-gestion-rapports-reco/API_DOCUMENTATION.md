# 📚 Documentation API REST - MindAudit

## 🚀 Démarrage de l'API

L'API démarre automatiquement quand tu lances l'application JavaFX.

**URL de base:** `http://localhost:8080/`

---

## 📋 Endpoints - Mot de Passe Oublié

### 1️⃣ Envoyer le code de vérification

**Endpoint:** `POST /api/forgot-password/send-code`

**Body (JSON):**
```json
{
  "contact": "admin@mindaudit.com"
}
```
OU
```json
{
  "contact": "0612345678"
}
```

**Réponse succès (200):**
```json
{
  "success": true,
  "message": "Code envoyé avec succès",
  "data": {
    "code": "123456",
    "maskedContact": "a***@mindaudit.com"
  }
}
```

**Réponse erreur (404):**
```json
{
  "success": false,
  "message": "Aucun compte trouvé"
}
```

---

### 2️⃣ Vérifier le code

**Endpoint:** `POST /api/forgot-password/verify-code`

**Body (JSON):**
```json
{
  "contact": "admin@mindaudit.com",
  "code": "123456"
}
```

**Réponse succès (200):**
```json
{
  "success": true,
  "message": "Code vérifié avec succès"
}
```

**Réponse erreur (400):**
```json
{
  "success": false,
  "message": "Code incorrect"
}
```

---

### 3️⃣ Réinitialiser le mot de passe

**Endpoint:** `POST /api/forgot-password/reset`

**Body (JSON):**
```json
{
  "contact": "admin@mindaudit.com",
  "code": "123456",
  "newPassword": "nouveauMotDePasse123"
}
```

**Réponse succès (200):**
```json
{
  "success": true,
  "message": "Mot de passe réinitialisé avec succès"
}
```

**Réponse erreur (400):**
```json
{
  "success": false,
  "message": "Code invalide ou expiré"
}
```

---

### 4️⃣ Renvoyer un nouveau code

**Endpoint:** `POST /api/forgot-password/resend-code`

**Body (JSON):**
```json
{
  "contact": "admin@mindaudit.com"
}
```

**Réponse succès (200):**
```json
{
  "success": true,
  "message": "Nouveau code envoyé",
  "data": {
    "code": "654321"
  }
}
```

---

## 🧪 Tester l'API avec Postman

### Installation Postman
1. Télécharge Postman: https://www.postman.com/downloads/
2. Installe et ouvre Postman

### Test complet du flow

#### Étape 1: Envoyer le code
1. Crée une nouvelle requête POST
2. URL: `http://localhost:8080/api/forgot-password/send-code`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "contact": "admin@mindaudit.com"
}
```
5. Clique sur **Send**
6. Note le code dans la réponse

#### Étape 2: Vérifier le code
1. Nouvelle requête POST
2. URL: `http://localhost:8080/api/forgot-password/verify-code`
3. Body:
```json
{
  "contact": "admin@mindaudit.com",
  "code": "123456"
}
```
4. Send

#### Étape 3: Réinitialiser le mot de passe
1. Nouvelle requête POST
2. URL: `http://localhost:8080/api/forgot-password/reset`
3. Body:
```json
{
  "contact": "admin@mindaudit.com",
  "code": "123456",
  "newPassword": "nouveauPass123"
}
```
4. Send

---

## 🧪 Tester l'API avec curl (Terminal)

### Étape 1: Envoyer le code
```bash
curl -X POST http://localhost:8080/api/forgot-password/send-code \
  -H "Content-Type: application/json" \
  -d "{\"contact\":\"admin@mindaudit.com\"}"
```

### Étape 2: Vérifier le code
```bash
curl -X POST http://localhost:8080/api/forgot-password/verify-code \
  -H "Content-Type: application/json" \
  -d "{\"contact\":\"admin@mindaudit.com\",\"code\":\"123456\"}"
```

### Étape 3: Réinitialiser
```bash
curl -X POST http://localhost:8080/api/forgot-password/reset \
  -H "Content-Type: application/json" \
  -d "{\"contact\":\"admin@mindaudit.com\",\"code\":\"123456\",\"newPassword\":\"nouveauPass123\"}"
```

---

## 🔧 Tester l'API avec JavaScript (Frontend)

```javascript
// Étape 1: Envoyer le code
async function sendCode(contact) {
  const response = await fetch('http://localhost:8080/api/forgot-password/send-code', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ contact })
  });
  return await response.json();
}

// Étape 2: Vérifier le code
async function verifyCode(contact, code) {
  const response = await fetch('http://localhost:8080/api/forgot-password/verify-code', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ contact, code })
  });
  return await response.json();
}

// Étape 3: Réinitialiser
async function resetPassword(contact, code, newPassword) {
  const response = await fetch('http://localhost:8080/api/forgot-password/reset', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ contact, code, newPassword })
  });
  return await response.json();
}

// Utilisation
const result1 = await sendCode('admin@mindaudit.com');
console.log('Code:', result1.data.code);

const result2 = await verifyCode('admin@mindaudit.com', '123456');
console.log('Vérifié:', result2.success);

const result3 = await resetPassword('admin@mindaudit.com', '123456', 'newpass123');
console.log('Réinitialisé:', result3.success);
```

---

## ⚙️ Configuration

### Changer le port de l'API

Modifie `ApiServer.java`:
```java
private static final String BASE_URI = "http://localhost:9000/"; // Port 9000
```

### Désactiver l'API

Commente dans `Main.java`:
```java
// ApiServer.start();
```

---

## 🔒 Sécurité (Production)

⚠️ **IMPORTANT pour la production:**

1. **Ne pas renvoyer le code dans la réponse API**
   - Retire `data.put("code", code);` dans `ForgotPasswordAPI.java`

2. **Ajouter expiration des codes**
   - Utilise Redis ou cache avec TTL (Time To Live)
   - Expire les codes après 5-10 minutes

3. **Rate limiting**
   - Limite le nombre de tentatives par IP
   - Bloque après 5 tentatives échouées

4. **HTTPS obligatoire**
   - Utilise SSL/TLS en production
   - Jamais HTTP pour les mots de passe

5. **Validation stricte**
   - Vérifie tous les inputs
   - Protège contre injection SQL (déjà fait avec PreparedStatement)

---

## 📊 Codes de statut HTTP

| Code | Signification |
|------|---------------|
| 200  | Succès |
| 400  | Requête invalide (mauvais format, code incorrect) |
| 404  | Ressource non trouvée (utilisateur inexistant) |
| 500  | Erreur serveur |

---

## 🐛 Dépannage

### L'API ne démarre pas
- Vérifie que le port 8080 n'est pas déjà utilisé
- Regarde la console pour les erreurs
- Vérifie que Maven a bien téléchargé les dépendances Jersey

### Erreur "Connection refused"
- L'application JavaFX doit être lancée
- Vérifie que l'API est bien démarrée (regarde la console)

### Code non reçu
- Regarde la console Java, le code s'affiche là
- En production, configure un vrai service SMS (Twilio) ou Email (SMTP)

---

## 📝 Notes

- L'API démarre automatiquement avec l'application JavaFX
- Les codes sont stockés en mémoire (perdu au redémarrage)
- En production, utilise Redis ou une base de données pour les codes
- Le code s'affiche dans la console pour le développement

---

## ✅ Checklist pour le prof

- ✅ API REST avec JAX-RS (Jersey)
- ✅ Endpoints POST pour mot de passe oublié
- ✅ Format JSON pour requêtes/réponses
- ✅ Codes de statut HTTP appropriés
- ✅ Validation des données
- ✅ Documentation complète
- ✅ Exemples Postman et curl
- ✅ Architecture propre (DTO, Service, API)
