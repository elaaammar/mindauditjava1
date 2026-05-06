@echo off
echo Correction du mot de passe pour user@mindaudit.com...
mysql -u root -proot < fix-user-login.sql
if %errorlevel% equ 0 (
    echo.
    echo ✓ Mot de passe corrige avec succes!
    echo.
    echo Vous pouvez maintenant vous connecter avec:
    echo Email: user@mindaudit.com
    echo Mot de passe: user123
) else (
    echo.
    echo ✗ Erreur lors de la correction
)
pause
