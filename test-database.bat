@echo off
echo === TEST DE LA BASE DE DONNEES ===
echo.
echo Verification de MySQL...
mysql -u root -e "SELECT 'MySQL fonctionne!' as Status;"
echo.
echo Verification de la base mindaudit...
mysql -u root -e "USE mindaudit; SELECT COUNT(*) as NombreUtilisateurs FROM users;"
echo.
echo Affichage des utilisateurs...
mysql -u root -e "USE mindaudit; SELECT id, nom, email, role_id, actif FROM users LIMIT 5;"
echo.
pause
