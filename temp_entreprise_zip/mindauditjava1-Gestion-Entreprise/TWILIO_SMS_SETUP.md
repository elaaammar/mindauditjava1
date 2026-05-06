# 📱 Guide de Configuration Twilio SMS

Ce guide explique comment configurer Twilio pour envoyer des SMS de vérification dans votre application MindAudit.

---

## 🎯 Pourquoi Twilio?

- ✅ **Gratuit pour commencer** - 15$ de crédit gratuit
- ✅ **Facile à utiliser** - API simple
- ✅ **Fiable** - Utilisé par Uber, Airbnb, Netflix
- ✅ **Support France** - Envoi de SMS vers numéros français

---

## 📋 Étape 1: Créer un Compte Twilio

1. Allez sur [https://www.twilio.com/try-twilio](https://www.twilio.com/try-twilio)
2. Cliquez sur **"Start your free trial"**
3. Remplissez le formulaire:
   - Prénom et Nom
   - Email
   - Mot de passe
4. Vérifiez votre email
5. Vérifiez votre numéro de téléphone (vous recevrez un SMS)

---

## 📋 Étape 2: Obtenir vos Identifiants

1. Une fois connecté, vous êtes sur le **Dashboard**
2. Notez ces 3 informations importantes:

### Account SID
```
Exemple: ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```
C'est votre identifiant de compte (commence par "AC")

### Auth Token
```
Exemple: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```
Cliquez sur "Show" pour le voir

### Numéro de Téléphone Twilio
Vous devez en obtenir un gratuitement:

1. Dans le menu, allez à **"Phone Numbers" > "Manage" > "Buy a number"**
2. Sélectionnez **"Country: France"** ou **"United States"** (gratuit)
3. Cochez **"SMS"** dans les capacités
4. Cliquez sur **"Search"**
5. Choisissez un numéro et cliquez sur **"Buy"**
6. Confirmez (c'est gratuit avec le crédit d'essai)

Votre numéro sera au format: **+33XXXXXXXXX** (France) ou **+1XXXXXXXXXX** (USA)

---

## 📋 Étape 3: Configurer l'Application

### 3.1 Ajouter la Dépendance Maven

Ouvrez `pom.xml` et ajoutez:

```xml
<!-- Twilio SMS -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.14.1</version>
</dependency>
```

Puis exécutez:
```bash
mvn clean install
```

### 3.2 Configurer les Identifiants

Ouvrez `src/main/resources/sms-config.properties` et remplacez:

```properties
# Vos identifiants Twilio
twilio.account.sid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
twilio.auth.token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
twilio.phone.number=+33XXXXXXXXX

# EXEMPLE RÉEL:
# twilio.account.sid=AC1234567890abcdef1234567890abcd
# twilio.auth.token=abcdef1234567890abcdef1234567890
# twilio.phone.number=+33612345678
```

⚠️ **IMPORTANT**: Ajoutez ce fichier dans `.gitignore` pour ne pas exposer vos clés!

```
# .gitignore
src/main/resources/sms-config.properties
```

---

## 📋 Étape 4: Ajouter des Numéros Vérifiés (Mode Test)

En mode gratuit, vous ne pouvez envoyer des SMS qu'à des numéros **vérifiés**.

1. Allez dans **"Phone Numbers" > "Manage" > "Verified Caller IDs"**
2. Cliquez sur **"Add a new Caller ID"**
3. Entrez votre numéro de téléphone au format international: **+33612345678**
4. Vous recevrez un code par SMS
5. Entrez le code pour vérifier

Maintenant vous pouvez recevoir des SMS sur ce numéro!

---

## 📋 Étape 5: Tester l'Envoi de SMS

### Test dans l'Application

1. Lancez votre application MindAudit
2. Cliquez sur **"Mot de passe oublié?"**
3. Entrez votre numéro vérifié: `0612345678` ou `+33612345678`
4. Cliquez sur **"Envoyer le code"**
5. **Vous devriez recevoir un SMS avec le code!** 📱

### Vérifier dans la Console Twilio

1. Allez dans **"Monitor" > "Logs" > "Messaging"**
2. Vous verrez tous les SMS envoyés avec leur statut:
   - ✅ **Delivered** = SMS reçu
   - ⏳ **Sent** = En cours d'envoi
   - ❌ **Failed** = Échec (vérifiez le numéro)

---

## 🔧 Dépannage

### Erreur: "The number +33XXXXXXXXX is unverified"

**Solution**: Ajoutez le numéro dans "Verified Caller IDs" (voir Étape 4)

### Erreur: "Unable to create record: Invalid 'To' Phone Number"

**Solution**: Vérifiez le format du numéro:
- ✅ Bon: `+33612345678`
- ❌ Mauvais: `0612345678` (sera converti automatiquement)
- ❌ Mauvais: `06 12 34 56 78` (sera nettoyé automatiquement)

### Le SMS n'arrive pas

1. Vérifiez que le numéro est vérifié dans Twilio
2. Vérifiez les logs dans Twilio Console
3. Vérifiez que vous avez du crédit (15$ gratuit au départ)
4. Attendez 1-2 minutes (parfois il y a un délai)

### Erreur: "Authentication Error"

**Solution**: Vérifiez que votre `Account SID` et `Auth Token` sont corrects dans `sms-config.properties`

---

## 💰 Tarification

### Mode Gratuit (Trial)
- ✅ 15$ de crédit gratuit
- ✅ ~500 SMS gratuits
- ⚠️ Seulement vers numéros vérifiés
- ⚠️ Message "Sent from a Twilio trial account" ajouté

### Mode Payant
- 💵 ~0.03$ par SMS (France)
- ✅ Envoi vers tous les numéros
- ✅ Pas de message "trial"
- ✅ Numéro de téléphone: ~1$/mois

---

## 🔒 Sécurité

### Protéger vos Clés API

1. **Ne jamais commiter** `sms-config.properties` dans Git
2. Ajoutez dans `.gitignore`:
   ```
   src/main/resources/sms-config.properties
   ```
3. En production, utilisez des **variables d'environnement**:
   ```java
   String accountSid = System.getenv("TWILIO_ACCOUNT_SID");
   String authToken = System.getenv("TWILIO_AUTH_TOKEN");
   ```

### Limiter les Abus

Dans `SmsService.java`, ajoutez:
- ⏱️ Limite de temps entre envois (ex: 1 SMS par minute)
- 🔢 Limite d'essais (ex: 3 codes max par jour)
- 📊 Logs de tous les envois

---

## 📚 Ressources

- [Documentation Twilio](https://www.twilio.com/docs/sms)
- [Console Twilio](https://www.twilio.com/console)
- [Tarifs SMS](https://www.twilio.com/sms/pricing)
- [Support Twilio](https://support.twilio.com/)

---

## ✅ Checklist Finale

- [ ] Compte Twilio créé
- [ ] Account SID copié
- [ ] Auth Token copié
- [ ] Numéro Twilio acheté (gratuit)
- [ ] Numéro de téléphone vérifié
- [ ] Dépendance Maven ajoutée
- [ ] `sms-config.properties` configuré
- [ ] `.gitignore` mis à jour
- [ ] Test d'envoi réussi

---

**Bon développement! 📱🚀**
