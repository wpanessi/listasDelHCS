package com.example.myjavafxapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label messageLabel;

    public void initialize() {
        System.out.println("MainController initialized. Message label text: " + messageLabel.getText());
        // You could set text here, or it can be set from FXML or by a service later
        // messageLabel.setText("Hello from MainController!");
    }
}
