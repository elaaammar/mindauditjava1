@echo off
echo ============================================
echo   Lancement de MindAudit
echo ============================================
echo.

REM Vérifier si Maven est installé
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Maven n'est pas installe ou pas dans le PATH
    echo.
    echo Solutions:
    echo 1. Installer Maven: https://maven.apache.org/download.cgi
    echo 2. Utiliser IntelliJ IDEA pour lancer l'application
    echo 3. Ajouter Maven au PATH Windows
    echo.
    pause
    exit /b 1
)

echo Lancement de l'application JavaFX...
echo.

mvn javafx:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERREUR lors du lancement!
    echo Verifiez que le projet est compile: mvn clean compile
    pause
    exit /b 1
)
