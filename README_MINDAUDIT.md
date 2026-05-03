# MindAudit - Système de Gestion d'Audit Interne

Une application JavaFX complète pour la gestion des utilisateurs, rôles et permissions dans un système d'audit interne d'entreprise.

## Fonctionnalités

### 1. **Authentification et Connexion**
- Page de login sécurisée avec validation des données
- Authentification avec email et mot de passe hashé (SHA-256)
- Support de plusieurs rôles : Admin, User, Auditeur

### 2. **Gestion des Utilisateurs**
- Affichage de tous les utilisateurs dans un tableau
- Ajout de nouveaux utilisateurs
- Modification des données utilisateur
- Suppression d'utilisateurs
- Validation complète des données (email, nom, âge, etc.)

### 3. **Recherche et Tri**
- Recherche par ID
- Recherche par Nom
- Recherche par Âge
- Tri par Nom (A-Z / Z-A)
- Tri par Âge (Croissant / Décroissant)

### 4. **Tableau de Bord Statistique**
- Nombre total d'utilisateurs
- Nombre d'utilisateurs actifs
- Nombre d'utilisateurs inactifs

### 5. **Profil Utilisateur**
- Affichage des informations du profil connecté
- Visualisation des détails (nom, email, rôle, âge, téléphone)

### 6. **Gestion des Rôles et Permissions**
- Création de nouveaux rôles
- Attribution de permissions aux rôles
- Gestion des permissions disponibles
- Contrôle d'accès basé sur les rôles (RBAC)

### 7. **Sécurité**
- Validation des données d'entrée
- Hachage des mots de passe
- Contrôle d'accès par rôle
- Messages d'erreur explicites

## Structure du Projet

```
MindJavaFX/
├── src/main/
│   ├── java/com/example/mindjavafx/
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Role.java
│   │   │   └── Permission.java
│   │   ├── controller/
│   │   │   ├── LoginController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── UserManagementController.java
│   │   │   ├── StatisticsController.java
│   │   │   ├── ProfileController.java
│   │   │   └── RolePermissionController.java
│   │   ├── dao/
│   │   │   └── UserDAO.java
│   │   ├── service/
│   │   │   ├── AuthenticationService.java
│   │   │   └── UserService.java
│   │   ├── util/
│   │   │   ├── DatabaseConnection.java
│   │   │   ├── PasswordUtil.java
│   │   │   └── Validation.java
│   │   └── Main.java
│   └── resources/
│       ├── fxml/
│       │   ├── login.fxml
│       │   ├── dashboard.fxml
│       │   ├── user-management.fxml
│       │   ├── statistics.fxml
│       │   ├── profile.fxml
│       │   ├── role-permission.fxml
│       │   └── access-denied.fxml
│       └── css/
│           └── styles.css
├── pom.xml
├── mindaudit.sql
└── README.md
```

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- MySQL 5.7 ou supérieur

## Installation

### 1. Installer les Dépendances

```bash
# Assurez-vous d'avoir installé:
# - Java 17 (JDK) : https://adoptium.net/
# - Maven : https://maven.apache.org/
# - MySQL Server : https://www.mysql.com/downloads/
```

### 2. Créer la Base de Données

Exécutez le script SQL fourni dans `mindaudit.sql`:

```bash
mysql -u root -p < mindaudit.sql
```

Ou via MySQL Workbench/PhpMyAdmin en copiant le contenu du fichier.

### 3. Configurer la Connexion à la Base de Données

Modifiez le fichier `src/main/java/com/example/mindjavafx/util/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/mindaudit";
private static final String USER = "root";
private static final String PASSWORD = "votre_mot_de_passe";
```

### 4. Compiler et Exécuter le Projet

```bash
# Compiler
mvn clean compile

# Exécuter
mvn javafx:run
```

## Identifiants de Test

Après l'exécution du script SQL, vous pouvez utiliser:

### Admin
- **Email:** admin@mindaudit.com
- **Mot de passe:** admin123

### User
- **Email:** user@mindaudit.com
- **Mot de passe:** user123

### Auditeur
- **Email:** marie@mindaudit.com
- **Mot de passe:** marie123

## Validation des Données

### Nom
- Minimum 2 caractères
- Caractères autorisés: lettres, accents, espaces, traits d'union

### Email
- Format valide requis (exemple@domaine.com)

### Mot de Passe
- Minimum 6 caractères
- Hashé en SHA-256 dans la base de données

### Âge
- Entre 18 et 100 ans

### Téléphone
- Format flexible (numéros, tirets, espaces, parenthèses)
- Minimum 8 caractères

## Fonctionnalités Avancées

### Contrôle d'Accès Basé sur les Rôles (RBAC)
- Les administrateurs peuvent accéder à toutes les fonctionnalités
- Les utilisateurs standards voient une version limitée
- Les auditeurs ont accès uniquement aux rapports

### Statistiques
- Mise à jour en temps réel
- Visualisation en cartes colorées

### Recherche Avancée
- Recherche instantanée
- Tri dynamique
- Sélection dans le tableau pour pré-remplir le formulaire

## Sécurité

- **Hachage des Mots de Passe:** SHA-256
- **Validation d'Entrée:** Vérification de chaque champ
- **Contrôle d'Accès:** Basé sur les rôles de l'utilisateur
- **Préparation SQL:** Utilisation de PreparedStatements pour éviter les injections SQL

## Dépannage

### Erreur de Connexion à la Base de Données
1. Vérifiez que MySQL est en cours d'exécution
2. Vérifiez les identifiants dans DatabaseConnection.java
3. Vérifiez que la base de données `mindaudit` est créée

### Erreur: "mvn: command not found"
- Installez Maven: https://maven.apache.org/download.cgi
- Ajoutez Maven au PATH du système

### Erreur: "javafx: not found"
- Vérifiez que Java 17 est installé
- Vérifiez la configuration JavaFX dans pom.xml

## À Venir

- [ ] Export des données en PDF/Excel
- [ ] Notifications par email
- [ ] Dashboard graphique avancé
- [ ] Audit logs pour tracer les actions
- [ ] Gestion des sessions
- [ ] API REST

## Licence

MIT License

## Auteur

MindAudit Development Team

## Support

Pour toute question ou problème, veuillez ouvrir une issue sur GitHub ou contacter l'équipe de développement.
