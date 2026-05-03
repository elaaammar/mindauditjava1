@echo off
echo ========================================
echo   CORRECTION DU MOT DE PASSE ADMIN
echo ========================================
echo.

REM Chercher MySQL dans les emplacements communs
set MYSQL_PATH=
if exist "C:\xampp\mysql\bin\mysql.exe" set MYSQL_PATH=C:\xampp\mysql\bin\mysql.exe
if exist "C:\wamp64\bin\mysql\mysql8.0.27\bin\mysql.exe" set MYSQL_PATH=C:\wamp64\bin\mysql\mysql8.0.27\bin\mysql.exe
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe

if "%MYSQL_PATH%"=="" (
    echo [ERREUR] MySQL non trouve. Veuillez executer fix-login.sql manuellement dans phpMyAdmin
    pause
    exit /b 1
)

echo MySQL trouve: %MYSQL_PATH%
echo.
echo Execution de la correction...
"%MYSQL_PATH%" -u root < fix-login.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   CORRECTION REUSSIE!
    echo ========================================
    echo.
    echo Vous pouvez maintenant vous connecter:
    echo   Email: admin@mindaudit.com
    echo   Mot de passe: admin123
    echo.
) else (
    echo.
    echo [ERREUR] La correction a echoue
    echo Veuillez executer fix-login.sql manuellement
)

pause
