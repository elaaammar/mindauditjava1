-- ============================================
-- CONFIGURATION COMPLETE MINDAUDIT
-- Exécutez ce script dans MySQL/Workbench
-- ============================================

-- ÉTAPE 1: Supprimer ancienne base (optionnel)
DROP DATABASE IF EXISTS mindaudit;

-- ÉTAPE 2: Créer base de données
CREATE DATABASE mindaudit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mindaudit;

-- ÉTAPE 3: Créer table Role
CREATE TABLE role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ÉTAPE 4: Créer table Permission
CREATE TABLE permission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ÉTAPE 5: Créer table Role_Permission
CREATE TABLE role_permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ÉTAPE 6: Créer table UserJava
CREATE TABLE userjava (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    role_id INT,
    actif BOOLEAN DEFAULT TRUE,
    telephone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ÉTAPE 7: Insérer Rôles
INSERT INTO role (id, nom, description) VALUES
(1, 'Admin', 'Administrateur système'),
(2, 'User', 'Utilisateur standard'),
(3, 'Auditeur', 'Auditeur');

-- ÉTAPE 8: Insérer Permissions
INSERT INTO permission (nom, description) VALUES
('voir_utilisateurs', 'Voir les utilisateurs'),
('creer_utilisateur', 'Créer utilisateur'),
('modifier_utilisateur', 'Modifier utilisateur'),
('supprimer_utilisateur', 'Supprimer utilisateur'),
('voir_rapports', 'Voir rapports'),
('creer_rapport', 'Créer rapport'),
('gerer_roles', 'Gérer rôles'),
('gerer_permissions', 'Gérer permissions');

-- ÉTAPE 9: Associer Permissions/Rôles
INSERT INTO role_permission (role_id, permission_id) VALUES
(1,1), (1,2), (1,3), (1,4), (1,5), (1,6), (1,7), (1,8),
(2,1), (2,5),
(3,5);

-- ÉTAPE 10: Insérer Utilisateurs de Test
-- IMPORTANT: Les mots de passe sont en SHA-256
-- admin123 = a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
-- user123 = 04f8996da763b7a969b1028ee3007569eaf3a635486f694971d3311271d7cb81

INSERT INTO userjava (nom, email, password_hash, age, role_id, actif, telephone) VALUES
('Admin Audit', 'admin@mindaudit.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 35, 1, TRUE, '+33123456789'),
('Jean User', 'user@mindaudit.com', '04f8996da763b7a969b1028ee3007569eaf3a635486f694971d3311271d7cb81', 28, 2, TRUE, '+33123456788');

-- ÉTAPE 11: Vérifier les données
SELECT '✓ Base de données configurée' AS status;
SELECT COUNT(*) as total_users FROM userjava;
SELECT email, nom FROM userjava;
