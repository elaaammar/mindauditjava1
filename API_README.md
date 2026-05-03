# 🚀 API REST MindAudit - Démarrage Rapide

## ⚡ Démarrage en 3 Étapes

### 1️⃣ Recharge Maven dans IntelliJ
```
Clic droit sur pom.xml → Maven → Reload Project
```
Attends que les dépendances Jersey se téléchargent.

### 2️⃣ Lance l'application JavaFX
```
Run → Main.java
```
Tu verras dans la console:
```
========================================
🚀 API REST MindAudit démarrée!
📍 URL: http://localhost:8080/
========================================
```

### 3️⃣ Teste l'API avec Postman

**Option A: Importer la collection**
1. Ouvre Postman
2. Import → `MindAudit_API.postman_collection.json`
3. Exécute les requêtes dans l'ordre

**Option B: Test manuel**
1. Nouvelle requête POST
2. URL: `http://localhost:8080/api/forgot-password/send-code`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "contact": "admin@mindaudit.com"
}
```
5. Send → Note le code dans la réponse

---

## 📋 Les 4 Endpoints

| Endpoint | Description |
|----------|-------------|
| `POST /api/forgot-password/send-code` | Envoie le code |
| `POST /api/forgot-password/verify-code` | Vérifie le code |
| `POST /api/forgot-password/reset` | Réinitialise le mot de passe |
| `POST /api/forgot-password/resend-code` | Renvoie un nouveau code |

---

## 🧪 Test Rapide avec curl

```bash
curl -X POST http://localhost:8080/api/forgot-password/send-code \
  -H "Content-Type: application/json" \
  -d "{\"contact\":\"admin@mindaudit.com\"}"
```

Ou double-clique sur `test-api.bat` (Windows)

---

## 📚 Documentation Complète

- **`API_DOCUMENTATION.md`** - Documentation détaillée avec exemples
- **`GUIDE_API_PROF.md`** - Guide pour le professeur
- **`MindAudit_API.postman_collection.json`** - Collection Postman

---

## ❓ Problèmes?

### L'API ne démarre pas
- Vérifie que Maven a bien rechargé les dépendances
- Regarde la console pour les erreurs
- Le port 8080 doit être libre

### "Connection refused"
- L'application JavaFX doit être lancée
- L'API démarre automatiquement avec l'app

### Code non reçu
- Regarde la console Java, le code s'affiche là
- Le code est aussi dans la réponse JSON (champ `data.code`)

---

## ✅ C'est Prêt!

L'API est maintenant fonctionnelle. Teste avec Postman ou curl.

**URL de base:** `http://localhost:8080/`
