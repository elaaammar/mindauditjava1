-- Fix user@mindaudit.com login
-- Password: user123
-- Correct SHA-256 hash: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9

USE mindaudit;

UPDATE userjava 
SET password_hash = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9' 
WHERE email = 'user@mindaudit.com';

SELECT 'User password updated successfully' AS status;
SELECT email, nom, password_hash FROM userjava WHERE email = 'user@mindaudit.com';
