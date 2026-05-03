@echo off
REM Script d'installation et compilation - MindAudit
REM Exe: run-mindaudit.bat

echo ============================================
echo   Configuration MindAudit - JavaFX Audit
echo ============================================
echo.

REM Verifier Java
echo [1/4] Verification de Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: Java 17 n'est pas installe!
    echo Telecharger depuis: https://adoptium.net/
    pause
    exit /b 1
)
echo OK: Java detecte

REM Verifier Maven
echo.
echo [2/4] Verification de Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: Maven n'est pas installe!
    echo.
    echo Solution 1: Installer Maven depuis https://maven.apache.org/
    echo Solution 2: Utiliser le Maven Wrapper inclus
    echo.
    REM Essayer mvnw
    if exist "mvnw.cmd" (
        echo Tentative avec Maven Wrapper...
        set "MAVEN_CMD=mvnw.cmd"
    ) else (
        echo Veuillez installer Maven ou utiliser un IDE (IntelliJ/Eclipse)
        pause
        exit /b 1
    )
) else (
    set "MAVEN_CMD=mvn"
)
echo OK: Maven detecte

REM Verifier MySQL
echo.
echo [3/4] Verification de MySQL...
mysql -version >nul 2>&1
if errorlevel 1 (
    echo AVERTISSEMENT: MySQL n'est pas accessible via CLI
    echo Assurez-vous que MySQL est installe et en fonctionnement
) else (
    echo OK: MySQL detecte
)

REM Compiler
echo.
echo [4/4] Compilation du projet...
echo.
call %MAVEN_CMD% clean compile

if errorlevel 1 (
    echo.
    echo ERREUR de compilation!
    echo Verifiez les fichiers sources et essayez a nouveau.
    pause
    exit /b 1
)

echo.
echo ============================================
echo   Compilation reussie!
echo ============================================
echo.
echo Pour lancer l'application:
echo   %MAVEN_CMD% javafx:run
echo.
pause
