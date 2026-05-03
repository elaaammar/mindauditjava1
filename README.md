# MindJavaFX

Un projet JavaFX avec architecture MVC, sans DAO.

## Structure
- `src/main/java/com/example/mindjavafx/` : Code source
  - `model/` : Classes de modèle (ex: User)
  - `controller/` : Contrôleurs (ex: MainController)
  - `view/` : Non utilisé directement, FXML dans resources
- `src/main/resources/fxml/` : Fichiers FXML
- `pom.xml` : Configuration Maven

## Fonctionnalités
- Bouton "Allow Once" qui définit un drapeau et met à jour la vue automatiquement.

## Exécution
Assurez-vous que Maven et JavaFX sont installés.
```bash
mvn clean compile
mvn javafx:run
```