package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button allowOnceButton;

    private User user;

    @FXML
    public void initialize() {
        user = new User("Guest User");
        updateView();
    }

    @FXML
    private void handleAllowOnce() {
        user.setAllowedOnce(true);
        updateView();
        // Automatically pass or proceed
        if (welcomeLabel != null) {
            welcomeLabel.setText("Allowed once! Proceeding automatically.");
        }
        if (allowOnceButton != null) {
            allowOnceButton.setDisable(true);
        }
    }

    private void updateView() {
        if (user.isAllowedOnce()) {
            welcomeLabel.setText("Welcome, " + user.getName() + "! You are allowed.");
        } else {
            welcomeLabel.setText("Welcome, " + user.getName() + "! Click to allow once.");
        }
    }
}