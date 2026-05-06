# Étapes d'installation et de configuration de MindAudit

## 1. Prérequis Système

### Logiciels Requis:
- **Java 17 JDK**: https://adoptium.net/
- **Maven 3.6+**: https://maven.apache.org/download.cgi
- **MySQL Server 5.7+**: https://www.mysql.com/downloads/
- **Git** (optionnel): https://git-scm.com/

### Installation sur Windows:
```bash
# Vérifier les installations
java -version
mvn -version
mysql --version
```

## 2. Configuration MySQL

### Créer la Base de Données:
```bash
# Ouvrir MySQL
mysql -u root -p

# Exécuter le script SQL
source mindaudit.sql;

# Ou copier-coller le contenu du fichier mindaudit.sql
```

### Vérifier la Base de Données:
```sql
USE mindaudit;
SHOW TABLES;
SELECT * FROM userjava;
SELECT * FROM role;
SELECT * FROM permission;
```

## 3. Configuration du Projet

### Modifier la Connexion à la Base de Données:

Fichier: `src/main/java/com/example/mindjavafx/util/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/mindaudit";
private static final String USER = "root";  // Votre utilisateur MySQL
private static final String PASSWORD = "";  // Votre mot de passe MySQL
```

## 4. Compilation et Exécution

### Option 1: Maven CLI
```bash
cd c:\Users\DELL\Desktop\MindJavaFX

# Compiler le projet
mvn clean compile

# Exécuter l'application
mvn javafx:run
```

### Option 2: IDE (IntelliJ/Eclipse)
1. Ouvrir le projet dans l'IDE
2. Clic droit sur pom.xml → Run
3. Vérifier les configurations Maven

## 5. Première Connexion

### Identifiants Disponibles:

**Compte Admin:**
- Email: admin@mindaudit.com
- Mot de passe: admin123

**Compte User:**
- Email: user@mindaudit.com
- Mot de passe: user123

**Compte Auditeur:**
- Email: marie@mindaudit.com
- Mot de passe: marie123

## 6. Fonctionnalités à Tester

### Après Connexion en Admin:
1. ✓ Voir le Tableau de Bord Statistique
2. ✓ Accéder à la Gestion des Utilisateurs
3. ✓ Créer un nouvel utilisateur
4. ✓ Modifier un utilisateur
5. ✓ Supprimer un utilisateur
6. ✓ Rechercher par ID/Nom/Âge
7. ✓ Trier les utilisateurs
8. ✓ Voir le profil utilisateur
9. ✓ Accéder à la Gestion des Rôles et Permissions
10. ✓ Se déconnecter

### Après Connexion en User:
- Les mêmes fonctionnalités, SAUF la Gestion des Rôles et Permissions

## 7. Dépannage Courant

### Erreur: "mysql-connector-java not found"
```bash
# Nettoyer et compiler à nouveau
mvn clean
mvn dependency:resolve
mvn compile
```

### Erreur: "Connexion à la base de données refusée"
1. Vérifier que MySQL est lancé
2. Vérifier les identifiants (USER, PASSWORD)
3. Vérifier que la base existe: `SHOW DATABASES;`

### Erreur: "Unable to load FXML"
1. Vérifier que les fichiers .fxml existent dans `src/main/resources/fxml/`
2. Vérifier les chemins dans les contrôleurs

### Application figée au démarrage
- Vérifier la connexion à la base de données
- Vérifier les logs dans la console

## 8. Structure du Projet Finale

```
MindJavaFX/
├── src/
│   └── main/
│       ├── java/com/example/mindjavafx/
│       │   ├── model/ (entités)
│       │   ├── controller/ (logique UI)
│       │   ├── dao/ (accès données)
│       │   ├── service/ (services métier)
│       │   ├── util/ (utilitaires)
│       │   └── Main.java
│       └── resources/
│           ├── fxml/ (interfaces)
│           └── css/ (styles)
├── pom.xml (dépendances Maven)
├── mindaudit.sql (script BD)
├── config.properties (configuration)
├── README_MINDAUDIT.md (documentation)
└── INSTALLATION.md (ce fichier)
```

## 9. Notes Importantes

- **Sécurité**: Les mots de passe sont hashés en SHA-256
- **Validation**: Tous les champs sont validés côté client
- **Base de Données**: MySQL doit être lancé avant de démarrer l'app
- **Rôles**: Admin > User > Auditeur (niveaux d'accès)
- **Permissions**: Associées aux rôles dans la BD

## 10. Commandes Utiles Maven

```bash
# Nettoyer les fichiers compilés
mvn clean

# Compiler uniquement
mvn compile

# Compiler et empaqueter
mvn package

# Exécuter les tests (si disponibles)
mvn test

# Générer la documentation
mvn site

# Afficher l'arborescence des dépendances
mvn dependency:tree
```

## 11. Support et Resources

- Documentation JavaFX: https://openjfx.io/
- MySQL Documentation: https://dev.mysql.com/doc/
- Maven Guide: https://maven.apache.org/guides/
- Java 17 Docs: https://docs.oracle.com/en/java/javase/17/

---

**Prêt à utiliser! Bonne chance avec MindAudit! 🚀**
