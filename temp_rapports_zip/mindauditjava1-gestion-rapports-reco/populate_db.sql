-- SQL Script to populate AuditAI Database with 10 reports and recommendations
-- Use this in phpMyAdmin if the application cannot initialize the database.

CREATE DATABASE IF NOT EXISTS auditai_db;
USE auditai_db;

-- Drop tables if they are corrupted "in engine"
DROP TABLE IF EXISTS recommandation;
DROP TABLE IF EXISTS rapport_audit;

-- Create tables
CREATE TABLE rapport_audit (
    id VARCHAR(36) PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    auditeur VARCHAR(100) NOT NULL,
    entite_auditee VARCHAR(100) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    description TEXT,
    date_creation DATE NOT NULL,
    date_mise_a_jour DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE recommandation (
    id VARCHAR(36) PRIMARY KEY,
    rapport_id VARCHAR(36) NOT NULL,
    description TEXT NOT NULL,
    priorite VARCHAR(20) NOT NULL,
    resolue BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (rapport_id) REFERENCES rapport_audit(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Populate Data
INSERT INTO rapport_audit (id, titre, auditeur, entite_auditee, statut, description, date_creation, date_mise_a_jour) VALUES
('R1', 'Audit Sécurité SI', 'Ali Ben Salem', 'DSI', 'EN_COURS', 'RAPPORT D\'AUDIT INTERNE N° 2026-07\nObjet : Audit Sécurité SI et Périmètre Réseau\n\n1. SYNTHÈSE RÉSUMÉE\nL\'analyse de la sécurité périmétrique et des accès VPN a mis en lumière une configuration obsolète des pare-feux principaux, permettant potentiellement des intrusions de niveau 3. L\'authentification à double facteur (2FA) n\'est pas généralisée pour les accès distants. L\'application immédiate des correctifs est requise.', CURDATE(), CURDATE()),
('R2', 'Audit Financier Q1', 'Sara Mansour', 'Finance', 'FINALISE', 'RAPPORT D\'AUDIT INTERNE N° 2026-08\nObjet : Audit Financier du 1er Trimestre (Q1)\n\n1. SYNTHÈSE RÉSUMÉE\nLa vérification des processus de facturation manuels a identifié des écarts de rapprochement bancaire s\'élevant à 4,5% du chiffre d\'affaires. Le délai moyen de recouvrement client est passé de 30 à 45 jours. L\'absence d\'un outil de suivi automatisé retarde la clôture comptable.', CURDATE(), CURDATE()),
('R3', 'Audit RH 2024', 'Mohamed Triki', 'Ressources Humaines', 'BROUILLON', 'RAPPORT D\'AUDIT INTERNE N° 2026-09\nObjet : Audit des Processus RH et Dossiers du Personnel\n\n1. SYNTHÈSE RÉSUMÉE\nL\'audit des dossiers du personnel et du stockage physique met en exergue un retard critique dans la digitalisation des archives RH. Plus de 30% des contrats récents n\'ont pas fait l\'objet d\'un archivage électronique sécurisé. Des failles de confidentialité ont été détectées.', CURDATE(), CURDATE()),
('R4', 'Audit Qualité ISO 9001', 'Fatma Riahi', 'Production', 'EN_COURS', 'RAPPORT D\'AUDIT INTERNE N° 2026-10\nObjet : Audit Qualité ISO 9001 sur Ligne de Production\n\n1. SYNTHÈSE RÉSUMÉE\nL\'évaluation de la traçabilité des pièces sur la chaîne d\'assemblage indique une baisse d\'application des procédures ISO 9001. Les numéros de lots sont saisis manuellement, entraînant un taux d\'erreur de 2,3%. Ce manque de traçabilité risque de bloquer la certification.', CURDATE(), CURDATE()),
('R5', 'Audit Performance IT', 'Jean Dupont', 'IT', 'FINALISE', 'RAPPORT D\'AUDIT INTERNE N° 2026-11\nObjet : Audit de Performance IT et Disponibilité Serveurs\n\n1. SYNTHÈSE RÉSUMÉE\nL\'optimisation des temps de réponse des serveurs critiques a été auditée suite à plusieurs plaintes. Le goulet d\'étranglement principal se situe au niveau du cluster de bases de données dont les disques saturent. Les scripts de nettoyage des caches ne sont plus fonctionnels.', CURDATE(), CURDATE()),
('R6', 'Audit Conformité RGPD', 'Sophie Laurent', 'Juridique', 'EN_COURS', 'RAPPORT D\'AUDIT INTERNE N° 2026-12\nObjet : Audit Conformité RGPD et Protection des Données\n\n1. SYNTHÈSE RÉSUMÉE\nLa vérification de la gestion des données personnelles révèle un retard dans la mise en conformité du registre des traitements. Plusieurs flux de données vers des sous-traitants hors UE ne sont pas couverts par des clauses contractuelles types. Le processus de réponse aux droits est lent.', CURDATE(), CURDATE()),
('R7', 'Audit Logistique', 'Marc Durand', 'Entrepôt', 'BROUILLON', 'RAPPORT D\'AUDIT INTERNE N° 2026-13\nObjet : Audit Logistique et Gestion de l\'Entrepôt\n\n1. SYNTHÈSE RÉSUMÉE\nL\'analyse de la gestion des stocks montre une optimisation insuffisante de l\'espace, entraînant des déplacements redondants. Le taux d\'erreur de préparation de commandes s\'élève à 3,5%. La mise en place d\'un système de gestion d\'entrepôt (WMS) moderne est fortement recommandée.', CURDATE(), CURDATE()),
('R8', 'Audit Marketing Digital', 'Kevin Martin', 'Marketing', 'FINALISE', 'RAPPORT D\'AUDIT INTERNE N° 2026-14\nObjet : Audit de l\'Efficacité du Marketing Digital\n\n1. SYNTHÈSE RÉSUMÉE\nL\'évaluation du ROI des campagnes publicitaires souligne une dispersion budgétaire sur des canaux peu performants. Le coût d\'acquisition client a augmenté de 15%. Le tracking des conversions est incomplet, rendant difficile l\'analyse de l\'attribution des ventes.', CURDATE(), CURDATE()),
('R9', 'Audit Maintenance', 'Paul Lefebvre', 'Technique', 'EN_COURS', 'RAPPORT D\'AUDIT INTERNE N° 2026-15\nObjet : Audit du Plan de Maintenance Préventive\n\n1. SYNTHÈSE RÉSUMÉE\nLa vérification du plan de maintenance montre que les interventions sont majoritairement curatives plutôt que préventives. Cette approche entraîne des arrêts de production non planifiés coûteux. L\'absence d\'une GMAO limite la visibilité sur l\'historique des pannes.', CURDATE(), CURDATE()),
('R10', 'Audit Sécurité Physique', 'Eric Petit', 'Sécurité', 'FINALISE', 'RAPPORT D\'AUDIT INTERNE N° 2026-16\nObjet : Audit de Sécurité Physique des Bâtiments\n\n1. SYNTHÈSE RÉSUMÉE\nLe contrôle des accès et de la vidéosurveillance a mis en évidence plusieurs failles de sécurité. Les badges visiteurs ne sont pas systématiquement récupérés, et la couverture des caméras présente des angles morts. Les serrures mécaniques ne répondent plus aux standards.', CURDATE(), CURDATE());

INSERT INTO recommandation (id, rapport_id, description, priorite, resolue) VALUES
(UUID(), 'R1', 'Mettre à jour les pare-feux', 'Haute', 0),
(UUID(), 'R1', 'Activer l\'authentification 2FA', 'Haute', 0),
(UUID(), 'R2', 'Réviser la politique des dépenses', 'Moyenne', 0),
(UUID(), 'R2', 'Automatiser les rapports comptables', 'Faible', 0),
(UUID(), 'R3', 'Mettre à jour les contrats', 'Haute', 0),
(UUID(), 'R3', 'Digitaliser les dossiers', 'Faible', 0),
(UUID(), 'R3', 'Former au RGPD', 'Moyenne', 0),
(UUID(), 'R4', 'Système de traçabilité', 'Haute', 0),
(UUID(), 'R4', 'Revoir contrôle qualité', 'Moyenne', 0),
(UUID(), 'R5', 'Migrer vers le Cloud', 'Moyenne', 0),
(UUID(), 'R5', 'Optimiser les requêtes SQL', 'Haute', 0),
(UUID(), 'R6', 'Nommer un DPO', 'Haute', 0),
(UUID(), 'R6', 'Mettre à jour les mentions légales', 'Faible', 0),
(UUID(), 'R7', 'Installer un WMS', 'Haute', 0),
(UUID(), 'R7', 'Réorganiser le zonage', 'Moyenne', 0),
(UUID(), 'R8', 'Changer d\'agence média', 'Faible', 0),
(UUID(), 'R8', 'Améliorer le tracking', 'Moyenne', 0),
(UUID(), 'R9', 'Remplacer les machines obsolètes', 'Haute', 0),
(UUID(), 'R9', 'Planifier les arrêts techniques', 'Moyenne', 0),
(UUID(), 'R10', 'Ajouter des caméras', 'Moyenne', 0),
(UUID(), 'R10', 'Badgeage biométrique', 'Haute', 0),
(UUID(), 'R10', 'Remplacer les serrures', 'Moyenne', 0);
