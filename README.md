# MindAudit — JavaFX Desktop Application

**MindAudit** est une solution complète de gestion d'audit de sécurité, composée d'une application de bureau JavaFX puissante et d'un tableau de bord web Symfony. Ce projet concerne la partie **Client Desktop (JavaFX)**.

## 🚀 Fonctionnalités Clés

- **Gestion des Utilisateurs** : Authentification sécurisée et gestion des profils synchronisée avec Symfony.
- **Audits de Sécurité** : Création et suivi d'audits techniques avec calcul de scores.
- **Gestion des Entreprises** : Module complet de gestion des partenaires et clients.
- **Gestion Documentaire** : Upload et suivi de documents (ISO, Juridiques, Techniques) avec analyse IA.
- **Réclamations** : Système de tickets pour le support et le suivi des incidents.
- **IA Intégrée** : Chatbot intelligent pour aider les utilisateurs dans la navigation et l'audit.

## 🛠 Architecture & Technologies

- **Langage** : Java 17+
- **Interface** : JavaFX 17 (FXML, CSS moderne)
- **Base de Données** : MySQL (via XAMPP) — Partagée avec Symfony.
- **Gestionnaire de dépendances** : Maven
- **Synchronisation** : Accès direct à la base de données unifiée `mindaudit_java`.

## 📋 Prérequis

- **Java JDK 17** ou supérieur.
- **Maven** installé.
- **XAMPP** (ou un serveur MySQL local) en cours d'exécution.
- La base de données `mindaudit_java` doit être créée (gérée automatiquement au premier lancement).

## ⚙️ Configuration

L'application utilise une base de données MySQL locale. Vérifiez les paramètres de connexion dans :
- `src/main/java/com/example/mindjavafx/util/DatabaseConnection.java`
- `src/main/java/com/gestion_audit/util/DBConnection.java`

**Paramètres par défaut :**
- **Hôte** : `localhost:3306`
- **Base de données** : `mindaudit_java`
- **Utilisateur** : `root`
- **Mot de passe** : (vide)

## 🚀 Lancement

1.  **Cloner le projet**
2.  **Lancer MySQL via XAMPP**
3.  **Compiler et lancer avec Maven** :
    ```bash
    mvn clean javafx:run
    ```

## 🔐 Accès par défaut (Test)

Si vous n'avez pas encore d'utilisateur, lancez la classe `FixPasswordHash.java` pour créer un administrateur par défaut :
- **Email** : `admin@mindaudit.com`
- **Mot de passe** : `admin123`

## 🤝 Synchronisation avec Symfony

Ce projet est conçu pour fonctionner en binôme avec le backend Symfony **MindAudit-Web**. Toute modification effectuée ici est immédiatement reflétée sur le tableau de bord administrateur web grâce à l'unification du schéma de données (`utilisateur`, `entreprise`, `document`, etc.).

---
*Développé dans le cadre du projet MindAudit.*
