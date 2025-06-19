package com.example.myjavafxapp.controller;

import com.example.myjavafxapp.model.Councilor;
import com.example.myjavafxapp.repository.CouncilorRepository;
import com.example.myjavafxapp.service.SessionStateService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PresidentSessionController {

    @FXML
    private ListView<Councilor> availableCouncilorsListView;
    @FXML
    private Button requestSpeakButton;

    @FXML
    private ListView<Councilor> oratorsListView;
    @FXML
    private Button removeSpeakerButton;
    @FXML
    private Button moveToSpeakingButton;

    @FXML
    private Label currentlySpeakingLabel;
    @FXML
    private Button finishedSpeakingButton;

    private final CouncilorRepository councilorRepository;
    private final SessionStateService sessionStateService;

    private final ObservableList<Councilor> availableCouncilors = FXCollections.observableArrayList();
    // oratorsList and currentlySpeakingCouncilor are now managed by SessionStateService

    @Autowired
    public PresidentSessionController(CouncilorRepository councilorRepository, SessionStateService sessionStateService) {
        this.councilorRepository = councilorRepository;
        this.sessionStateService = sessionStateService;
    }

    @FXML
    public void initialize() {
        // Load available councilors from repository
        availableCouncilors.addAll(councilorRepository.findAll());
        System.out.println("PresidentSessionController: Available councilors loaded from DB: " + availableCouncilors.size());

        setupListViewCellFactory(availableCouncilorsListView);
        setupListViewCellFactory(oratorsListView);

        availableCouncilorsListView.setItems(availableCouncilors);
        // Bind ListView to the service's read-only list property
        oratorsListView.itemsProperty().bind(sessionStateService.oratorsListProperty());

        // Bind Label to the service's read-only object property
        sessionStateService.currentlySpeakingCouncilorProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentlySpeakingLabel.setText(newVal.getFirstName() + " " + newVal.getLastName());
            } else {
                currentlySpeakingLabel.setText("None");
            }
            updateButtonStates();
        });

        updateButtonStates(); // Initial button states
    }

    private void setupListViewCellFactory(ListView<Councilor> listView) {
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Councilor councilor, boolean empty) {
                super.updateItem(councilor, empty);
                if (empty || councilor == null) {
                    setText(null);
                } else {
                    setText(councilor.getFirstName() + " " + councilor.getLastName() + " (" + councilor.getListName() + ")");
                }
            }
        });
    }


    @FXML
    private void handleRequestSpeakButtonAction() {
        Councilor selected = availableCouncilorsListView.getSelectionModel().getSelectedItem();
        if (selected != null &&
            !sessionStateService.getOratorsList().contains(selected) &&
            (sessionStateService.currentlySpeakingCouncilorProperty().get() == null ||
             !sessionStateService.currentlySpeakingCouncilorProperty().get().equals(selected))) {
            sessionStateService.addOrator(selected);
        }
        updateButtonStates();
    }

    @FXML
    private void handleRemoveSpeakerButtonAction() {
        Councilor selected = oratorsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInformationAlert("Action Hint", "Please select an orator from the list to remove.");
            return;
        }
        sessionStateService.removeOrator(selected);
        if (selected.equals(sessionStateService.currentlySpeakingCouncilorProperty().get())) {
             sessionStateService.setCurrentlySpeaking(null); // Also clear current speaker if they are removed
        }
        updateButtonStates();
    }

    @FXML
    private void handleMoveToSpeakingButtonAction() {
        if (sessionStateService.currentlySpeakingCouncilorProperty().get() != null) {
            showInformationAlert("Action Hint", "Someone is already speaking. Please wait for them to finish.");
            return;
        }
        if (sessionStateService.getOratorsList().isEmpty()) {
            showInformationAlert("Action Hint", "Orators list is empty. No one to move to speak.");
            return;
        }
        Councilor nextSpeaker = sessionStateService.getNextOrator();
        if (nextSpeaker != null) {
            sessionStateService.setCurrentlySpeaking(nextSpeaker);
        }
        updateButtonStates();
    }

    @FXML
    private void handleFinishedSpeakingButtonAction() {
        Councilor finishedSpeaker = sessionStateService.currentlySpeakingCouncilorProperty().get();
        if (finishedSpeaker != null) {
            sessionStateService.removeOrator(finishedSpeaker); // Remove from orators list after speaking
            sessionStateService.setCurrentlySpeaking(null);

            if (!sessionStateService.getOratorsList().isEmpty()) {
                handleMoveToSpeakingButtonAction(); // Automatically try to move next speaker
            }
        }
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean someoneSpeaking = sessionStateService.currentlySpeakingCouncilorProperty().get() != null;
        boolean oratorsListEmpty = sessionStateService.getOratorsList().isEmpty();
        boolean availableSelected = availableCouncilorsListView.getSelectionModel().getSelectedItem() != null;
        boolean oratorSelected = oratorsListView.getSelectionModel().getSelectedItem() != null;

        requestSpeakButton.setDisable(!availableSelected || someoneSpeaking);

        moveToSpeakingButton.setDisable(oratorsListEmpty || someoneSpeaking);
        removeSpeakerButton.setDisable(!oratorSelected || (someoneSpeaking && selectedOratorIsCurrentlySpeaking()));

        finishedSpeakingButton.setDisable(!someoneSpeaking);

        Councilor selectedAvailable = availableCouncilorsListView.getSelectionModel().getSelectedItem();
        if (selectedAvailable != null) {
            boolean alreadyOrator = sessionStateService.getOratorsList().contains(selectedAvailable);
            boolean isCurrentlySpeaking = selectedAvailable.equals(sessionStateService.currentlySpeakingCouncilorProperty().get());
            if (alreadyOrator || isCurrentlySpeaking) {
                 requestSpeakButton.setDisable(true);
            }
        } else {
             requestSpeakButton.setDisable(true); // No selection in available list
        }
    }

    private boolean selectedOratorIsCurrentlySpeaking() {
        Councilor selectedOrator = oratorsListView.getSelectionModel().getSelectedItem();
        return selectedOrator != null && selectedOrator.equals(sessionStateService.currentlySpeakingCouncilorProperty().get());
    }

    // Helper methods for alerts (can be moved to a utility class later)
    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformationAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
