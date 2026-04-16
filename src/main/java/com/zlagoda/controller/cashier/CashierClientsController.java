package com.zlagoda.controller.cashier;

import com.mysql.cj.xdevapi.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class CashierClientsController {
    @FXML private TableView<Client> clientsTable;
    @FXML private Button addClientButton;
    @FXML private VBox editClientBox;
    @FXML private Button cancelEditButton;
    @FXML private Button saveClientButton;

    // Поля редагування
    @FXML private TextField cardNumberField;
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField patronymicField;
    @FXML private TextField phoneField;
    @FXML private TextField cityField;
    @FXML private TextField streetField;
    @FXML private TextField zipField;
    @FXML private TextField discountPercentField;

    @FXML
    public void initialize() {

        cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cardNumberField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        hideEditBox();

        addClientButton.setOnAction(event -> showEditBox(null));

        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showEditBox(newVal);
            }
        });

        cancelEditButton.setOnAction(event -> hideEditBox());

        saveClientButton.setOnAction(event -> {
            System.out.println("Збереження клієнта...");
            hideEditBox();
        });
    }
    private void clearFields() {
        cardNumberField.clear();
        lastNameField.clear();
        firstNameField.clear();
        patronymicField.clear();
        phoneField.clear();
        cityField.clear();
        streetField.clear();
        zipField.clear();
        discountPercentField.clear();

    }

    private void showEditBox(Client client) {
        if (client == null) {
            lastNameField.clear();
            firstNameField.clear();
        }

        editClientBox.setVisible(true);
        editClientBox.setManaged(true);
        addClientButton.setVisible(false);
    }

    private void hideEditBox() {
        editClientBox.setVisible(false);
        editClientBox.setManaged(false);
        addClientButton.setVisible(true);
        clientsTable.getSelectionModel().clearSelection();
        clearFields();
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        System.out.println("Клік по профілю касира");
    }
}
