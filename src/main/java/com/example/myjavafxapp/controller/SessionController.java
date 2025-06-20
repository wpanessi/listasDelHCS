package com.example.myjavafxapp.controller;

import com.example.myjavafxapp.model.Councilor;
import com.example.myjavafxapp.model.Session;
import com.example.myjavafxapp.repository.CouncilorRepository;
import com.example.myjavafxapp.repository.SessionRepository;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class SessionController {

    @FXML
    private TableView<Session> sessionTable;
    @FXML
    private TableColumn<Session, String> sessionNumberColumn;
    @FXML
    private TableColumn<Session, LocalDate> dateColumn;
    @FXML
    private TableColumn<Session, Boolean> isActiveColumn;

    @FXML
    private TextField sessionNumberField;
    @FXML
    private DatePicker datePicker;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button toggleActivationButton;

    @FXML
    private VBox attendancePane;
    @FXML
    private Label attendanceSessionLabel;
    @FXML
    private ListView<Councilor> attendanceCouncilorListView;

    private final SessionRepository sessionRepository;
    private final CouncilorRepository councilorRepository;
    private final ObservableList<Session> sessionData = FXCollections.observableArrayList();
    private final ObservableList<Councilor> allCouncilors = FXCollections.observableArrayList();
    private Session selectedSession = null;

    @Autowired
    public SessionController(SessionRepository sessionRepository, CouncilorRepository councilorRepository) {
        this.sessionRepository = sessionRepository;
        this.councilorRepository = councilorRepository;
    }

    @FXML
    public void initialize() {
        allCouncilors.addAll(councilorRepository.findAll());
        sessionData.addAll(sessionRepository.findAll());
        System.out.println("Sessions loaded from DB: " + sessionData.size());
        System.out.println("All councilors loaded: " + allCouncilors.size());

        sessionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("sessionNumberPerYear"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        isActiveColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isActive()).asObject());
        isActiveColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("active-session-yes", "active-session-no");
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Yes" : "No");
                    if (item) {
                        getStyleClass().add("active-session-yes");
                    } else {
                        getStyleClass().add("active-session-no");
                    }
                }
            }
        });

        sessionTable.setItems(sessionData);

        sessionTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedSession = newValue;
                    populateForm(newValue);
                    loadAttendanceView(newValue);
                    updateActivationButtonState();
                }
        );

        attendanceCouncilorListView.setCellFactory(CheckBoxListCell.forListView(councilor -> {
            SimpleBooleanProperty selectedProperty = new SimpleBooleanProperty(
                selectedSession != null && selectedSession.getPresentCouncilors().contains(councilor)
            );
            selectedProperty.addListener((obs, wasSelected, isNowSelected) -> {
                if (selectedSession != null && selectedSession.isActive()) {
                    if (isNowSelected) {
                        selectedSession.getPresentCouncilors().add(councilor);
                    } else {
                        selectedSession.getPresentCouncilors().remove(councilor);
                    }
                    sessionRepository.save(selectedSession);
                    System.out.println("Attendance updated for " + councilor.getFirstName() + ": " + isNowSelected);
                } else if (selectedSession != null && !selectedSession.isActive() && isNowSelected) {
                     showErrorAlert("Attendance Error", "Session is not active. Cannot modify attendance.");
                     selectedProperty.set(wasSelected);
                }
            });
            return selectedProperty;
        }));

        attendanceCouncilorListView.setItems(allCouncilors);

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        toggleActivationButton.setDisable(true);
        updateActivationButtonState(); // Set initial style and text
        attendancePane.setVisible(false);
        attendancePane.setManaged(false);
    }

    private void populateForm(Session session) {
        if (session != null) {
            sessionNumberField.setText(session.getSessionNumberPerYear());
            datePicker.setValue(session.getDate());
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            toggleActivationButton.setDisable(false);
        } else {
            clearForm();
        }
        updateActivationButtonState();
    }

    private void updateActivationButtonState() {
        toggleActivationButton.getStyleClass().removeAll("activate", "deactivate");
        if (selectedSession != null) {
            if (selectedSession.isActive()) {
                toggleActivationButton.setText("Deactivate");
                toggleActivationButton.getStyleClass().add("deactivate");
            } else {
                toggleActivationButton.setText("Activate");
                toggleActivationButton.getStyleClass().add("activate");
            }
            toggleActivationButton.setDisable(false);
        } else {
            toggleActivationButton.setText("Activate/Deactivate");
            toggleActivationButton.setDisable(true);
        }
    }

    private void loadAttendanceView(Session session) {
        if (session != null) {
            attendanceSessionLabel.setText("Attendance for: " + session.getSessionNumberPerYear());
            attendancePane.setVisible(true);
            attendancePane.setManaged(true);
            attendanceCouncilorListView.setDisable(!session.isActive());

            ObservableList<Councilor> tempCouncilorsForAttendance = FXCollections.observableArrayList();
            for (Councilor c : allCouncilors) {
                 tempCouncilorsForAttendance.add(c);
            }
            attendanceCouncilorListView.setItems(null);
            attendanceCouncilorListView.setItems(tempCouncilorsForAttendance);

        } else {
            attendanceSessionLabel.setText("Attendance: No session selected");
            attendancePane.setVisible(false);
            attendancePane.setManaged(false);
        }
    }

    @FXML
    private void handleAddButtonAction() {
        if (validateInput()) {
            Session newSession = new Session(
                    sessionNumberField.getText(),
                    datePicker.getValue()
            );
            Session savedSession = sessionRepository.save(newSession);
            sessionData.add(savedSession);
            System.out.println("Added Session: " + savedSession.getSessionNumberPerYear() + " with ID: " + savedSession.getId());
            clearForm();
            sessionTable.refresh();
        }
    }

    @FXML
    private void handleUpdateButtonAction() {
        if (selectedSession != null && validateInput()) {
            selectedSession.setSessionNumberPerYear(sessionNumberField.getText());
            selectedSession.setDate(datePicker.getValue());
            Session updatedSession = sessionRepository.save(selectedSession);
            int index = sessionData.indexOf(selectedSession);
            if (index != -1) {
                sessionData.set(index, updatedSession);
            } else {
                sessionTable.refresh();
            }
            System.out.println("Updated Session: " + updatedSession.getSessionNumberPerYear());
            clearForm();
            sessionTable.refresh();
        }
    }

    @FXML
    private void handleDeleteButtonAction() {
        if (selectedSession != null) {
            Optional<ButtonType> result = showConfirmationAlert(
                "Confirm Deletion",
                "Are you sure you want to delete session: " + selectedSession.getSessionNumberPerYear() + " (" + selectedSession.getDate() + ")?"
            );

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    if (selectedSession.isActive()) {
                        showErrorAlert("Delete Error", "Cannot delete an active session. Please deactivate it first.");
                        return;
                    }
                    sessionRepository.delete(selectedSession);
                    sessionData.remove(selectedSession);
                    System.out.println("Deleted Session: " + selectedSession.getSessionNumberPerYear());
                    showInformationAlert("Success", "Session deleted successfully.");
                    clearForm();
                    sessionTable.refresh();
                    loadAttendanceView(null);
                } catch (Exception e) {
                    showErrorAlert("Delete Error", "Failed to delete session: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showErrorAlert("No Selection", "No session selected for deletion.");
        }
    }

    @FXML
    private void handleClearButtonAction() {
        clearForm();
    }

    private void clearForm() {
        sessionNumberField.clear();
        datePicker.setValue(null);
        selectedSession = null;
        sessionTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        // toggleActivationButton.setDisable(true); // updateActivationButtonState will handle this
        // toggleActivationButton.setText("Activate/Deactivate"); // updateActivationButtonState will handle this
        attendancePane.setVisible(false);
        attendancePane.setManaged(false);
        sessionNumberField.requestFocus();
        updateActivationButtonState(); // Ensure button is in correct state after clear
    }

    @FXML
    private void handleToggleActivationButtonAction() {
        if (selectedSession == null) {
            showErrorAlert("Action Error", "Please select a session to activate/deactivate.");
            return;
        }

        if (!selectedSession.isActive()) {
            long activeSessionsCount = sessionRepository.countByIsActiveTrue();
            if (activeSessionsCount > 0) {
                showErrorAlert("Activation Error", "Another session is already active. Please deactivate it first.");
                return;
            }
            selectedSession.setActive(true);
            System.out.println("Activated session: " + selectedSession.getSessionNumberPerYear());
        } else {
            selectedSession.setActive(false);
            System.out.println("Deactivated session: " + selectedSession.getSessionNumberPerYear());
        }

        sessionRepository.save(selectedSession);
        sessionTable.refresh();
        updateActivationButtonState();
        loadAttendanceView(selectedSession);
    }

    private boolean validateInput() {
        if (sessionNumberField.getText() == null || sessionNumberField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "Session Number/Year cannot be empty.");
            sessionNumberField.requestFocus();
            return false;
        }
        if (datePicker.getValue() == null) {
            showErrorAlert("Validation Error", "Date cannot be empty.");
            datePicker.requestFocus();
            return false;
        }
        return true;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
