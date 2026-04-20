package com.zlagoda.controller.manager;

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

public class ManagerClientsController {

    @FXML private TextField lastNameSearchField;
    @FXML private TextField discountSearchField;
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
    @FXML private Button deleteClientButton;
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

        searchButton.setOnAction(e -> handleSearch());

        clearButton.setOnAction(e -> {
            lastNameSearchField.clear();
            discountSearchField.clear();
            loadClients();
        });

        addClientButton.setOnAction(e -> showEditBox(null));

        deleteClientButton.setOnAction(e -> handleDelete());
        cancelEditButton.setOnAction(e -> hideEditBox());
        saveClientButton.setOnAction(e -> handleSave());

        phoneField.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) {
                phoneField.setText(newV.replaceAll("[^\\d]", ""));
            }
            if (phoneField.getText().length() > 9) {
                phoneField.setText(phoneField.getText().substring(0, 9));
            }
        });

        discountPercentField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("\\d*")) {
                discountPercentField.setText(n.replaceAll("[^\\d]", ""));
            }
        });

        discountSearchField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("\\d*")) {
                discountSearchField.setText(n.replaceAll("[^\\d]", ""));
            }
        });

        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) showEditBox(n);
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
        String surname = lastNameSearchField.getText().trim().toLowerCase();
        String percentText = discountSearchField.getText().trim();

        try {
            Integer percent = null;
            if (!percentText.isEmpty()) {
                percent = Integer.parseInt(percentText);
            }

            List<Customer_Card> all = customerDAO.getAllCustomerCardsOrderBySurname();

            Integer finalPercent = percent;
            List<Customer_Card> filtered = all.stream()
                    .filter(c -> surname.isEmpty() || c.getSurname().toLowerCase().startsWith(surname))
                    .filter(c -> finalPercent == null || c.getPercent() == finalPercent)
                    .toList();

            clientList.setAll(filtered);

        } catch (Exception e) {
            showAlert("Помилка пошуку", "Некоректний відсоток");
        }
    }

    private void handleDelete() {
        String cardNumber = cardNumberField.getText().trim();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Підтвердження");
        confirm.setHeaderText("Видалити клієнта?");
        confirm.setContentText("Карта №: " + cardNumber);

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                customerDAO.deleteCustomerCard(cardNumber);
                loadClients();
                hideEditBox();
            } catch (SQLException e) {
                showAlert("Помилка видалення", e.getMessage());
            }
        }
    }

    private void handleSave() {
        String cardNumber = cardNumberField.getText().trim();
        String surname = lastNameField.getText().trim();
        String name = firstNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (cardNumber.isEmpty() || surname.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            showAlert("Помилка", "Обов'язкові поля: номер карти, прізвище, ім'я, телефон");
            return;
        }

        try {
            Customer_Card card = new Customer_Card();

            card.setCard_number(cardNumber);
            card.setSurname(surname);
            card.setName(name);
            card.setPatronymic(patronymicField.getText().trim());
            card.setPhone_number("+380" + phone);
            card.setCity(cityField.getText().trim());
            card.setStreet(streetField.getText().trim());
            card.setZip_code(zipField.getText().trim());

            String percentText = discountPercentField.getText().trim();
            card.setPercent(percentText.isEmpty() ? 0 : Integer.parseInt(percentText));

            if (isEditMode) {
                customerDAO.updateCustomerCard(card);
            } else {
                customerDAO.addCustomerCard(card);
            }

            loadClients();
            hideEditBox();

        } catch (Exception e) {
            showAlert("Помилка збереження", e.getMessage());
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

        deleteClientButton.setVisible(isEditMode);
        deleteClientButton.setManaged(isEditMode);

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

    private void showAlert(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(text);
        alert.showAndWait();
    }
}