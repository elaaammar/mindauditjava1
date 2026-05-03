# 🧪 Exemple de Test Complet - API REST

## 📋 Scénario de Test

**Objectif:** Réinitialiser le mot de passe de l'utilisateur `admin@mindaudit.com`

**Nouveau mot de passe:** `nouveauPass123`

---

## 🔄 Flow Complet (4 Requêtes)

### 1️⃣ Envoyer le Code de Vérification

**Requête:**
```http
POST http://localhost:8080/api/forgot-password/send-code
Content-Type: application/json

{
  "contact": "admin@mindaudit.com"
}
```

**Réponse Attendue (200 OK):**
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

**Console Java:**
```
[API] Code généré pour admin@mindaudit.com: 123456
[SMS] Code de vérification: 123456
```

**✅ Vérifications:**
- [ ] Status code = 200
- [ ] success = true
- [ ] code présent dans data
- [ ] code affiché dans console

---

### 2️⃣ Vérifier le Code

**Requête:**
```http
POST http://localhost:8080/api/forgot-password/verify-code
Content-Type: application/json

{
  "contact": "admin@mindaudit.com",
  "code": "123456"
}
```

**Réponse Attendue (200 OK):**
```json
{
  "success": true,
  "message": "Code vérifié avec succès",
  "data": null
}
```

**✅ Vérifications:**
- [ ] Status code = 200
- [ ] success = true
- [ ] message = "Code vérifié avec succès"

---

### 3️⃣ Réinitialiser le Mot de Passe

**Requête:**
```http
POST http://localhost:8080/api/forgot-password/reset
Content-Type: application/json

{
  "contact": "admin@mindaudit.com",
  "code": "123456",
  "newPassword": "nouveauPass123"
}
```

**Réponse Attendue (200 OK):**
```json
{
  "success": true,
  "message": "Mot de passe réinitialisé avec succès",
  "data": null
}
```

**Console Java:**
```
[API] Mot de passe réinitialisé pour: admin@mindaudit.com
```

**✅ Vérifications:**
- [ ] Status code = 200
- [ ] success = true
- [ ] Message de confirmation dans console

---

### 4️⃣ Vérifier la Connexion avec le Nouveau Mot de Passe

**Action:**
1. Ouvre l'application JavaFX
2. Va sur la page de connexion
3. Entre:
   - Email: `admin@mindaudit.com`
   - Mot de passe: `nouveauPass123`
4. Clique sur "Se connecter"

**Résultat Attendu:**
- ✅ Connexion réussie
- ✅ Redirection vers le dashboard
- ✅ Pas de message d'erreur

---

## 🧪 Tests d'Erreur

### Test 1: Email Invalide

**Requête:**
```http
POST http://localhost:8080/api/forgot-password/send-code
Content-Type: application/json

{
  "contact": "emailinvalide"
}
```

**Réponse Attendue (400 Bad Request):**
```json
{
  "success": false,
  "message": "Email invalide",
  "data": null
}
```

---

### Test 2: Utilisateur Inexistant

**Requête:**
```http
POST http://localhost:8080/api/forgot-password/send-code
Content-Type: application/json

{
  "contact": "inexistant@example.com"
}
```

**Réponse Attendue (404 Not Found):**
```json
{
  "success": false,
  "message": "Aucun compte trouvé",
  "data": null
}
```

---

### Test 3: Code Incorrect

**Requête:**
```http
POST http://localhost:8080/api/forgot-password/verify-code
Content-Type: application/json

{
  "contact": "admin@mindaudit.com",
  "code": "999999"
}
```

**Réponse Attendue (400 Bad Request):**
```json
{
  "success": false,
  "message": "Code incorrect",
  "data": null
}
```

---

### Test 4: Mot de Passe Trop Court

**Requête:**
```http
POST http://localhost:8080/api/forgot-password/reset
Content-Type: application/json

{
  "contact": "admin@mindaudit.com",
  "code": "123456",
  "newPassword": "123"
}
```

**Réponse Attendue (400 Bad Request):**
```json
{
  "success": false,
  "message": "Le mot de passe doit contenir au moins 6 caractères",
  "data": null
}
```

---

### Test 5: Code Expiré (après reset)

**Requête:**
```http
POST http://localhost:8080/api/forgot-password/verify-code
Content-Type: application/json

{
  "contact": "admin@mindaudit.com",
  "code": "123456"
}
```

**Réponse Attendue (400 Bad Request):**
```json
{
  "success": false,
  "message": "Aucun code trouvé pour ce contact",
  "data": null
}
```

**Explication:** Le code est supprimé après un reset réussi.

---

## 📊 Tableau Récapitulatif

| Test | Endpoint | Status | Résultat Attendu |
|------|----------|--------|------------------|
| 1. Envoyer code | /send-code | 200 | Code généré et envoyé |
| 2. Vérifier code | /verify-code | 200 | Code vérifié |
| 3. Réinitialiser | /reset | 200 | Mot de passe changé |
| 4. Connexion | (JavaFX) | - | Connexion réussie |
| 5. Email invalide | /send-code | 400 | Erreur validation |
| 6. User inexistant | /send-code | 404 | Compte non trouvé |
| 7. Code incorrect | /verify-code | 400 | Code invalide |
| 8. MDP trop court | /reset | 400 | Validation échouée |
| 9. Code expiré | /verify-code | 400 | Code non trouvé |

---

## 🔍 Vérifications Base de Données

### Avant le Reset

```sql
SELECT id, nom, email, password_hash 
FROM userjava 
WHERE email = 'admin@mindaudit.com';
```

**Résultat:**
```
id: 1
nom: Admin
email: admin@mindaudit.com
password_hash: $2a$12$oldHashValue...
```

### Après le Reset

```sql
SELECT id, nom, email, password_hash 
FROM userjava 
WHERE email = 'admin@mindaudit.com';
```

**Résultat:**
```
id: 1
nom: Admin
email: admin@mindaudit.com
password_hash: $2a$12$newHashValue...  ← CHANGÉ!
```

**✅ Le hash doit être différent!**

---

## 🧪 Test avec curl (Ligne de Commande)

### Flow Complet

```bash
# 1. Envoyer le code
curl -X POST http://localhost:8080/api/forgot-password/send-code \
  -H "Content-Type: application/json" \
  -d '{"contact":"admin@mindaudit.com"}'

# Résultat: {"success":true,"message":"Code envoyé avec succès","data":{"code":"123456",...}}

# 2. Vérifier le code (remplace 123456 par le code reçu)
curl -X POST http://localhost:8080/api/forgot-password/verify-code \
  -H "Content-Type: application/json" \
  -d '{"contact":"admin@mindaudit.com","code":"123456"}'

# Résultat: {"success":true,"message":"Code vérifié avec succès"}

# 3. Réinitialiser
curl -X POST http://localhost:8080/api/forgot-password/reset \
  -H "Content-Type: application/json" \
  -d '{"contact":"admin@mindaudit.com","code":"123456","newPassword":"nouveauPass123"}'

# Résultat: {"success":true,"message":"Mot de passe réinitialisé avec succès"}
```

---

## 🎯 Checklist de Test Complète

### Préparation
- [ ] Application JavaFX lancée
- [ ] API démarrée (message dans console)
- [ ] Postman ouvert avec collection importée
- [ ] Base de données accessible

### Tests Positifs
- [ ] Envoyer code avec email valide → 200
- [ ] Envoyer code avec téléphone valide → 200
- [ ] Vérifier code correct → 200
- [ ] Réinitialiser avec code valide → 200
- [ ] Connexion avec nouveau mot de passe → Succès
- [ ] Renvoyer un nouveau code → 200

### Tests Négatifs
- [ ] Email invalide → 400
- [ ] Téléphone invalide → 400
- [ ] Utilisateur inexistant → 404
- [ ] Code incorrect → 400
- [ ] Mot de passe trop court → 400
- [ ] Code expiré/supprimé → 400
- [ ] Contact vide → 400
- [ ] Code vide → 400

### Vérifications Techniques
- [ ] Hash BCrypt dans la base de données
- [ ] Code affiché dans console
- [ ] Codes HTTP appropriés
- [ ] Format JSON correct
- [ ] CORS headers présents
- [ ] Logs dans la console

---

## 📈 Résultats Attendus

**Tests Positifs:** 6/6 ✅  
**Tests Négatifs:** 8/8 ✅  
**Vérifications Techniques:** 6/6 ✅  

**TOTAL: 20/20 ✅**

---

## 🎉 Conclusion

Si tous les tests passent, ton API REST est:
- ✅ Fonctionnelle
- ✅ Sécurisée
- ✅ Robuste
- ✅ Prête pour la démonstration

**Prochaine étape:** Présente au professeur avec PRESENTATION_API.md
