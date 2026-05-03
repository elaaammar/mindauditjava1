# 🚀 SETUP SIMPLE & RAPIDE

## Étape 1️⃣ : Créer la Base de Données (2 minutes)

### Ouvrir le fichier SQL dans MySQL:

#### Option A: MySQL Workbench (Recommandé)
```
1. Ouvrir MySQL Workbench
2. File → Open SQL Script
3. Sélectionner: QUICK_SETUP.sql
4. Cliquer sur ⚡ Execute (ou Ctrl+Shift+Enter)
5. Attendre "Query executed successfully"
```

#### Option B: Terminal/PowerShell
```bash
# Windows
mysql -u root -p < QUICK_SETUP.sql

# Linux/Mac
mysql -u root -p < QUICK_SETUP.sql
```

#### Option C: MySQL CLI Direct
```bash
mysql -u root -p

# Dans MySQL:
source QUICK_SETUP.sql;
```

---

## Étape 2️⃣ : Vérifier que c'est OK

```bash
# Dans MySQL:
USE mindaudit;
SELECT email, nom FROM userjava;
```

Vous devez voir:
```
admin@mindaudit.com | Admin Audit
user@mindaudit.com  | Jean User
```

---

## Étape 3️⃣ : Configurer le Mot de Passe MySQL

**Fichier**: `src/main/java/com/example/mindjavafx/util/DatabaseConnection.java`

Chercher cette ligne:
```java
private static final String PASSWORD = "";
```

Et remplacer par votre mot de passe MySQL (si vous en avez un):
```java
private static final String PASSWORD = "votremotdepasse";  // Si mot de passe
private static final String PASSWORD = "";                 // Si pas de mot de passe
```

---

## Étape 4️⃣: Relancer l'Application

### Dans IntelliJ:
1. **Build** → **Rebuild Project**
2. **Run** → **Run** (ou Shift+F10)

### Via Terminal:
```bash
cd c:\Users\DELL\Desktop\MindJavaFX
mvn clean compile
mvn javafx:run
```

---

## Étape 5️⃣: Tester la Connexion

**Page de Login:**
- Email: `admin@mindaudit.com`
- Mot de passe: `admin123`

**Cliquer**: Se Connecter

✅ Si ça marche, vous allez voir le Tableau de Bord!

---

## 🆘 Si ça ne marche pas:

### ❌ "Email ou mot de passe incorrect"
→ Vérifier que QUICK_SETUP.sql a été exécuté correctement
→ Vérifier que les 2 utilisateurs existent: `SELECT * FROM userjava;`

### ❌ "Connexion refusée"
→ MySQL n'est pas lancé
→ Windows: Services → MySQL (démarrer)
→ Linux: `sudo systemctl start mysql`

### ❌ "Unknown database 'mindaudit'"
→ QUICK_SETUP.sql n'a pas été exécuté
→ Réexécuter le script

### ❌ "Access denied for user 'root'"
→ Le mot de passe MySQL est incorrect dans DatabaseConnection.java
→ Vérifier PASSWORD dans le code

---

## 📝 Identifiants Disponibles

```
Email: admin@mindaudit.com
Mot de passe: admin123
Rôle: Admin (accès total)

---

Email: user@mindaudit.com
Mot de passe: user123
Rôle: User (accès limité)
```

---

## ✅ Checklist Finale

- [ ] QUICK_SETUP.sql exécuté
- [ ] Base `mindaudit` existe
- [ ] 2 utilisateurs en base
- [ ] PASSWORD configuré dans DatabaseConnection.java
- [ ] Application compilée
- [ ] Application lancée
- [ ] Login réussit
- [ ] Tableau de Bord visible

---

**C'est tout! 🎉 MindAudit est prêt à l'emploi!**
