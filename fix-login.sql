-- Fix Login Password Hash
-- Le hash SHA-256 correct pour "admin123" est: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9

USE mindaudit;

-- Mettre à jour le mot de passe admin
UPDATE userjava 
SET password_hash = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
    actif = 1
WHERE email = 'admin@mindaudit.com';

-- Vérifier la mise à jour
SELECT id, nom, email, actif, 
       CASE 
           WHEN password_hash = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9' 
           THEN 'CORRECT ✓' 
           ELSE 'INCORRECT ✗' 
       END as hash_status
FROM userjava 
WHERE email = 'admin@mindaudit.com';

-- Si l'utilisateur n'existe pas, le créer
INSERT INTO userjava (nom, email, password_hash, age, role_id, actif, telephone)
SELECT 'Administrateur', 'admin@mindaudit.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 30, 1, 1, '0000000000'
WHERE NOT EXISTS (SELECT 1 FROM userjava WHERE email = 'admin@mindaudit.com');

SELECT '✓ CORRECTION TERMINÉE - Vous pouvez maintenant vous connecter avec admin@mindaudit.com / admin123' as message;
