-- ============================================
-- MindAudit - Script de Restauration de la Base de Données
-- ============================================
-- Ce script recrée toute la structure de la base de données MindAudit
-- incluant les utilisateurs, les audits, les notifications et les rapports.

-- 1. Créer la base de données
DROP DATABASE IF EXISTS mindaudit;
CREATE DATABASE mindaudit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mindaudit;

-- 2. Table des Rôles
CREATE TABLE role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. Table des Permissions
CREATE TABLE permission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. Table de Liaison Rôles-Permissions
CREATE TABLE role_permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Table des Utilisateurs (userjava)
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

-- 6. Table des Audits
CREATE TABLE audit (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100),
    global_score INT DEFAULT 0,
    security_score INT DEFAULT 0,
    compliance_score INT DEFAULT 0,
    performance_score INT DEFAULT 0,
    findings TEXT,
    status VARCHAR(50) DEFAULT 'En cours',
    audit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Table des Notifications
CREATE TABLE notification (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT,
    type VARCHAR(50), -- 'alert', 'recommendation', 'info'
    is_read BOOLEAN DEFAULT FALSE,
    related_entity_type VARCHAR(50),
    related_entity_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Table des Rapports
CREATE TABLE report (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    audit_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size LONG,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    FOREIGN KEY (audit_id) REFERENCES audit(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================
-- DONNÉES PAR DÉFAUT
-- ============================================

-- Rôles
INSERT INTO role (id, nom, description) VALUES
(1, 'Admin', 'Administrateur système complet'),
(2, 'User', 'Utilisateur standard / Professionnel'),
(3, 'Auditeur', 'Auditeur avec accès lecture');

-- Permissions
INSERT INTO permission (nom, description) VALUES
('voir_utilisateurs', 'Voir la liste des utilisateurs'),
('creer_utilisateur', 'Créer un utilisateur'),
('modifier_utilisateur', 'Modifier un utilisateur'),
('supprimer_utilisateur', 'Supprimer un utilisateur'),
('voir_rapports', 'Consulter les rapports'),
('creer_rapport', 'Générer des rapports'),
('gerer_roles', 'Administration des rôles'),
('gerer_permissions', 'Administration des permissions');

-- Liaison Rôles-Permissions (Admin a tout)
INSERT INTO role_permission (role_id, permission_id) 
SELECT 1, id FROM permission;

-- Liaison Rôles-Permissions (User a accès aux rapports)
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 1), (2, 5), (2, 6);

-- Utilisateurs par défaut (Mot de passe: password123 en SHA-256)
-- admin@mindaudit.com / admin123 (a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3)
-- user@mindaudit.com / user123 (04f8996da763b7a969b1028ee3007569eaf3a635486f694971d3311271d7cb81)

INSERT INTO userjava (nom, email, password_hash, age, role_id, actif, telephone) VALUES
('Admin System', 'admin@mindaudit.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 35, 1, TRUE, '+33123456789'),
('Jean Professionnel', 'user@mindaudit.com', '04f8996da763b7a969b1028ee3007569eaf3a635486f694971d3311271d7cb81', 28, 2, TRUE, '+33123456788');

-- ============================================
-- INDEX DE PERFORMANCE
-- ============================================
CREATE INDEX idx_user_email ON userjava(email);
CREATE INDEX idx_audit_user ON audit(user_id);
CREATE INDEX idx_notif_user ON notification(user_id);
CREATE INDEX idx_report_user ON report(user_id);

SELECT '✓ Base de données MindAudit restaurée avec succès!' AS status;
