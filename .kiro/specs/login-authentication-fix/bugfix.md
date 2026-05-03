# Bugfix Requirements Document

## Introduction

Ce document décrit le bug de connexion dans l'application MindAudit où l'authentification échoue systématiquement avec le message "Mot de passe incorrect" même lorsque l'utilisateur entre les identifiants corrects (admin@mindaudit.com / admin123). Le bug empêche tous les utilisateurs de se connecter à l'application, rendant le système inutilisable.

L'analyse du code révèle que le problème se situe dans la vérification du mot de passe : le hash SHA-256 généré à partir du mot de passe saisi ne correspond pas au hash stocké dans la base de données, probablement en raison d'un problème de format ou d'encodage du hash stocké.

## Bug Analysis

### Current Behavior (Defect)

1.1 WHEN l'utilisateur entre un email valide (admin@mindaudit.com) et le mot de passe correct (admin123) THEN le système affiche "Mot de passe incorrect" et refuse la connexion

1.2 WHEN l'utilisateur entre des identifiants valides et que le hash du mot de passe saisi est comparé au hash stocké dans la base de données THEN la comparaison échoue systématiquement même si le mot de passe est correct

1.3 WHEN l'utilisateur tente de se connecter avec des identifiants corrects THEN le champ mot de passe est effacé mais l'utilisateur reste sur l'écran de connexion sans accès au tableau de bord

### Expected Behavior (Correct)

2.1 WHEN l'utilisateur entre un email valide (admin@mindaudit.com) et le mot de passe correct (admin123) THEN le système SHALL authentifier l'utilisateur avec succès et charger le tableau de bord

2.2 WHEN l'utilisateur entre des identifiants valides et que le hash du mot de passe saisi est comparé au hash stocké dans la base de données THEN la comparaison SHALL réussir et retourner true

2.3 WHEN l'utilisateur se connecte avec succès THEN le système SHALL charger l'interface du tableau de bord et définir l'utilisateur comme currentUser dans AuthenticationService

### Unchanged Behavior (Regression Prevention)

3.1 WHEN l'utilisateur entre un email invalide (format incorrect) THEN le système SHALL CONTINUE TO afficher "Email invalide" et refuser la connexion

3.2 WHEN l'utilisateur entre un email valide mais un mot de passe réellement incorrect THEN le système SHALL CONTINUE TO afficher "Mot de passe incorrect" et refuser la connexion

3.3 WHEN l'utilisateur entre un email qui n'existe pas dans la base de données THEN le système SHALL CONTINUE TO afficher "Utilisateur introuvable" et refuser la connexion

3.4 WHEN l'utilisateur entre les identifiants d'un compte désactivé (actif = false) THEN le système SHALL CONTINUE TO afficher "Le compte est désactivé" et refuser la connexion

3.5 WHEN l'utilisateur laisse les champs email ou mot de passe vides THEN le système SHALL CONTINUE TO afficher "Email et mot de passe requis" et refuser la connexion

3.6 WHEN l'utilisateur entre un mot de passe de moins de 6 caractères THEN le système SHALL CONTINUE TO afficher "Mot de passe invalide (min 6 caractères)" et refuser la connexion

3.7 WHEN la connexion à la base de données échoue THEN le système SHALL CONTINUE TO afficher "Erreur de connexion à la base de données" avec le message d'erreur détaillé
