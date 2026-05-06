package com.gestion.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

public class UiHelper {
    public static void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public static void showError(Label label, String msg, Control field) {
        if (label != null) {
            label.setText(msg);
            label.setVisible(true);
        }
        if (field != null) field.setStyle("-fx-border-color: red;");
    }
    public static void hideError(Label label, Control field) {
        if (label != null) label.setVisible(false);
        if (field != null) field.setStyle("");
    }
    public static void resetFieldStyles(Control... fields) {
        for (Control f : fields) f.setStyle("");
    }
}
