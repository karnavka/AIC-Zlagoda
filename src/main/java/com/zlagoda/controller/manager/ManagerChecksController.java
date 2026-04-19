package com.zlagoda.controller.manager;

import com.zlagoda.dao.CheckDAO;
import com.zlagoda.dao.EmployeeDAO;
import com.zlagoda.dto.CheckDetailsDTO;
import com.zlagoda.model.Check;
import com.zlagoda.model.Employee;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ManagerChecksController {

    private final CheckDAO checkDAO = new CheckDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private final ObservableList<Check> checkList = FXCollections.observableArrayList();
    private final ObservableList<CheckDetailsDTO> checkItemsList = FXCollections.observableArrayList();

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<Employee> cashierComboBox;
    @FXML
    private TableView<Check> checksTable;
    @FXML
    private TableColumn<Check, String> numberColumn;
    @FXML
    private TableColumn<Check, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<Check, String> cashierColumn;
    @FXML
    private TableColumn<Check, Double> totalColumn;
    @FXML
    private Label totalSumLabel;
    @FXML
    private VBox detailsBox;
    @FXML
    private TableView<CheckDetailsDTO> checkItemsTable;
    @FXML
    private TableColumn<CheckDetailsDTO, String> itemNameColumn;
    @FXML
    private TableColumn<CheckDetailsDTO, Integer> itemQuantityColumn;
    @FXML
    private TableColumn<CheckDetailsDTO, Double> itemPriceColumn;

    @FXML
    public void initialize() {
        setupChecksTable();
        setupDetailsTable();
        loadCashiers();
        hideDetails();

        checksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showCheckDetails(newVal);
            } else {
                hideDetails();
            }
        });
    }

    private void setupChecksTable() {
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("check_number"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("print_date"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("sum_total"));

        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString().replace('T', ' '));
                }
            }
        });

        cashierColumn.setCellValueFactory(cellData -> {
            Check check = cellData.getValue();
            try {
                Employee cashier = employeeDAO.getEmployeeById(check.getId_employee());
                if (cashier != null) {
                    return new SimpleStringProperty(cashier.getSurname() + " " + cashier.getName());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty(check.getId_employee());
        });

        checksTable.setItems(checkList);
    }

    private void setupDetailsTable() {
        itemNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        itemQuantityColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getProduct_number()));

        itemPriceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getSelling_price()));

        checkItemsTable.setItems(checkItemsList);
    }

    private void loadCashiers() {
        try {
            cashierComboBox.getItems().clear();
            cashierComboBox.getItems().add(null);

            // якщо в БД role зберігається як "cashier", залиш так
            // якщо як "Cashier" або "КАСИР" — підстав свій варіант
            cashierComboBox.getItems().addAll(employeeDAO.getEmployeesByRole("cashier"));

            cashierComboBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(Employee item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else if (item == null) {
                        setText("Усі (касири)");
                    } else {
                        setText(item.getSurname() + " " + item.getName());
                    }
                }
            });

            cashierComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Employee item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else if (item == null) {
                        setText("Усі (касири)");
                    } else {
                        setText(item.getSurname() + " " + item.getName());
                    }
                }
            });

            cashierComboBox.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    @FXML
    public void searchCheck(ActionEvent actionEvent) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showAlert("Помилка", "Оберіть початкову і кінцеву дату.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showAlert("Помилка", "Кінцева дата не може бути раніше початкової.");
            return;
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        Employee selectedCashier = cashierComboBox.getValue();

        try {
            List<Check> results;
            double total;

            if (selectedCashier == null) {
                results = checkDAO.getChecksByPeriod(start, end);
                total = checkDAO.getCheckSumByPeriod(start, end);
            } else {
                results = checkDAO.getChecksByEmployeeAndPeriod(
                        selectedCashier.getId_employee(), start, end
                );
                total = checkDAO.getCheckSumByEmployeeAndPeriod(
                        selectedCashier.getId_employee(), start, end
                );
            }

            checkList.setAll(results);
            totalSumLabel.setText(String.format("%.2f", total));
            hideDetails();

        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    private void showCheckDetails(Check check) {
        try {
            List<CheckDetailsDTO> details = checkDAO.getCheckDetails(check.getCheck_number());
            checkItemsList.setAll(details);
            detailsBox.setVisible(true);
            detailsBox.setManaged(true);
        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    private void hideDetails() {
        checkItemsList.clear();
        detailsBox.setVisible(false);
        detailsBox.setManaged(false);
    }

    @FXML
    public void deleteCheck(ActionEvent actionEvent) {
        Check selectedCheck = checksTable.getSelectionModel().getSelectedItem();

        if (selectedCheck == null) {
            showAlert("Помилка", "Спочатку оберіть чек.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Підтвердження");
        confirm.setHeaderText("Видалення чеку");
        confirm.setContentText("Ви точно хочете видалити чек " + selectedCheck.getCheck_number() + "?");

        ButtonType result = confirm.showAndWait().orElse(ButtonType.CANCEL);
        if (result != ButtonType.OK) {
            return;
        }

        try {
            checkDAO.deleteCheck(selectedCheck.getCheck_number());
            checkList.remove(selectedCheck);
            hideDetails();

            double newTotal = 0.0;
            for (Check check : checkList) {
                newTotal += check.getSum_total();
            }
            totalSumLabel.setText(String.format("%.2f", newTotal));

        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}