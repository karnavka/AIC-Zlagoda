package com.zlagoda.controller.cashier;

import com.zlagoda.dao.CheckDAO;
import com.zlagoda.dao.Customer_CardDAO;
import com.zlagoda.model.Check;
import com.zlagoda.model.Customer_Card;
import com.zlagoda.model.Employee;
import com.zlagoda.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CashierChecksController {
    private final CheckDAO checkDAO = new CheckDAO();
    private final ObservableList<Check> checkList = FXCollections.observableArrayList();


    @FXML
    private TextField searchCheckNumberField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button searchByDateButton;
    @FXML
    private Button todayChecksButton;

    @FXML
    private TableView<Check> checksTable;
    @FXML
    private TableColumn<Check, String> checkNumberColumn;
    @FXML
    private TableColumn<Check, String> checkDateColumn;
    @FXML
    private TableColumn<Check, Double> totalSumColumn;
    @FXML
    private Pane checkDetailsBox;

    private User currentUser;
    private Employee currentEmployee;

    @FXML
    public void initialize() {
        setupTable();
    }

    public void initData(User user) {
        this.currentUser = user;
        loadChecks();
    }

    private void setupTable() {
        checkNumberColumn.setCellValueFactory(new PropertyValueFactory<>("check_number"));
        checkDateColumn.setCellValueFactory(new PropertyValueFactory<>("print_date"));
        totalSumColumn.setCellValueFactory(new PropertyValueFactory<>("sum_total"));
    }

    private void loadChecks() {
        if (currentUser == null) {
            System.out.println("returning empty checks");
            return;
        }

        try {
            checkList.setAll(checkDAO.getTodayChecksByEmployee(currentUser.getEmployeeId()));
            checksTable.setItems(checkList);
            System.out.println("printing checks");
            System.out.println(currentUser.getEmployeeId());
            System.out.println(checkList.size());
        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }


    public void searchByDate(ActionEvent actionEvent) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate == null || endDate == null) {
            showAlert("Ви не ввели дату", "Якщо дати не вибрані, відображаються лише сьогоднішні чеки");
            return;
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        try {
            List<Check> results = checkDAO.getChecksByEmployeeAndPeriod(currentUser.getEmployeeId(), start, end);
            checkList.setAll(results);
            checksTable.setItems(checkList);
        } catch (SQLException e) {
            showAlert("Помилка пошуку", "Не вдалося виконати запит до бази: " + e.getMessage());
        }
    }


    @FXML
    private void handleCloseDetails() {
        checkDetailsBox.setVisible(false);
        checkDetailsBox.setManaged(false);
        searchCheckNumberField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}