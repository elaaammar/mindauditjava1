#!/bin/bash
# Script de compilation MindAudit pour Linux/Mac

echo "============================================"
echo "   Configuration MindAudit - JavaFX Audit"
echo "============================================"
echo ""

# Verifier Java
echo "[1/3] Verification de Java..."
if ! command -v java &> /dev/null; then
    echo "ERREUR: Java 17 n'est pas installe!"
    echo "Telecharger depuis: https://adoptium.net/"
    exit 1
fi
echo "OK: Java detecte"

# Verifier Maven
echo ""
echo "[2/3] Verification de Maven..."
if ! command -v mvn &> /dev/null; then
    echo "ERREUR: Maven n'est pas installe!"
    echo "Installer depuis: https://maven.apache.org/"
    echo ""
    echo "Ou utiliser Homebrew (Mac): brew install maven"
    echo "Ou utiliser apt (Linux): sudo apt install maven"
    exit 1
fi
echo "OK: Maven detecte"

# Compiler
echo ""
echo "[3/3] Compilation du projet..."
echo ""
mvn clean compile

if [ $? -ne 0 ]; then
    echo ""
    echo "ERREUR de compilation!"
    exit 1
fi

echo ""
echo "============================================"
echo "   Compilation reussie!"
echo "============================================"
echo ""
echo "Pour lancer l'application:"
echo "   mvn javafx:run"
echo ""
