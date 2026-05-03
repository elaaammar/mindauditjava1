# 📋 Récapitulatif des Changements - Google OAuth

## ✅ Fichiers Modifiés

### 1. `src/main/java/com/example/mindjavafx/service/GoogleOAuthService.java`

**Changements:**
- ✅ `REDIRECT_URI` changé de `urn:ietf:wg:oauth:2.0:oob` → `http://localhost:8080/callback`
- ✅ Méthode `authenticate()` complètement réécrite:
  - Démarre un serveur local sur port 8080
  - Ouvre le navigateur pour l'authentification Google
  - Attend le callback de Google
  - Extrait le code d'autorisation
  - Envoie une page HTML de confirmation au navigateur
  - Échange le code contre un access token
  - Récupère les informations utilisateur
  - Retourne Map<String, String> avec email, name, picture

- ✅ Nouvelle méthode `exchangeCodeForToken(String authorizationCode)`:
  - Envoie une requête POST à `https://oauth2.googleapis.com/token`
  - Paramètres: code, client_id, client_secret, redirect_uri, grant_type
  - Retourne l'access token

- ✅ Nouvelle méthode `getUserInfo(String accessToken)`:
  - Envoie une requête GET à `https://www.googleapis.com/oauth2/v2/userinfo`
  - Header: `Authorization: Bearer {accessToken}`
  - Parse la réponse JSON
  - Retourne Map avec email, name, picture

**Avant:**
```java
public static Map<String, String> authenticate() {
    // Ouvrait juste le navigateur
    // Retournait null
    return null;
}
```

**Après:**
```java
public static Map<String, String> authenticate() {
    // Flux OAuth complet
    // Serveur local + callback + token exchange + user info
    return userInfo; // Map avec email, name, picture
}
```

---

### 2. `src/main/java/com/example/mindjavafx/controller/LoginController.java`

**Changements:**
- ✅ Méthode `handleGoogleLogin()` complètement réécrite

**Avant:**
```java
@FXML
private void handleGoogleLogin() {
    errorLabel.setText("❌ Google OAuth nécessite une configuration Web");
}
```

**Après:**
```java
@FXML
private void handleGoogleLogin() {
    // 1. Affiche message "Connexion en cours..."
    // 2. Appelle GoogleOAuthService.authenticate()
    // 3. Récupère email et name
    // 4. Vérifie si user existe dans DB
    // 5. Si existe → loginWithOAuth() et loadDashboard()
    // 6. Si n'existe pas:
    //    - Crée Role(2, "User")
    //    - Crée User avec constructeur correct
    //    - Appelle userService.addUser()
    //    - loginWithOAuth() et loadDashboard()
    // 7. Gestion des erreurs avec messages appropriés
}
```

**Corrections importantes:**
- ✅ Utilise `userService.addUser()` au lieu de `createUser()` (qui n'existe pas)
- ✅ Utilise `authService.loginWithOAuth()` au lieu de `setCurrentUser()` (qui n'existe pas)
- ✅ Crée `Role` avec `new Role(2, "User")` (constructeur correct)
- ✅ Crée `User` avec `new User(name, email, passwordHash, age, role)` (constructeur correct)

---

## 📁 Fichiers Créés

### 1. `GOOGLE_OAUTH_SETUP.md`
Guide complet avec:
- Instructions détaillées pour créer le client OAuth Web
- Étapes de configuration sur Google Cloud Console
- Comment ajouter les URI de redirection
- Comment ajouter les utilisateurs test
- Dépannage des erreurs courantes

### 2. `GOOGLE_OAUTH_QUICK_GUIDE.txt`
Guide rapide avec:
- Résumé des changements
- Actions requises
- Étapes de configuration en 10 points
- Explication du flux OAuth

### 3. `CHANGEMENTS_GOOGLE_OAUTH.md` (ce fichier)
Récapitulatif technique de tous les changements

---

## 🔧 Configuration Requise

### ⚠️ IMPORTANT: Créer un Client OAuth Web

**Pourquoi?**
- Le client actuel est de type "Desktop" (Ordinateur de bureau)
- Le code utilise `http://localhost:8080/callback` comme redirect_uri
- Google n'autorise PAS les clients Desktop à utiliser des redirect_uri localhost
- Solution: Créer un nouveau client de type "Application Web"

**Étapes:**
1. Google Cloud Console → APIs et services → Identifiants
2. "+ CRÉER DES IDENTIFIANTS" → "ID client OAuth"
3. Type: **"Application Web"** (PAS Desktop!)
4. URI de redirection: `http://localhost:8080/callback`
5. Copier le nouveau Client ID et Client Secret
6. Mettre à jour `GoogleOAuthService.java` lignes 25-26

---

## 🎯 Flux OAuth Complet

```
┌─────────────────────────────────────────────────────────────┐
│ 1. User clique "Continuer avec Google"                     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. Application démarre serveur local sur port 8080         │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. Navigateur s'ouvre avec URL Google OAuth                │
│    https://accounts.google.com/o/oauth2/v2/auth?...        │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. User se connecte avec son compte Google                 │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. Google redirige vers http://localhost:8080/callback     │
│    avec le code d'autorisation                             │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. Application reçoit le callback et extrait le code       │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. Application envoie page HTML "Authentification réussie" │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 8. Application échange code contre access token            │
│    POST https://oauth2.googleapis.com/token                │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 9. Application récupère infos utilisateur                  │
│    GET https://www.googleapis.com/oauth2/v2/userinfo       │
│    Authorization: Bearer {access_token}                    │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 10. Vérification si user existe dans DB                    │
│     - Si oui → loginWithOAuth()                            │
│     - Si non → addUser() puis loginWithOAuth()             │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 11. Redirection vers Dashboard                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🧪 Test

### Avant de tester:
1. ✅ Créer le client OAuth Web sur Google Cloud Console
2. ✅ Mettre à jour Client ID et Client Secret dans `GoogleOAuthService.java`
3. ✅ Ajouter votre email dans les utilisateurs test
4. ✅ Compiler le projet: `./compile.bat`

### Test:
1. Lancer l'application
2. Cliquer sur "Continuer avec Google"
3. Vérifier que le navigateur s'ouvre
4. Se connecter avec votre compte Google
5. Vérifier la page "Authentification réussie"
6. Vérifier que l'application ouvre le dashboard

### Logs à surveiller:
```
[Google OAuth] Serveur local démarré sur le port 8080
[Google OAuth] Navigateur ouvert pour l'authentification
[Google OAuth] En attente du callback...
[Google OAuth] Code d'autorisation reçu: 4/0AeanS...
[Google OAuth] Access token obtenu
[Google OAuth] Informations utilisateur récupérées
[Google Login] Email: votre.email@gmail.com
[Google Login] Nom: Votre Nom
[Google Login] Utilisateur existant trouvé (ou Création d'un nouvel utilisateur)
```

---

## 🐛 Dépannage

### Erreur: "redirect_uri_mismatch"
**Cause:** Client OAuth de type Desktop au lieu de Web
**Solution:** Créer un nouveau client de type "Application Web"

### Erreur: "access_denied"
**Cause:** Email pas dans les utilisateurs test
**Solution:** Ajouter l'email dans l'écran de consentement OAuth

### Erreur: "Port 8080 already in use"
**Cause:** Une autre application utilise le port 8080
**Solution:** Fermer l'autre application ou changer le port dans le code

### Erreur: "Échec de l'authentification Google"
**Cause:** Plusieurs possibilités
**Solution:** Vérifier les logs dans la console pour voir l'erreur exacte

---

## ✅ Checklist Finale

- [ ] Client OAuth Web créé sur Google Cloud Console
- [ ] URI de redirection `http://localhost:8080/callback` ajouté
- [ ] Client ID et Client Secret copiés
- [ ] `GoogleOAuthService.java` mis à jour avec les nouveaux identifiants
- [ ] Email ajouté dans les utilisateurs test
- [ ] Projet compilé sans erreurs
- [ ] Test effectué avec succès

---

**Tout est prêt! Il ne reste plus qu'à configurer le client OAuth Web sur Google Cloud Console.** 🚀
