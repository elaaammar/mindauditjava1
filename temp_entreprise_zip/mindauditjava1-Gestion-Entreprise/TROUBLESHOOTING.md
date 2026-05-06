# GUIDE COMPLET - PROBLÈMES & SOLUTIONS

## 🔴 Le Login ne Marche Pas?

### Cause 1: Base de Données Non Créée

**✅ Solution:**

#### Étape 1: Créer la Base
```bash
# Via MySQL CLI
mysql -u root -p < QUICK_SETUP.sql

# Ou via MySQL Workbench:
# 1. Ouvrir MySQL Workbench
# 2. File → Open SQL Script → QUICK_SETUP.sql
# 3. Cliquer sur ⚡ Execute
```

#### Étape 2: Vérifier les Données
```sql
USE mindaudit;
SELECT * FROM userjava;
SELECT email, nom, password_hash FROM userjava;
```

---

### Cause 2: Mot de Passe MySQL Incorrect

**✅ Solution:**

Modifier le fichier: `src/main/java/com/example/mindjavafx/util/DatabaseConnection.java`

```java
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mindaudit";
    private static final String USER = "root";
    private static final String PASSWORD = "VOTRE_MOT_DE_PASSE_ICI";  // ← MODIFIER
}
```

Si mot de passe vide:
```java
private static final String PASSWORD = "";
```

---

### Cause 3: MySQL Non Lancé

**✅ Solution:**

#### Windows:
```bash
# Vérifier le service
Services → MySQL (ou MySQL80)

# Ou via PowerShell:
Get-Service MySQL80 | Start-Service
```

#### Linux:
```bash
sudo systemctl start mysql
sudo systemctl status mysql
```

#### Mac:
```bash
brew services start mysql
```

---

### Cause 4: Identifiants Incorrects

**✅ Identifiants de Test (après QUICK_SETUP.sql):**

```
Email: admin@mindaudit.com
Mot de passe: admin123
```

Ou:
```
Email: user@mindaudit.com
Mot de passe: user123
```

---

## 🔧 Test de Connexion

### Via Java:
```bash
cd c:\Users\DELL\Desktop\MindJavaFX
mvn clean compile

# Tester la connexion:
java -cp target/classes com.example.mindjavafx.util.DatabaseConnection
```

### Via MySQL CLI:
```bash
# Vérifier la base
mysql -u root -p -e "SHOW DATABASES;"

# Vérifier les utilisateurs
mysql -u root -p -e "USE mindaudit; SELECT COUNT(*) FROM userjava;"
```

---

## 🛠️ Déboguer le Hash du Mot de Passe

Si le mot de passe ne marche pas, le hash peut être incorrect.

### Générer le Hash:
```bash
mvn clean compile
java -cp target/classes com.example.mindjavafx.util.PasswordUtil
```

Entrez le mot de passe et copiez le hash.

### Mettre à jour la Base:
```sql
UPDATE userjava SET password_hash = 'VOTRE_HASH' WHERE email = 'admin@mindaudit.com';
```

---

## 🔐 Hashes des Mots de Passe Test

```
admin123 → a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
user123  → 04f8996da763b7a969b1028ee3007569eaf3a635486f694971d3311271d7cb81
marie123 → e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23dd3f175149
```

---

## 📋 Checklist Complète

- [ ] MySQL est installé et lancé
- [ ] La base `mindaudit` existe
- [ ] La table `userjava` existe
- [ ] Au moins 2 utilisateurs sont présents
- [ ] Les hashes de mot de passe sont corrects
- [ ] DatabaseConnection.java a le bon mot de passe MySQL
- [ ] L'application se lance sans erreur
- [ ] Je peux me connecter avec admin@mindaudit.com / admin123

---

## 🚀 Procédure Complète (Do It From Scratch)

### 1. Arrêter l'Application
Fermer MindAudit complètement

### 2. Créer la Base de Données
```bash
mysql -u root -p < QUICK_SETUP.sql
```

### 3. Vérifier
```sql
mysql -u root -p
USE mindaudit;
SELECT * FROM userjava;
```

Vous devez voir 2 utilisateurs:
- admin@mindaudit.com
- user@mindaudit.com

### 4. Modifier DatabaseConnection.java
```java
private static final String PASSWORD = "MOT_DE_PASSE_ROOT_MYSQL";
```

### 5. Recompiler
```bash
mvn clean compile
```

### 6. Relancer l'Application
IntelliJ → Run

### 7. Tester
- Email: `admin@mindaudit.com`
- Mot de passe: `admin123`

---

## ⚠️ Messages d'Erreur Courants

### "Connection refused"
→ MySQL n'est pas lancé
→ Solution: Vérifiez que le service MySQL fonctionne

### "Unknown database 'mindaudit'"
→ La BD n'existe pas
→ Solution: Exécutez QUICK_SETUP.sql

### "Access denied for user 'root'@'localhost'"
→ Mot de passe incorrect
→ Solution: Modifiez PASSWORD dans DatabaseConnection.java

### "java.sql.SQLException: No suitable driver"
→ Driver MySQL manquant
→ Solution: `mvn dependency:resolve`

---

## 💡 Tips

1. **Toujours tester la connexion MySQL d'abord** avant de lancer l'app
2. **Les mots de passe sont CASE-SENSITIVE** (majuscules/minuscules)
3. **Après modifier DatabaseConnection.java**, recompiler obligatoirement
4. **Vérifier la base MySQL** : `SHOW TABLES;` dans la BD

---

**Besoin d'aide? Suivez cette checklist étape par étape! ✅**
