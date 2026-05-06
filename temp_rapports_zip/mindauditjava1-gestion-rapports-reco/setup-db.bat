@echo off
REM Script pour initialiser la base de données MySQL MindAudit
REM Utilisation: setup-db.bat

echo.
echo ==========================================
echo   Configuration Base de Donnees MindAudit
echo ==========================================
echo.

REM Verifier si mysql est installe
mysql --version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: MySQL n'est pas installe ou pas accessible
    echo Installation requise: https://www.mysql.com/downloads/
    echo.
    echo Alternative: Utiliser MySQL Workbench ou phpMyAdmin
    echo 1. Ouvrir MySQL Workbench
    echo 2. Copier le contenu de setup-database.sql
    echo 3. Executer le script
    pause
    exit /b 1
)

echo Prêt à configurer la base de données MindAudit
echo.
echo Deux options:
echo.
echo Option 1: Exécution automatique (recommandé)
echo   Appuyez sur une touche pour continuer...
pause >nul

REM Demander mot de passe
set /p DB_PASSWORD="Entrez le mot de passe MySQL root (vide si pas de mot de passe): "

REM Exécuter le script SQL
echo.
echo Execution du script de configuration...
echo.

mysql -u root -p%DB_PASSWORD% < setup-database.sql

if errorlevel 0 (
    echo.
    echo ==========================================
    echo   Base de donnees configuree avec succes!
    echo ==========================================
    echo.
    echo Identifiants de test:
    echo   Admin:   admin@mindaudit.com / admin123
    echo   User:    user@mindaudit.com / user123
    echo   Auditeur: marie@mindaudit.com / marie123
    echo.
) else (
    echo.
    echo ERREUR lors de l'execution du script SQL
    echo Assurez-vous que:
    echo   1. MySQL server est en fonctionnement
    echo   2. Vous aviez le bon mot de passe root
    echo.
)

pause
