-- ============================================
-- MindAudit - Script Complet de Configuration
-- ============================================
-- ATTENTION: Ce script supprime et recrée la base!

-- Supprimer la base si elle existe
DROP DATABASE IF EXISTS mindaudit;

-- Créer la base de données
CREATE DATABASE mindaudit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mindaudit;

-- ============================================
-- TABLE ROLE
-- ============================================
CREATE TABLE role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE PERMISSION
-- ============================================
CREATE TABLE permission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE ROLE_PERMISSION
-- ============================================
CREATE TABLE role_permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE USERJAVA
-- ============================================
CREATE TABLE userjava (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    age INT NOT NULL CHECK (age >= 18 AND age <= 100),
    role_id INT,
    actif BOOLEAN DEFAULT TRUE,
    telephone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- INDEX POUR PERFORMANCE
-- ============================================
CREATE INDEX idx_email ON userjava(email);
CREATE INDEX idx_nom ON userjava(nom);
CREATE INDEX idx_age ON userjava(age);
CREATE INDEX idx_role_id ON userjava(role_id);
CREATE INDEX idx_actif ON userjava(actif);

-- ============================================
-- INSÉRER LES RÔLES
-- ============================================
INSERT INTO role (nom, description) VALUES
('Admin', 'Administrateur système avec tous les accès'),
('User', 'Utilisateur standard avec accès limité'),
('Auditeur', 'Auditeur avec accès en lecture sur les rapports');

-- ============================================
-- INSÉRER LES PERMISSIONS
-- ============================================
INSERT INTO permission (nom, description) VALUES
('voir_utilisateurs', 'Voir la liste des utilisateurs'),
('creer_utilisateur', 'Créer un nouvel utilisateur'),
('modifier_utilisateur', 'Modifier les informations d\'un utilisateur'),
('supprimer_utilisateur', 'Supprimer un utilisateur'),
('voir_rapports', 'Voir les rapports d\'audit'),
('creer_rapport', 'Créer un nouveau rapport'),
('gerer_roles', 'Gérer les rôles du système'),
('gerer_permissions', 'Gérer les permissions du système');

-- ============================================
-- ASSOCIER PERMISSIONS AUX RÔLES
-- ============================================
INSERT INTO role_permission (role_id, permission_id) VALUES
-- Admin: Toutes les permissions (1-8)
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
-- User: Voir utilisateurs et rapports (1, 5)
(2, 1), (2, 5),
-- Auditeur: Seulement voir rapports (5)
(3, 5);

-- ============================================
-- INSÉRER LES UTILISATEURS DE TEST
-- ============================================
-- Mots de passe:
--   admin123 (SHA-256): a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
--   user123 (SHA-256):  04f8996da763b7a969b1028ee3007569eaf3a635486f694971d3311271d7cb81
--   marie123 (SHA-256): e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23dd3f175149

INSERT INTO userjava (nom, email, password_hash, age, role_id, actif, telephone) VALUES
('Admin Audit', 'admin@mindaudit.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 35, 1, TRUE, '+33123456789'),
('Jean User', 'user@mindaudit.com', '04f8996da763b7a969b1028ee3007569eaf3a635486f694971d3311271d7cb81', 28, 2, TRUE, '+33123456788'),
('Marie Auditeur', 'marie@mindaudit.com', 'e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23dd3f175149', 32, 3, TRUE, '+33123456787');

-- ============================================
-- VÉRIFICATION
-- ============================================
SELECT 'Utilisateurs:' AS Info;
SELECT id, nom, email, age, role_id, actif FROM userjava;

SELECT 'Rôles:' AS Info;
SELECT * FROM role;

SELECT 'Total utilisateurs:' AS Info;
SELECT COUNT(*) as total FROM userjava;

-- Message de confirmation
SELECT CONCAT('✓ Base de données MindAudit configurée avec succès! ✓') AS Statut;
