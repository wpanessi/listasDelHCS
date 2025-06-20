package com.example.myjavafxapp.controller;

import com.example.myjavafxapp.model.CouncilList;
import com.example.myjavafxapp.model.Councilor;
import com.example.myjavafxapp.repository.CouncilListRepository;
import com.example.myjavafxapp.repository.CouncilorRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouncilorController {

    @FXML
    private TableView<Councilor> councilorTable;
    @FXML
    private TableColumn<Councilor, String> lastNameColumn;
    @FXML
    private TableColumn<Councilor, String> firstNameColumn;
    @FXML
    private TableColumn<Councilor, String> councilListNumberColumn; // Updated
    @FXML
    private TableColumn<Councilor, String> councilListCloisterColumn; // Updated
    @FXML
    private TableColumn<Councilor, Boolean> isTitularColumn;
    @FXML
    private TableColumn<Councilor, Void> actionsColumn;

    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private ComboBox<CouncilList> councilListComboBox; // Updated
    @FXML
    private CheckBox isTitularCheckBox;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton; // This is the "Delete Selected" button
    @FXML
    private Button clearButton;

    // Using Autowired but it won't be fully utilized until DB persistence is implemented
    private final CouncilorRepository councilorRepository;
    private final CouncilListRepository councilListRepository; // Added

    private final ObservableList<Councilor> councilorData = FXCollections.observableArrayList();
    private Councilor selectedCouncilor = null;

    @Autowired
    public CouncilorController(CouncilorRepository councilorRepository, CouncilListRepository councilListRepository) { // Added
        this.councilorRepository = councilorRepository;
        this.councilListRepository = councilListRepository; // Added
    }

    @FXML
    public void initialize() {
        // Load data from repository
        councilorData.addAll(councilorRepository.findAll());
        System.out.println("Councilors loaded from DB: " + councilorData.size());

        // Populate ComboBox
        councilListComboBox.setItems(FXCollections.observableArrayList(councilListRepository.findAll()));
        System.out.println("CouncilLists loaded into ComboBox: " + councilListComboBox.getItems().size());


        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        councilListNumberColumn.setCellValueFactory(cellData -> {
            CouncilList list = cellData.getValue().getCouncilList();
            return new SimpleStringProperty(list != null ? list.getListNumber() : "");
        });
        councilListCloisterColumn.setCellValueFactory(cellData -> {
            CouncilList list = cellData.getValue().getCouncilList();
            return new SimpleStringProperty(list != null ? list.getCloister() : "");
        });

        isTitularColumn.setCellValueFactory(new PropertyValueFactory<>("isTitular"));

        // Custom cell for isTitular to show Yes/No
        isTitularColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Yes" : "No");
                }
            }
        });

        // Actions column - for now, we'll use the separate "Delete Selected" button
        // So, this column might be removed or repurposed later. For now, it's defined in FXML.
        // If we wanted inline delete buttons:
        // setupActionsColumn();

        councilorTable.setItems(councilorData);

        // Listener for table selection
        councilorTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedCouncilor = newValue;
                    populateForm(newValue);
                }
        );

        updateButton.setDisable(true); // Disable update until a selection is made
        deleteButton.setDisable(true); // Disable delete until a selection is made
    }

    private void populateForm(Councilor councilor) {
        if (councilor != null) {
            lastNameField.setText(councilor.getLastName());
            firstNameField.setText(councilor.getFirstName());
            councilListComboBox.setValue(councilor.getCouncilList()); // Set ComboBox value
            isTitularCheckBox.setSelected(councilor.isTitular());
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
        } else {
            clearForm();
        }
    }

    @FXML
    private void handleAddButtonAction() {
        if (validateInput()) {
            Councilor newCouncilor = new Councilor(
                    lastNameField.getText(),
                    firstNameField.getText(),
                    councilListComboBox.getValue(), // Get from ComboBox
                    isTitularCheckBox.isSelected()
            );
            Councilor savedCouncilor = councilorRepository.save(newCouncilor);
            councilorData.add(savedCouncilor);
            System.out.println("Added councilor: " + savedCouncilor.getFirstName() + " " + savedCouncilor.getLastName() + " with ID: " + savedCouncilor.getId() + ", List: " + (savedCouncilor.getCouncilList() != null ? savedCouncilor.getCouncilList().toString() : "None"));
            clearForm();
        }
    }

    @FXML
    private void handleUpdateButtonAction() {
        if (selectedCouncilor != null && validateInput()) {
            selectedCouncilor.setLastName(lastNameField.getText());
            selectedCouncilor.setFirstName(firstNameField.getText());
            selectedCouncilor.setCouncilList(councilListComboBox.getValue()); // Get from ComboBox
            selectedCouncilor.setTitular(isTitularCheckBox.isSelected());

            Councilor updatedCouncilor = councilorRepository.save(selectedCouncilor);
            // The list is backed by the table, replacing the item or refreshing should update.
            // For simplicity, find and update or just refresh.
            int index = councilorData.indexOf(selectedCouncilor);
            if (index != -1) {
                councilorData.set(index, updatedCouncilor);
            } else {
                councilorTable.refresh(); // Fallback if index not found (should not happen)
            }
            System.out.println("Updated councilor: " + updatedCouncilor.getFirstName() + " " + updatedCouncilor.getLastName());
            clearForm();
        }
    }

    @FXML
    private void handleDeleteButtonAction() {
        if (selectedCouncilor != null) {
            councilorRepository.delete(selectedCouncilor);
            councilorData.remove(selectedCouncilor);
            System.out.println("Deleted councilor: " + selectedCouncilor.getFirstName() + " " + selectedCouncilor.getLastName());
            clearForm();
        }
    }

    @FXML
    private void handleClearButtonAction() {
        clearForm();
    }

    private void clearForm() {
        lastNameField.clear();
        firstNameField.clear();
        councilListComboBox.setValue(null); // Clear ComboBox
        isTitularCheckBox.setSelected(false);
        selectedCouncilor = null;
        councilorTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        lastNameField.requestFocus();
    }

    private boolean validateInput() {
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();
        // CouncilList councilList = councilListComboBox.getValue(); // listName is now councilList

        if (lastName == null || lastName.trim().isEmpty()) {
            showErrorAlert("Validation Error", "Last Name cannot be empty.");
            lastNameField.requestFocus();
            return false;
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            showErrorAlert("Validation Error", "First Name cannot be empty.");
            firstNameField.requestFocus();
            return false;
        }
        // Validation for ComboBox (e.g., if a list selection is mandatory)
        if (councilListComboBox.getValue() == null) {
            showErrorAlert("Validation Error", "Council List selection is required.");
            councilListComboBox.requestFocus();
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

    // Optional: For successful operations
    private void showInformationAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
