package com.example.myjavafxapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import org.springframework.stereotype.Component;

@Component
public class MainAppController {

    @FXML
    private TabPane mainTabPane;

    public MainAppController() {
        // Constructor
        System.out.println("MainAppController instantiated.");
    }

    @FXML
    public void initialize() {
        // Initialization logic for the main app view, if any
        System.out.println("MainAppController initialized.");
        // For example, you could programmatically select a default tab:
        // mainTabPane.getSelectionModel().select(0); // Selects the first tab
    }
}
