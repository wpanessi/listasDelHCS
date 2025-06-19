package com.example.myjavafxapp.controller;

import com.example.myjavafxapp.model.Session;
import com.example.myjavafxapp.repository.SessionRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SessionController {

    @FXML
    private TableView<Session> sessionTable;
    @FXML
    private TableColumn<Session, String> sessionNumberColumn;
    @FXML
    private TableColumn<Session, LocalDate> dateColumn;

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

    private final SessionRepository sessionRepository;
    private final ObservableList<Session> sessionData = FXCollections.observableArrayList();
    private Session selectedSession = null;

    @Autowired
    public SessionController(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @FXML
    public void initialize() {
        sessionData.addAll(sessionRepository.findAll());
        System.out.println("Sessions loaded from DB: " + sessionData.size());

        sessionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("sessionNumberPerYear"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        sessionTable.setItems(sessionData);

        sessionTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedSession = newValue;
                    populateForm(newValue);
                }
        );

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void populateForm(Session session) {
        if (session != null) {
            sessionNumberField.setText(session.getSessionNumberPerYear());
            datePicker.setValue(session.getDate());
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
        } else {
            clearForm();
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
        }
    }

    @FXML
    private void handleDeleteButtonAction() {
        if (selectedSession != null) {
            sessionRepository.delete(selectedSession);
            sessionData.remove(selectedSession);
            System.out.println("Deleted Session: " + selectedSession.getSessionNumberPerYear());
            clearForm();
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
        sessionNumberField.requestFocus();
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
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private java.util.Optional<javafx.scene.control.ButtonType> showConfirmationAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void showInformationAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
