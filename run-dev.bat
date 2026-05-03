@echo off
REM Script de développement avec hot reload - MindAudit
REM Ce script lance l'application en mode développement

echo ============================================
echo   MindAudit - Mode Développement
echo   Hot Reload activé
echo ============================================
echo.

REM Vérifier Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: Maven n'est pas installé!
    if exist "mvnw.cmd" (
        set "MAVEN_CMD=mvnw.cmd"
    ) else (
        echo Veuillez installer Maven
        pause
        exit /b 1
    )
) else (
    set "MAVEN_CMD=mvn"
)

echo Lancement de l'application en mode développement...
echo.
echo ASTUCE: Après avoir modifié un fichier:
echo   1. Sauvegardez vos modifications
echo   2. Dans un autre terminal, lancez: mvn compile
echo   3. L'application se rechargera automatiquement
echo.

REM Lancer avec le plugin JavaFX en mode développement
call %MAVEN_CMD% clean javafx:run

pause
