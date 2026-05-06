@echo off
echo ========================================
echo Test API MindAudit - Mot de passe oublie
echo ========================================
echo.
echo IMPORTANT: Lance d'abord l'application JavaFX!
echo L'API demarre automatiquement sur http://localhost:8080/
echo.
pause
echo.

echo [1/4] Envoi du code de verification...
curl -X POST http://localhost:8080/api/forgot-password/send-code -H "Content-Type: application/json" -d "{\"contact\":\"admin@mindaudit.com\"}"
echo.
echo.

echo [2/4] Entre le code recu (regarde la console Java):
set /p CODE="Code: "
echo.

echo [3/4] Verification du code...
curl -X POST http://localhost:8080/api/forgot-password/verify-code -H "Content-Type: application/json" -d "{\"contact\":\"admin@mindaudit.com\",\"code\":\"%CODE%\"}"
echo.
echo.

echo [4/4] Reinitialisation du mot de passe...
curl -X POST http://localhost:8080/api/forgot-password/reset -H "Content-Type: application/json" -d "{\"contact\":\"admin@mindaudit.com\",\"code\":\"%CODE%\",\"newPassword\":\"newpass123\"}"
echo.
echo.

echo ========================================
echo Test termine!
echo ========================================
pause
