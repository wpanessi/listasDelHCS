package com.example.myjavafxapp.controller;

import com.example.myjavafxapp.model.Councilor;
import com.example.myjavafxapp.service.SessionStateService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublicDisplayController {

    @FXML
    private Label publicCurrentlySpeakingLabel;
    @FXML
    private ListView<Councilor> publicOratorsListView;

    private final SessionStateService sessionStateService;

    @Autowired
    public PublicDisplayController(SessionStateService sessionStateService) {
        this.sessionStateService = sessionStateService;
    }

    @FXML
    public void initialize() {
        // Bind the currently speaking label
        sessionStateService.currentlySpeakingCouncilorProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                publicCurrentlySpeakingLabel.setText(newVal.getFirstName() + " " + newVal.getLastName());
            } else {
                publicCurrentlySpeakingLabel.setText("None");
            }
        });

        // Bind the orators list view
        publicOratorsListView.itemsProperty().bind(sessionStateService.oratorsListProperty());

        // Set cell factory for the orators list view to display names
        publicOratorsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Councilor councilor, boolean empty) {
                super.updateItem(councilor, empty);
                if (empty || councilor == null) {
                    setText(null);
                } else {
                    setText(councilor.getFirstName() + " " + councilor.getLastName());
                }
            }
        });

        // Initialize with current state
        Councilor currentSpeaker = sessionStateService.currentlySpeakingCouncilorProperty().get();
        if (currentSpeaker != null) {
            publicCurrentlySpeakingLabel.setText(currentSpeaker.getFirstName() + " " + currentSpeaker.getLastName());
        } else {
            publicCurrentlySpeakingLabel.setText("None");
        }
    }
}
