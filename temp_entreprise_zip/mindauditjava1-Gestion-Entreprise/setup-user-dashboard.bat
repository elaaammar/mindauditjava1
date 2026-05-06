@echo off
echo ========================================
echo  Setup User Dashboard Database
echo ========================================
echo.

mysql -u root -p < setup-user-dashboard.sql

echo.
echo ========================================
echo  Setup complete!
echo ========================================
pause
