# 🔧 Guide de Résolution - Erreur "package javafx.fxml does not exist"

## ❌ Problème
```
java: package javafx.fxml does not exist
```

Cette erreur signifie que les dépendances JavaFX ne sont pas téléchargées.

---

## ✅ SOLUTION 1: Recharger Maven dans IntelliJ IDEA (RECOMMANDÉ)

### Étape 1: Ouvrir le Panneau Maven
1. Ouvrez **IntelliJ IDEA**
2. Cliquez sur l'onglet **"Maven"** à droite de l'écran
   - Si vous ne le voyez pas: **View** → **Tool Windows** → **Maven**

### Étape 2: Recharger le Projet
1. Dans le panneau Maven, cliquez sur l'icône **🔄 "Reload All Maven Projects"** (en haut)
2. Attendez que Maven télécharge toutes les dépendances
3. Vous verrez dans la console en bas:
   ```
   Downloading: javafx-controls-17.0.2.jar
   Downloading: javafx-fxml-17.0.2.jar
   ...
   ```

### Étape 3: Rebuild le Projet
1. Menu **Build** → **Rebuild Project**
2. Attendez la fin de la compilation
3. ✅ **Terminé!**

---

## ✅ SOLUTION 2: Via le Menu Contextuel

1. Dans l'explorateur de fichiers (à gauche), trouvez **`pom.xml`**
2. **Clic droit** sur `pom.xml`
3. Sélectionnez **"Maven"** → **"Reload Project"**
4. Attendez le téléchargement
5. **Build** → **Rebuild Project**

---

## ✅ SOLUTION 3: Invalider le Cache (Si les solutions 1 et 2 ne marchent pas)

### Étape 1: Invalider le Cache
1. Menu **File** → **Invalidate Caches...**
2. Cochez toutes les options:
   - ✅ Invalidate and Restart
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
3. Cliquez sur **"Invalidate and Restart"**
4. IntelliJ va redémarrer

### Étape 2: Après le Redémarrage
1. Attendez que IntelliJ finisse d'indexer (barre de progression en bas)
2. Ouvrez le panneau **Maven** (à droite)
3. Cliquez sur **🔄 Reload All Maven Projects**
4. **Build** → **Rebuild Project**

---

## ✅ SOLUTION 4: Supprimer le Cache Maven Local

Si rien ne marche, supprimez le cache Maven:

### Windows:
1. Fermez IntelliJ IDEA
2. Ouvrez l'Explorateur de fichiers
3. Allez dans: `C:\Users\DELL\.m2\repository\org\openjfx`
4. Supprimez le dossier `openjfx`
5. Rouvrez IntelliJ IDEA
6. Maven → Reload Project
7. Les dépendances seront re-téléchargées

---

## ✅ SOLUTION 5: Vérifier la Configuration Java

### Vérifier le JDK
1. **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings** → **Project**
3. Vérifiez que **Project SDK** est **Java 17** ou supérieur
4. Si non, cliquez sur **"Add SDK"** → **"Download JDK"**
5. Choisissez **Java 17** (Eclipse Temurin ou Oracle)
6. Cliquez sur **"Download"**

### Vérifier les Modules
1. Toujours dans **Project Structure**
2. **Project Settings** → **Modules**
3. Sélectionnez **mindjavafx**
4. Onglet **"Dependencies"**
5. Vérifiez que vous voyez:
   - ✅ javafx-controls-17.0.2
   - ✅ javafx-fxml-17.0.2
   - ✅ mysql-connector-java-8.0.33
6. Si elles manquent, cliquez sur **"+"** → **"Library"** → **"From Maven"**
7. Recherchez: `org.openjfx:javafx-controls:17.0.2`
8. Cliquez sur **"OK"**
9. Répétez pour `org.openjfx:javafx-fxml:17.0.2`

---

## ✅ SOLUTION 6: Installer Maven (Optionnel)

Si vous voulez compiler en ligne de commande:

### Télécharger Maven
1. Allez sur: https://maven.apache.org/download.cgi
2. Téléchargez **apache-maven-3.9.x-bin.zip**
3. Extrayez dans: `C:\Program Files\Apache\maven`

### Ajouter au PATH
1. Recherchez **"Variables d'environnement"** dans Windows
2. Cliquez sur **"Variables d'environnement..."**
3. Dans **"Variables système"**, trouvez **"Path"**
4. Cliquez sur **"Modifier"**
5. Cliquez sur **"Nouveau"**
6. Ajoutez: `C:\Program Files\Apache\maven\bin`
7. Cliquez sur **"OK"** partout
8. **Redémarrez le terminal**

### Tester Maven
Ouvrez un nouveau terminal et tapez:
```bash
mvn -version
```

Vous devriez voir:
```
Apache Maven 3.9.x
```

### Compiler
```bash
mvn clean compile
```

---

## 🎯 Résumé Rapide

**La solution la plus simple:**

1. **IntelliJ IDEA** → Panneau **Maven** (à droite)
2. Cliquez sur **🔄 Reload All Maven Projects**
3. Attendez le téléchargement
4. **Build** → **Rebuild Project**
5. ✅ **Ça marche!**

---

## 📞 Si Rien Ne Marche

Vérifiez:
- ✅ Connexion internet active (Maven doit télécharger les dépendances)
- ✅ Java 17 installé (`java -version` dans le terminal)
- ✅ IntelliJ IDEA à jour (version 2023 ou plus récente)
- ✅ Pas de proxy/firewall bloquant Maven

---

**Bonne chance! 🚀**
