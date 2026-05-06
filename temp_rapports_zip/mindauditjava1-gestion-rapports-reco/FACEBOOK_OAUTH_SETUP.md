# 🔵 Configuration Facebook OAuth - Instructions Complètes

## ⚠️ IMPORTANT: Configuration Requise

Pour que le bouton "Continuer avec Facebook" fonctionne, tu dois créer une application Facebook et obtenir les identifiants.

---

## 📋 Étapes de Configuration

### 1️⃣ Créer une Application Facebook

1. **Va sur Facebook Developers:**
   - URL: https://developers.facebook.com/
   - Connecte-toi avec ton compte Facebook

2. **Créer une nouvelle app:**
   - Clique sur **"Créer une app"**
   - Choisir **"Consommateur"** ou **"Autre"**
   - Nom de l'app: **"MindAudit"**
   - Email de contact: ton email
   - Clique **"Créer l'app"**

### 2️⃣ Configurer Facebook Login

1. **Ajouter Facebook Login:**
   - Dans le tableau de bord de ton app
   - Clique sur **"+ Ajouter un produit"**
   - Trouve **"Facebook Login"** et clique **"Configurer"**

2. **Configurer les paramètres:**
   - Va dans **"Facebook Login" → "Paramètres"**
   - **URI de redirection OAuth valides:** `http://localhost:8082/callback`
   - **Domaines d'app:** `localhost`
   - Clique **"Enregistrer les modifications"**

### 3️⃣ Récupérer les Identifiants

1. **Va dans "Paramètres" → "De base"**
2. **Copie ces valeurs:**
   - ✅ **ID de l'app**: `1234567890123456`
   - ✅ **Clé secrète de l'app**: Clique sur "Afficher" et copie

### 4️⃣ Mettre à Jour le Code

Ouvre le fichier: `src/main/java/com/example/mindjavafx/service/FacebookOAuthService.java`

Remplace les lignes 18-19:

```java
private static final String APP_ID = "TON_FACEBOOK_APP_ID";
private static final String APP_SECRET = "TON_FACEBOOK_APP_SECRET";
```

### 5️⃣ Configurer l'App en Mode Développement

1. **Dans le tableau de bord Facebook:**
   - Va dans **"Rôles" → "Testeurs"**
   - Ajoute ton compte Facebook comme testeur
   - Ou va dans **"Paramètres de l'app" → "De base"**
   - Change le statut de **"En développement"** à **"En ligne"** (plus tard)

---

## 🚀 Test de l'Authentification

Une fois la configuration terminée:

1. **Recompile** le projet:
   ```bash
   ./compile.bat
   ```

2. **Lance** l'application

3. **Écris un email** dans le champ

4. **Clique sur "Continuer avec Facebook"**

5. **Ce qui va se passer:**
   - ✅ Navigateur s'ouvre avec la page Facebook
   - ✅ Tu te connectes avec ton compte Facebook
   - ✅ Facebook te redirige vers `http://localhost:8082/callback`
   - ✅ Tu verras "✅ Authentification Facebook réussie!"
   - ✅ L'application récupère ton nom et email Facebook
   - ✅ Si tu n'existes pas dans la DB → création automatique
   - ✅ Dashboard s'ouvre avec ton profil

---

## 🔍 Dépannage

### Erreur: "URL de redirection non autorisée"
- ❌ Tu n'as pas ajouté `http://localhost:8082/callback` dans les URI valides
- ✅ Solution: Ajoute l'URI dans Facebook Login → Paramètres

### Erreur: "App en mode développement"
- ❌ L'app Facebook est en mode développement et ton compte n'est pas testeur
- ✅ Solution: Ajoute ton compte comme testeur ou mets l'app en ligne

### Erreur: "Invalid App ID"
- ❌ L'APP_ID dans le code ne correspond pas à ton app Facebook
- ✅ Solution: Vérifie et corrige l'APP_ID dans `FacebookOAuthService.java`

### Le navigateur ne s'ouvre pas
- ❌ Problème avec `java.awt.Desktop`
- ✅ Solution: Vérifiez que `requires java.desktop;` est dans `module-info.java` (déjà fait)

---

## 📝 Différences avec Google OAuth

### ✅ Avantages Facebook:
- Plus simple à configurer (pas besoin d'écran de consentement)
- Pas besoin d'utilisateurs test en mode développement
- Interface plus familière pour les utilisateurs

### ⚠️ Limitations Facebook:
- Certains utilisateurs n'ont pas d'email public sur Facebook
- Politique de confidentialité plus stricte
- Nécessite une révision pour les apps en production

---

## 💡 Notes Importantes

- **Port différent:** Facebook utilise le port **8082** (Google utilise 8081)
- **Email optionnel:** Si l'utilisateur n'a pas d'email public, on utilise `{facebook_id}@facebook.local`
- **Permissions:** On demande seulement `email` et `public_profile`
- **Mode développement:** Fonctionne seulement avec les comptes testeurs jusqu'à ce que l'app soit approuvée

---

## 🎯 Prochaines Étapes

**VOUS DEVEZ MAINTENANT:**
1. Créer l'app Facebook sur developers.facebook.com
2. Configurer Facebook Login avec l'URI de redirection
3. Copier l'APP_ID et APP_SECRET
4. Les mettre dans `FacebookOAuthService.java`
5. Recompiler et tester

---

**Besoin d'aide?** Suivez les étapes ci-dessus dans l'ordre et tout fonctionnera! 🚀

**Note:** Facebook OAuth est plus simple que Google OAuth - pas besoin d'écran de consentement complexe!