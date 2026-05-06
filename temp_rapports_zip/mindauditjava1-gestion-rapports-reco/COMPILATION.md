# 🔨 Compilation du Projet avec l'API REST

## ⚡ Méthode Rapide (IntelliJ IDEA)

### 1️⃣ Recharger Maven
```
Clic droit sur pom.xml → Maven → Reload Project
```
⏱️ Attends 1-2 minutes (téléchargement Jersey)

### 2️⃣ Compiler
```
Build → Build Project (Ctrl+F9)
```

### 3️⃣ Lancer
```
Run → Main (Shift+F10)
```

✅ Tu dois voir:
```
========================================
🚀 API REST MindAudit démarrée!
📍 URL: http://localhost:8080/
========================================
```

---

## 🛠️ Méthode Alternative (Maven CLI)

### Si Maven est dans le PATH

```bash
# Nettoyer
mvn clean

# Compiler
mvn compile

# Lancer
mvn javafx:run
```

### Si Maven n'est PAS dans le PATH

Utilise IntelliJ IDEA (méthode recommandée).

---

## ❌ Erreurs Possibles

### Erreur 1: "Package jakarta.ws.rs does not exist"

**Cause:** Maven n'a pas téléchargé les dépendances

**Solution:**
```
1. Clic droit sur pom.xml
2. Maven → Reload Project
3. Attends la fin du téléchargement
4. Build → Rebuild Project
```

---

### Erreur 2: "Module not found: javafx.controls"

**Cause:** module-info.java manquant ou incorrect

**Solution:**
Vérifie que `src/main/java/module-info.java` contient:
```java
module com.example.mindjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.pdfbox;
    requires jakarta.ws.rs;
    requires jersey.server;
    requires jersey.container.grizzly2.http;
    requires com.fasterxml.jackson.databind;
    
    opens com.example.mindjavafx to javafx.fxml;
    opens com.example.mindjavafx.controller to javafx.fxml;
    opens com.example.mindjavafx.model to javafx.base;
    
    exports com.example.mindjavafx;
    exports com.example.mindjavafx.controller;
    exports com.example.mindjavafx.model;
    exports com.example.mindjavafx.api;
    exports com.example.mindjavafx.api.dto;
}
```

---

### Erreur 3: "Port 8080 already in use"

**Cause:** Un autre programme utilise le port 8080

**Solution 1 (Recommandé):** Arrête l'autre programme

**Solution 2:** Change le port dans `ApiServer.java`:
```java
private static final String BASE_URI = "http://localhost:9000/";
```

---

### Erreur 4: "Cannot find symbol: class ForgotPasswordAPI"

**Cause:** Le package api n'est pas reconnu

**Solution:**
```
1. File → Invalidate Caches → Invalidate and Restart
2. Après redémarrage: Build → Rebuild Project
```

---

## 🔍 Vérifications Après Compilation

### 1. Vérifier les classes compilées

```
target/classes/com/example/mindjavafx/api/
├── ForgotPasswordAPI.class
├── ApiServer.class
├── CorsFilter.class
└── dto/
    ├── ForgotPasswordRequest.class
    ├── VerifyCodeRequest.class
    ├── ResetPasswordRequest.class
    └── ApiResponse.class
```

### 2. Vérifier les dépendances Maven

Dans IntelliJ:
```
View → Tool Windows → Maven
→ Dependencies
→ Cherche "jersey"
```

Tu dois voir:
- jersey-container-grizzly2-http:3.1.3
- jersey-hk2:3.1.3
- jersey-media-json-jackson:3.1.3

### 3. Vérifier que l'API démarre

Lance l'app et regarde la console:
```
[API] Serveur démarré sur http://localhost:8080/
```

---

## 🧪 Test Rapide Après Compilation

```bash
curl http://localhost:8080/api/forgot-password/send-code
```

**Résultat attendu:**
```
HTTP 400 (pas de body)
```

C'est normal! L'API fonctionne mais attend un body JSON.

**Test complet:**
```bash
curl -X POST http://localhost:8080/api/forgot-password/send-code \
  -H "Content-Type: application/json" \
  -d "{\"contact\":\"admin@mindaudit.com\"}"
```

**Résultat attendu:**
```json
{"success":true,"message":"Code envoyé avec succès",...}
```

---

## 📊 Checklist de Compilation

- [ ] Maven rechargé (pom.xml)
- [ ] Dépendances Jersey téléchargées
- [ ] Compilation sans erreur (Build Project)
- [ ] Classes .class générées dans target/
- [ ] Application démarre sans erreur
- [ ] Message "API démarrée" dans console
- [ ] Test curl fonctionne

---

## 🎯 Commandes Utiles IntelliJ

| Action | Raccourci |
|--------|-----------|
| Recharger Maven | Clic droit pom.xml → Reload |
| Compiler | Ctrl+F9 |
| Rebuild | Ctrl+Shift+F9 |
| Lancer | Shift+F10 |
| Arrêter | Ctrl+F2 |
| Clean | Maven → Lifecycle → clean |

---

## 💡 Conseils

### Pour une compilation propre

```
1. Maven → Lifecycle → clean
2. Maven → Reload Project
3. Build → Rebuild Project
4. Run → Main
```

### Si rien ne fonctionne

```
1. File → Invalidate Caches → Invalidate and Restart
2. Après redémarrage:
   - Clic droit pom.xml → Maven → Reload
   - Build → Rebuild Project
   - Run → Main
```

---

## ✅ Compilation Réussie

Si tu vois ce message, c'est bon:

```
========================================
🚀 API REST MindAudit démarrée!
📍 URL: http://localhost:8080/
========================================

📋 Endpoints disponibles:
  POST http://localhost:8080/api/forgot-password/send-code
  POST http://localhost:8080/api/forgot-password/verify-code
  POST http://localhost:8080/api/forgot-password/reset
  POST http://localhost:8080/api/forgot-password/resend-code

💡 Utilisez Postman ou curl pour tester l'API
========================================
```

**Prochaine étape:** Teste avec Postman ou test-api.html

---

## 📞 Support

Si tu as des problèmes de compilation:
1. Vérifie que Java 17 est installé
2. Vérifie que Maven fonctionne dans IntelliJ
3. Regarde les erreurs dans la console
4. Recharge Maven et rebuild

**Fichiers de référence:**
- `pom.xml` - Configuration Maven
- `module-info.java` - Configuration modules Java
- `START_HERE.md` - Guide de démarrage
