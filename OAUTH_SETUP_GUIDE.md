# 🔐 Guide de Configuration OAuth2 - Google & Facebook

Ce guide explique comment configurer l'authentification OAuth2 pour Google et Facebook dans votre application MindAudit.

---

## 📋 Table des Matières
1. [Configuration Google OAuth2](#1-configuration-google-oauth2)
2. [Configuration Facebook OAuth2](#2-configuration-facebook-oauth2)
3. [Dépendances Maven](#3-dépendances-maven)
4. [Implémentation du Code](#4-implémentation-du-code)

---

## 1. Configuration Google OAuth2

### Étape 1: Créer un Projet Google Cloud

1. Allez sur [Google Cloud Console](https://console.cloud.google.com/)
2. Cliquez sur **"Créer un projet"**
3. Nommez votre projet: `MindAudit-OAuth`
4. Cliquez sur **"Créer"**

### Étape 2: Activer l'API Google+

1. Dans le menu, allez à **"API et services" > "Bibliothèque"**
2. Recherchez **"Google+ API"**
3. Cliquez sur **"Activer"**

### Étape 3: Créer des Identifiants OAuth 2.0

1. Allez à **"API et services" > "Identifiants"**
2. Cliquez sur **"Créer des identifiants" > "ID client OAuth 2.0"**
3. Configurez l'écran de consentement OAuth:
   - Type d'application: **Application de bureau**
   - Nom: `MindAudit Desktop`
4. Ajoutez les URI de redirection autorisés:
   ```
   http://localhost:8080/oauth2callback
   ```
5. Cliquez sur **"Créer"**
6. **IMPORTANT**: Notez votre:
   - **Client ID**: `xxxxx.apps.googleusercontent.com`
   - **Client Secret**: `xxxxx`

### Étape 4: Ajouter les Identifiants dans le Code

Créez un fichier `src/main/resources/oauth-config.properties`:

```properties
# Google OAuth2
google.client.id=VOTRE_CLIENT_ID_ICI
google.client.secret=VOTRE_CLIENT_SECRET_ICI
google.redirect.uri=http://localhost:8080/oauth2callback

# Facebook OAuth2
facebook.app.id=VOTRE_APP_ID_ICI
facebook.app.secret=VOTRE_APP_SECRET_ICI
facebook.redirect.uri=http://localhost:8080/facebook/callback
```

---

## 2. Configuration Facebook OAuth2

### Étape 1: Créer une Application Facebook

1. Allez sur [Facebook Developers](https://developers.facebook.com/)
2. Cliquez sur **"Mes applications" > "Créer une application"**
3. Choisissez **"Consommateur"**
4. Nom de l'application: `MindAudit`
5. Email de contact: votre email
6. Cliquez sur **"Créer l'application"**

### Étape 2: Configurer Facebook Login

1. Dans le tableau de bord, ajoutez le produit **"Facebook Login"**
2. Choisissez **"Web"**
3. Dans **"Paramètres" > "De base"**:
   - Notez votre **App ID**
   - Notez votre **App Secret** (cliquez sur "Afficher")
4. Dans **"Facebook Login" > "Paramètres"**:
   - URI de redirection OAuth valides:
     ```
     http://localhost:8080/facebook/callback
     ```
5. Activez **"Connexion avec Facebook"**

### Étape 3: Passer en Mode Production

1. Dans **"Paramètres" > "De base"**
2. Basculez le commutateur en haut de **"En développement"** à **"En production"**
3. Ajoutez une politique de confidentialité (URL)

---

## 3. Dépendances Maven

Ajoutez ces dépendances dans votre `pom.xml`:

```xml
<!-- Google OAuth2 -->
<dependency>
    <groupId>com.google.api-client</groupId>
    <artifactId>google-api-client</artifactId>
    <version>2.2.0</version>
</dependency>

<dependency>
    <groupId>com.google.oauth-client</groupId>
    <artifactId>google-oauth-client-jetty</artifactId>
    <version>1.34.1</version>
</dependency>

<dependency>
    <groupId>com.google.apis</groupId>
    <artifactId>google-api-services-oauth2</artifactId>
    <version>v2-rev20200213-2.0.0</version>
</dependency>

<!-- Facebook OAuth2 -->
<dependency>
    <groupId>com.restfb</groupId>
    <artifactId>restfb</artifactId>
    <version>2023.11.0</version>
</dependency>

<!-- HTTP Client -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.14</version>
</dependency>
```

Après avoir ajouté les dépendances, exécutez:
```bash
mvn clean install
```

---

## 4. Implémentation du Code

### Créer le Service OAuth

Créez `src/main/java/com/example/mindjavafx/service/OAuthService.java`:

```java
package com.example.mindjavafx.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;

public class OAuthService {
    
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    
    public Userinfo authenticateWithGoogle() throws Exception {
        // Charger les identifiants depuis oauth-config.properties
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
            JSON_FACTORY,
            new InputStreamReader(new FileInputStream("src/main/resources/client_secret.json"))
        );
        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            new NetHttpTransport(),
            JSON_FACTORY,
            clientSecrets,
            Collections.singletonList("https://www.googleapis.com/auth/userinfo.profile")
        ).build();
        
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        
        Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), JSON_FACTORY, credential)
            .setApplicationName("MindAudit")
            .build();
        
        return oauth2.userinfo().get().execute();
    }
    
    public com.restfb.types.User authenticateWithFacebook(String accessToken) {
        com.restfb.FacebookClient facebookClient = new com.restfb.DefaultFacebookClient(
            accessToken,
            com.restfb.Version.LATEST
        );
        
        return facebookClient.fetchObject("me", com.restfb.types.User.class);
    }
}
```

### Mettre à Jour LoginController

Dans `handleGoogleLogin()`:

```java
@FXML
private void handleGoogleLogin() {
    try {
        OAuthService oauthService = new OAuthService();
        Userinfo userInfo = oauthService.authenticateWithGoogle();
        
        // Créer ou récupérer l'utilisateur dans la base de données
        String email = userInfo.getEmail();
        String name = userInfo.getName();
        
        // Vérifier si l'utilisateur existe
        User user = userService.getUserByEmail(email);
        if (user == null) {
            // Créer un nouvel utilisateur
            user = new User(name, email, "google_oauth", 0, new Role(2, "User"));
            userService.addUser(user);
        }
        
        // Connecter l'utilisateur
        authService.loginWithOAuth(user);
        loadDashboard();
        
    } catch (Exception e) {
        errorLabel.setText("Erreur lors de la connexion Google: " + e.getMessage());
        e.printStackTrace();
    }
}
```

---

## 📝 Notes Importantes

### Sécurité
- **NE JAMAIS** commiter les fichiers contenant vos clés API
- Ajoutez `oauth-config.properties` et `client_secret.json` dans `.gitignore`
- Utilisez des variables d'environnement en production

### Test en Local
- Les URI de redirection doivent pointer vers `localhost` pendant le développement
- Changez-les pour votre domaine en production

### Limitations
- Google OAuth nécessite une connexion internet
- Facebook OAuth nécessite que l'application soit en mode "Production" pour les utilisateurs externes

---

## 🚀 Commandes Rapides

```bash
# Installer les dépendances
mvn clean install

# Compiler le projet
mvn compile

# Lancer l'application
mvn javafx:run
```

---

## ❓ Dépannage

### Erreur: "redirect_uri_mismatch"
- Vérifiez que l'URI de redirection dans le code correspond exactement à celle configurée dans Google/Facebook

### Erreur: "invalid_client"
- Vérifiez que votre Client ID et Client Secret sont corrects
- Assurez-vous que le fichier `oauth-config.properties` est bien chargé

### L'application ne se connecte pas
- Vérifiez que les API sont activées dans Google Cloud Console
- Vérifiez que l'application Facebook est en mode "Production"

---

## 📚 Ressources

- [Google OAuth2 Documentation](https://developers.google.com/identity/protocols/oauth2)
- [Facebook Login Documentation](https://developers.facebook.com/docs/facebook-login)
- [RestFB Documentation](https://restfb.com/)

---

**Bon développement! 🎉**
