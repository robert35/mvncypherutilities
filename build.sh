#!/bin/bash

# MVNCypherUtilities - Script de build
set -e

echo "==> Nettoyage du repository local"
rm -rf ~/.m2/repository/com/epsilon777/mvncypherutilities/

echo "==> Installation du POM parent"
mvn clean install -N -DskipTests

echo "==> Installation de l'extension mvndecrypt"
cd mvndecrypt
mvn clean install -DskipTests
cd ..

echo "==> Build complet de tous les modules"
mvn clean install -DskipTests

echo ""
echo "✓ Build terminé avec succès"
