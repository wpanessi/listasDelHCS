package com.example.myjavafxapp.controller;

import com.example.myjavafxapp.model.CouncilList;
import com.example.myjavafxapp.repository.CouncilListRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouncilListController {

    @FXML
    private TableView<CouncilList> councilListTable;
    @FXML
    private TableColumn<CouncilList, String> listNumberColumn;
    @FXML
    private TableColumn<CouncilList, String> cloisterColumn;

    @FXML
    private TextField listNumberField;
    @FXML
    private TextField cloisterField;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final CouncilListRepository councilListRepository;
    private final ObservableList<CouncilList> councilListData = FXCollections.observableArrayList();
    private CouncilList selectedCouncilList = null;

    @Autowired
    public CouncilListController(CouncilListRepository councilListRepository) {
        this.councilListRepository = councilListRepository;
    }

    @FXML
    public void initialize() {
        councilListData.addAll(councilListRepository.findAll());
        System.out.println("CouncilLists loaded from DB: " + councilListData.size());

        listNumberColumn.setCellValueFactory(new PropertyValueFactory<>("listNumber"));
        cloisterColumn.setCellValueFactory(new PropertyValueFactory<>("cloister"));

        councilListTable.setItems(councilListData);

        councilListTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedCouncilList = newValue;
                    populateForm(newValue);
                }
        );

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void populateForm(CouncilList councilList) {
        if (councilList != null) {
            listNumberField.setText(councilList.getListNumber());
            cloisterField.setText(councilList.getCloister());
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
        } else {
            clearForm();
        }
    }

    @FXML
    private void handleAddButtonAction() {
        if (validateInput()) {
            CouncilList newList = new CouncilList(
                    listNumberField.getText(),
                    cloisterField.getText()
            );
            CouncilList savedList = councilListRepository.save(newList);
            councilListData.add(savedList);
            System.out.println("Added CouncilList: " + savedList.getListNumber() + " with ID: " + savedList.getId());
            clearForm();
        }
    }

    @FXML
    private void handleUpdateButtonAction() {
        if (selectedCouncilList != null && validateInput()) {
            selectedCouncilList.setListNumber(listNumberField.getText());
            selectedCouncilList.setCloister(cloisterField.getText());
            CouncilList updatedList = councilListRepository.save(selectedCouncilList);
            int index = councilListData.indexOf(selectedCouncilList);
            if (index != -1) {
                councilListData.set(index, updatedList);
            } else {
                councilListTable.refresh();
            }
            System.out.println("Updated CouncilList: " + updatedList.getListNumber());
            clearForm();
        }
    }

    @FXML
    private void handleDeleteButtonAction() {
        if (selectedCouncilList != null) {
            councilListRepository.delete(selectedCouncilList);
            councilListData.remove(selectedCouncilList);
            System.out.println("Deleted CouncilList: " + selectedCouncilList.getListNumber());
            clearForm();
        }
    }

    @FXML
    private void handleClearButtonAction() {
        clearForm();
    }

    private void clearForm() {
        listNumberField.clear();
        cloisterField.clear();
        selectedCouncilList = null;
        councilListTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        listNumberField.requestFocus();
    }

    private boolean validateInput() {
        if (listNumberField.getText() == null || listNumberField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "List Number cannot be empty.");
            listNumberField.requestFocus();
            return false;
        }
        if (cloisterField.getText() == null || cloisterField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "Cloister cannot be empty.");
            cloisterField.requestFocus();
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
