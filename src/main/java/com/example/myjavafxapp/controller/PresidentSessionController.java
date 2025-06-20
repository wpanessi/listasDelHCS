package com.example.myjavafxapp.controller;

import com.example.myjavafxapp.model.CouncilList;
import com.example.myjavafxapp.model.CouncilList;
import com.example.myjavafxapp.model.Councilor;
import com.example.myjavafxapp.model.Session;
import com.example.myjavafxapp.repository.CouncilorRepository;
import com.example.myjavafxapp.repository.SessionRepository;
import java.util.Set; // Added import
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
    @FXML
    private Label activeSessionInfoLabel; // Added

    private final CouncilorRepository councilorRepository;
    private final SessionRepository sessionRepository; // Added
    private final SessionStateService sessionStateService;

    private final ObservableList<Councilor> availableCouncilors = FXCollections.observableArrayList();
    // oratorsList and currentlySpeakingCouncilor are now managed by SessionStateService

    @Autowired
    public PresidentSessionController(CouncilorRepository councilorRepository,
                                      SessionRepository sessionRepository, // Added
                                      SessionStateService sessionStateService) {
        this.councilorRepository = councilorRepository;
        this.sessionRepository = sessionRepository; // Added
        this.sessionStateService = sessionStateService;
    }

    @FXML
    public void initialize() {
        loadDataForActiveSession(); // New method to load data

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

    private void loadDataForActiveSession() {
        availableCouncilors.clear(); // Clear previous list
        java.util.Optional<Session> activeSessionOpt = sessionRepository.findByIsActiveTrue();

        if (activeSessionOpt.isPresent()) {
            Session activeSession = activeSessionOpt.get();
            activeSessionInfoLabel.setText("Active Session: " + activeSession.getSessionNumberPerYear() + " (Date: " + activeSession.getDate().toString() + ")");
            activeSessionInfoLabel.getStyleClass().removeAll("no-active-session"); // Remove 'no-active' style
            // Eager fetch should have loaded presentCouncilors
            Set<Councilor> presentCouncilorsSet = activeSession.getPresentCouncilors();
            availableCouncilors.addAll(presentCouncilorsSet);
            System.out.println("PresidentSessionController: Loaded " + presentCouncilorsSet.size() + " present councilors for active session.");
            // Enable relevant UI parts
        } else {
            activeSessionInfoLabel.setText("No active session. Please activate a session in the 'Sessions' tab.");
            System.out.println("PresidentSessionController: No active session found.");
            // Disable relevant UI parts if needed, updateButtonStates should handle some of this
            activeSessionInfoLabel.getStyleClass().add("no-active-session"); // Add 'no-active' style
        }
        // Refresh orators list and current speaker from service, as they might have stale references if app was restarted without proper state saving
        // For now, SessionStateService is in-memory and resets on app start.
        // If it were persisted, we'd need to load its state here too.
        sessionStateService.clearOrators(); // Clear any previous orators
        sessionStateService.setCurrentlySpeaking(null); // Clear current speaker
    }

    private void setupListViewCellFactory(ListView<Councilor> listView) {
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Councilor councilor, boolean empty) {
                super.updateItem(councilor, empty);
                if (empty || councilor == null) {
                    setText(null);
                } else {
                    CouncilList list = councilor.getCouncilList();
                    String listDisplay = (list != null) ? list.toString() : "No List";
                    setText(councilor.getFirstName() + " " + councilor.getLastName() + " (" + listDisplay + ")");
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
        if (selectedAvailable != null && !availableCouncilors.isEmpty()) { // Check if availableCouncilors list is not empty
            boolean alreadyOrator = sessionStateService.getOratorsList().contains(selectedAvailable);
            boolean isCurrentlySpeaking = selectedAvailable.equals(sessionStateService.currentlySpeakingCouncilorProperty().get());
            if (alreadyOrator || isCurrentlySpeaking) {
                 requestSpeakButton.setDisable(true);
            }
        } else {
             requestSpeakButton.setDisable(true);
        }

        // Disable all speaker management if no active session / no available councilors
        boolean sessionControlsDisabled = availableCouncilors.isEmpty();
        requestSpeakButton.setDisable(requestSpeakButton.isDisable() || sessionControlsDisabled); // Keep existing disable logic + new condition
        removeSpeakerButton.setDisable(removeSpeakerButton.isDisable() || sessionControlsDisabled);
        moveToSpeakingButton.setDisable(moveToSpeakingButton.isDisable() || sessionControlsDisabled);
        finishedSpeakingButton.setDisable(finishedSpeakingButton.isDisable() || sessionControlsDisabled);
        oratorsListView.setDisable(sessionControlsDisabled);
        availableCouncilorsListView.setDisable(sessionControlsDisabled);

        if(sessionControlsDisabled) {
            currentlySpeakingLabel.setText("None (No active session/attendance)");
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
