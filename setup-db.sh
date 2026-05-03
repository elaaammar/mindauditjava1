#!/bin/bash
# Script pour initialiser la base de données MySQL MindAudit
# Utilisation: ./setup-db.sh

echo "=========================================="
echo "  Configuration Base de Données MindAudit"
echo "=========================================="
echo ""

# Vérifier si mysql est installé
if ! command -v mysql &> /dev/null; then
    echo "ERREUR: MySQL n'est pas installé ou pas accessible"
    echo "Installation requise: https://www.mysql.com/downloads/"
    exit 1
fi

# Demander les identifiants
read -sp "Entrez le mot de passe MySQL root: " DB_PASSWORD
echo ""

# Exécuter le script SQL
echo ""
echo "Exécution du script de configuration..."
echo ""

mysql -u root -p"$DB_PASSWORD" < setup-database.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "  ✓ Base de données configurée avec succès!"
    echo "=========================================="
    echo ""
    echo "Identifiants de test:"
    echo "  Admin:   admin@mindaudit.com / admin123"
    echo "  User:    user@mindaudit.com / user123"
    echo "  Auditeur: marie@mindaudit.com / marie123"
    echo ""
else
    echo ""
    echo "ERREUR lors de l'exécution du script SQL"
    exit 1
fi
