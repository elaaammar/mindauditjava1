# 🚀 API REST MindAudit - COMMENCE ICI

## ⚡ Démarrage Ultra-Rapide (3 étapes)

### 1️⃣ Recharge Maven
```
IntelliJ → Clic droit sur pom.xml → Maven → Reload Project
```
⏱️ Attends 1-2 minutes (téléchargement des dépendances Jersey)

### 2️⃣ Lance l'Application
```
Run → Main.java
```
✅ Tu dois voir dans la console:
```
========================================
🚀 API REST MindAudit démarrée!
📍 URL: http://localhost:8080/
========================================
```

### 3️⃣ Teste l'API

**Option A: Interface Web (Plus Simple)**
```
1. Ouvre test-api.html dans Chrome/Firefox
2. Clique sur "Envoyer le code"
3. Le code s'affiche en grand
4. Clique sur "Vérifier le code"
5. Entre un nouveau mot de passe
6. Clique sur "Réinitialiser"
```

**Option B: Postman (Plus Professionnel)**
```
1. Télécharge Postman: https://www.postman.com/downloads/
2. Import → MindAudit_API.postman_collection.json
3. Exécute les requêtes dans l'ordre (1→2→3)
```

---

## 📚 Documentation

| Fichier | Description |
|---------|-------------|
| **API_README.md** | Démarrage rapide |
| **API_DOCUMENTATION.md** | Documentation complète avec exemples |
| **GUIDE_API_PROF.md** | Guide pour le professeur |
| **PRESENTATION_API.md** | Comment présenter au prof |
| **API_SUMMARY.md** | Résumé de tout ce qui a été fait |

---

## 🎯 Les 4 Endpoints

| Endpoint | Description |
|----------|-------------|
| `POST /api/forgot-password/send-code` | Envoie le code de vérification |
| `POST /api/forgot-password/verify-code` | Vérifie le code |
| `POST /api/forgot-password/reset` | Réinitialise le mot de passe |
| `POST /api/forgot-password/resend-code` | Renvoie un nouveau code |

**URL de base:** `http://localhost:8080/`

---

## 🧪 Test Rapide avec curl

```bash
curl -X POST http://localhost:8080/api/forgot-password/send-code \
  -H "Content-Type: application/json" \
  -d "{\"contact\":\"admin@mindaudit.com\"}"
```

Ou double-clique sur **`test-api.bat`** (Windows)

---

## ✅ Checklist

- [ ] Maven rechargé (dépendances téléchargées)
- [ ] Application lancée (API démarrée)
- [ ] Test avec test-api.html OU Postman
- [ ] Flow complet fonctionne (send → verify → reset)
- [ ] Connexion avec nouveau mot de passe fonctionne

---

## 🎓 Pour le Prof

Lis **`PRESENTATION_API.md`** pour savoir comment présenter l'API.

**Points clés à mentionner:**
- ✅ API REST avec JAX-RS (standard Java)
- ✅ 4 endpoints fonctionnels
- ✅ Format JSON
- ✅ Sécurité (BCrypt, validation)
- ✅ Documentation complète

---

## ❓ Problème?

### L'API ne démarre pas
- Vérifie que Maven a bien rechargé
- Regarde la console pour les erreurs
- Le port 8080 doit être libre

### "Connection refused"
- L'application JavaFX doit être lancée
- L'API démarre automatiquement avec l'app

### Code non reçu
- Regarde la console Java, le code s'affiche là
- Le code est aussi dans la réponse JSON

---

## 🎉 C'est Prêt!

Ton API REST est complète et fonctionnelle.

**Prochaine étape:** Teste avec Postman ou test-api.html

**Pour présenter au prof:** Lis PRESENTATION_API.md
