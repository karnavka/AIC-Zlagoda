package com.zlagoda.controller.cashier;

import com.zlagoda.dao.Customer_CardDAO;
import com.zlagoda.model.Customer_Card;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class CashierClientsController {

    @FXML private TextField lastNameSearchField;
    @FXML private Button searchButton;
    @FXML private Button clearButton;

    @FXML private TableView<Customer_Card> clientsTable;
    @FXML private TableColumn<Customer_Card, String> cardNumberColumn;
    @FXML private TableColumn<Customer_Card, String> surnameColumn;
    @FXML private TableColumn<Customer_Card, String> nameColumn;
    @FXML private TableColumn<Customer_Card, String> patronymicColumn;
    @FXML private TableColumn<Customer_Card, String> phoneColumn;
    @FXML private TableColumn<Customer_Card, String> cityColumn;
    @FXML private TableColumn<Customer_Card, String> streetColumn;
    @FXML private TableColumn<Customer_Card, String> zipColumn;
    @FXML private TableColumn<Customer_Card, Integer> percentColumn;

    @FXML private Button addClientButton;
    @FXML private VBox editClientBox;
    @FXML private Button cancelEditButton;
    @FXML private Button saveClientButton;

    @FXML private TextField cardNumberField, lastNameField, firstNameField, patronymicField;
    @FXML private TextField phoneField, cityField, streetField, zipField, discountPercentField;

    private final Customer_CardDAO customerDAO = new Customer_CardDAO();
    private final ObservableList<Customer_Card> clientList = FXCollections.observableArrayList();

    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupTable();
        loadClients();

        searchButton.setOnAction(event -> handleSearch());

        clearButton.setOnAction(event -> {
            lastNameSearchField.clear();
            loadClients();
        });

        cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cardNumberField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (phoneField.getText().length() > 9) {
                String s = phoneField.getText().substring(0, 9);
                phoneField.setText(s);
            }
        });

        discountPercentField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                discountPercentField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        addClientButton.setOnAction(event -> showEditBox(null));
        cancelEditButton.setOnAction(event -> hideEditBox());
        saveClientButton.setOnAction(event -> handleSave());

        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showEditBox(newVal);
            }
        });

        hideEditBox();
    }

    private void setupTable() {
        cardNumberColumn.setCellValueFactory(new PropertyValueFactory<>("card_number"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
        zipColumn.setCellValueFactory(new PropertyValueFactory<>("zip_code"));
        percentColumn.setCellValueFactory(new PropertyValueFactory<>("percent"));
    }

    private void loadClients() {
        try {
            clientList.setAll(customerDAO.getAllCustomerCardsOrderBySurname());
            clientsTable.setItems(clientList);
        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    private void handleSearch() {
        String surname = lastNameSearchField.getText().trim();
        try {
            List<Customer_Card> results;
            if (surname.isEmpty()) {
                results = customerDAO.getAllCustomerCardsOrderBySurname();
            } else {
                results = customerDAO.searchCustomerCardsBySurname(surname);
            }
            clientList.setAll(results);
            clientsTable.setItems(clientList);
        } catch (SQLException e) {
            showAlert("Помилка пошуку", "Не вдалося виконати запит до бази: " + e.getMessage());
        }
    }

    private void handleSave() {
        try {
            Customer_Card card = new Customer_Card();
            card.setCard_number(cardNumberField.getText());
            card.setSurname(lastNameField.getText());
            card.setName(firstNameField.getText());
            card.setPatronymic(patronymicField.getText());
            card.setPhone_number("+380" + phoneField.getText());
            card.setCity(cityField.getText());
            card.setStreet(streetField.getText());
            card.setZip_code(zipField.getText());
            card.setPercent(Integer.parseInt(discountPercentField.getText()));

            if (isEditMode) {
                customerDAO.updateCustomerCard(card);
            } else {
                customerDAO.addCustomerCard(card);
            }

            loadClients();
            hideEditBox();
        } catch (Exception e) {
            showAlert("Помилка збереження", "Перевірте правильність заповнення полів!");
        }
    }

    private void showEditBox(Customer_Card client) {
        if (client == null) {
            isEditMode = false;
            clearFields();
            cardNumberField.setEditable(true);
        } else {
            isEditMode = true;
            cardNumberField.setText(client.getCard_number());
            cardNumberField.setEditable(false);
            lastNameField.setText(client.getSurname());
            firstNameField.setText(client.getName());
            patronymicField.setText(client.getPatronymic());
            phoneField.setText(client.getPhone_number().replace("+380", ""));
            cityField.setText(client.getCity());
            streetField.setText(client.getStreet());
            zipField.setText(client.getZip_code());
            discountPercentField.setText(String.valueOf(client.getPercent()));
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}