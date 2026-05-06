-- Professional User Dashboard Database Schema
-- Execute this script to create all required tables for the user dashboard

USE mindaudit;

-- Audit table
CREATE TABLE IF NOT EXISTS audit (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    global_score INT NOT NULL,
    security_score INT,
    compliance_score INT,
    performance_score INT,
    findings TEXT,
    status VARCHAR(50) DEFAULT 'completed',
    audit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_audit_date (audit_date),
    INDEX idx_category (category)
);

-- Notification table
CREATE TABLE IF NOT EXISTS notification (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    related_entity_type VARCHAR(50),
    related_entity_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);

-- Report table
CREATE TABLE IF NOT EXISTS report (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    audit_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    FOREIGN KEY (audit_id) REFERENCES audit(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_audit_id (audit_id)
);

-- Schedule table
CREATE TABLE IF NOT EXISTS schedule (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    audit_name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    scheduled_date DATE NOT NULL,
    scheduled_time TIME NOT NULL,
    reminder_sent BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_scheduled_date (scheduled_date),
    INDEX idx_status (status)
);

-- Recommendation table
CREATE TABLE IF NOT EXISTS recommendation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    recommendation_text TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    is_dismissed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_dismissed (is_dismissed)
);

-- User preferences table
CREATE TABLE IF NOT EXISTS user_preferences (
    user_id INT PRIMARY KEY,
    dark_mode_enabled BOOLEAN DEFAULT FALSE,
    notification_audit_complete BOOLEAN DEFAULT TRUE,
    notification_recommendations BOOLEAN DEFAULT TRUE,
    notification_scheduled_reminders BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE
);

-- Insert sample data for testing (user_id = 2 is the test user)
INSERT INTO audit (user_id, name, category, global_score, security_score, compliance_score, performance_score, findings, audit_date) VALUES
(2, 'Audit Sécurité Q1 2024', 'Sécurité', 85, 90, 80, 85, 'Bonne posture de sécurité générale', '2024-01-15 10:00:00'),
(2, 'Vérification Conformité', 'Conformité', 72, 70, 75, 70, 'Quelques lacunes en conformité', '2024-02-20 14:30:00'),
(2, 'Revue Performance', 'Performance', 65, 60, 70, 65, 'Performance à améliorer', '2024-03-10 09:15:00'),
(2, 'Audit Sécurité Q2 2024', 'Sécurité', 78, 82, 75, 77, 'Amélioration continue nécessaire', '2024-04-05 11:20:00');

-- Insert sample notifications
INSERT INTO notification (user_id, title, message, type, is_read) VALUES
(2, 'Audit Terminé', 'Votre Audit Sécurité Q1 2024 a été complété', 'audit_complete', FALSE),
(2, 'Recommandation', 'Considérez améliorer les métriques de performance', 'recommendation', FALSE),
(2, 'Rappel Planifié', 'Audit planifié pour demain', 'reminder', TRUE),
(2, 'Alerte Sécurité', 'Score de sécurité en baisse détecté', 'alert', FALSE);

-- Insert sample recommendations
INSERT INTO recommendation (user_id, category, recommendation_text, priority, is_dismissed) VALUES
(2, 'Performance', 'Améliorer les temps de réponse des systèmes critiques', 'high', FALSE),
(2, 'Conformité', 'Mettre à jour la documentation de conformité RGPD', 'medium', FALSE),
(2, 'Sécurité', 'Renforcer les politiques de mots de passe', 'high', FALSE);

-- Insert user preferences for test user
INSERT INTO user_preferences (user_id, dark_mode_enabled, notification_audit_complete, notification_recommendations, notification_scheduled_reminders) VALUES
(2, FALSE, TRUE, TRUE, TRUE)
ON DUPLICATE KEY UPDATE user_id = user_id;

SELECT 'Database schema created successfully!' AS status;
