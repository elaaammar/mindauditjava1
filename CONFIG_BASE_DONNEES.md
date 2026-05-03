# Configuration Base de Données - ProBLÈME DE MOT DE PASSE

## 🔴 Problème:
Le login ne marche pas avec les identifiants de test

## ✅ Solution:

### Option 1: Exécution du Script SQL (Recommandé)

#### Sur Windows:
1. **Double-cliquer sur**: `setup-db.bat`
2. **Entrer le mot de passe MySQL root** (ou laisser vide si pas de mot de passe)
3. **Attendre la confirmation**

#### Sur Linux/Mac:
```bash
chmod +x setup-db.sh
./setup-db.sh
```

### Option 2: Manuel via MySQL Workbench

1. **Ouvrir MySQL Workbench**
2. **Ouvrir le fichier**: `setup-database.sql`
3. **Cliquer sur**: ⚡ Execute (ou Ctrl+Shift+Enter)
4. **Vérifier les résultats**

### Option 3: CLI MySQL Direct

```bash
mysql -u root -p < setup-database.sql
```

## 📋 Ce que le Script Fait:

✓ Supprime l'ancienne base de données (si elle existe)  
✓ Crée une nouvelle base `mindaudit`  
✓ Crée les tables (role, permission, userjava, etc.)  
✓ Insère les utilisateurs de test avec les bons mots de passe  
✓ Crée les index pour la performance  

## 🔐 Mots de Passe Hachés (SHA-256):

```
admin123 → a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
user123  → 04f8996da763b7a969b1028ee3007569eaf3a635486f694971d3311271d7cb81
marie123 → e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23dd3f175149
```

## ✔️ Vérification Après Configuration:

```bash
# Vérifier que la base existe
mysql -u root -p -e "SHOW DATABASES LIKE 'mindaudit';"

# Vérifier les utilisateurs
mysql -u root -p -e "USE mindaudit; SELECT email, nom, role_id FROM userjava;"

# Vérifier les rôles
mysql -u root -p -e "USE mindaudit; SELECT * FROM role;"
```

## 🧪 Test dans l'Appli:

Après avoir exécuté le script SQL:

1. **Lancer MindAudit**
2. **Essayer les identifiants:**
   - Email: `admin@mindaudit.com`
   - Mot de passe: `admin123`

Ou:
   - Email: `user@mindaudit.com`
   - Mot de passe: `user123`

## 🐛 Si ça ne marche toujours pas:

### Vérifier la Connexion MySQL:

```bash
# Tester la connexion
mysql -u root -p -e "SELECT VERSION();"

# Si ça marche, vérifier la base
mysql -u root -p -e "SHOW DATABASES;"

# Si mindaudit existe:
mysql -u root -p -e "USE mindaudit; SHOW TABLES;"
```

### Vérifier la Configuration Java:

Fichier: `src/main/java/com/example/mindjavafx/util/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/mindaudit";
private static final String USER = "root";
private static final String PASSWORD = "";  // Votre mot de passe MySQL
```

**IMPORTANT**: Assurez-vous que le mot de passe Java correspond à votre mot de passe MySQL!

### Si MySQL n'est pas sur localhost:

Modifier l'URL dans DatabaseConnection.java:
```java
// Pour remote:
private static final String URL = "jdbc:mysql://IP_DU_SERVEUR:3306/mindaudit";
```

## 📝 Logs de Débogage:

Si vous avez une erreur "Connexion refusée":

1. **Vérifier que MySQL est lancé:**
   ```bash
   # Windows: Services → MySQL
   # Linux: sudo systemctl status mysql
   # Mac: brew services list
   ```

2. **Vérifier le port:**
   ```bash
   netstat -an | grep 3306
   ```

3. **Vérifier les identifiants dans DatabaseConnection.java**

4. **Vérifier que le driver MySQL est dans le PATH:**
   ```bash
   mvn dependency:tree | grep mysql
   ```

## 🔄 Réinitialiser la Base Complètement:

Si vous voulez recommencer zéro:

```bash
# Supprimer la base
mysql -u root -p -e "DROP DATABASE IF EXISTS mindaudit;"

# Puis re-exécuter le script
mysql -u root -p < setup-database.sql
```

---

**Une fois le script exécuté, MindAudit devrait fonctionner correctement! ✨**
