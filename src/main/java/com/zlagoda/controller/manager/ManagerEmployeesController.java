package com.zlagoda.controller.manager;

import com.zlagoda.dao.EmployeeDAO;
import com.zlagoda.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class ManagerEmployeesController {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    public ComboBox roleSearchComboBox;

    private Employee selectedEmployee;
    private boolean editMode = false;

    @FXML private TextField lastNameSearchField;
    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, String> idColumn;
    @FXML private TableColumn<Employee, String> surnameColumn;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> roleColumn;
    @FXML private TableColumn<Employee, String> phoneColumn;
    @FXML private TableColumn<Employee, Double> salaryColumn;

    @FXML private VBox editEmployeeBox;

    @FXML private TextField employeeIdField;
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField patronymicField;
    @FXML private TextField phoneField;
    @FXML private TextField salaryField;
    @FXML private TextField cityField;
    @FXML private TextField streetField;
    @FXML private TextField zipField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private DatePicker startDatePicker;

    @FXML private Button deleteEmployeeButton;

    @FXML
    public void initialize() {
        setupTable();
        setupRoleComboBox();
        loadAllEmployees();
        hideEditBox();

        employeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedEmployee = newVal;
                editMode = true;
                fillForm(newVal);
                showEditBox();
            }
        });
        roleSearchComboBox.setValue("Всі ролі");
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_employee"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        employeesTable.setItems(employeeList);
    }

    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList("cashier", "manager"));
    }

    private void loadAllEmployees() {
        try {
            List<Employee> employees = employeeDAO.getAllEmployeesOrderBySurname();
            employeeList.setAll(employees);
        } catch (SQLException e) {
            showError("Помилка БД", e.getMessage());
        }
    }

    @FXML
    private void searchEmployee() {
        String surname = lastNameSearchField.getText();
        String role = roleSearchComboBox.getValue().toString();

        try {
            List<Employee> employees = employeeDAO.searchEmployees(surname, role);
            employeesTable.setItems(FXCollections.observableArrayList(employees));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clearEmployees(ActionEvent actionEvent) {
        lastNameSearchField.clear();

        if (roleSearchComboBox != null) {
            roleSearchComboBox.getSelectionModel().select("Всі ролі");
        }

        employeesTable.getSelectionModel().clearSelection();
        selectedEmployee = null;
        editMode = false;
        clearForm();
        hideEditBox();
        searchEmployee();
    }

    @FXML
    public void addEmployee(ActionEvent actionEvent) {
        employeesTable.getSelectionModel().clearSelection();
        selectedEmployee = null;
        editMode = false;
        clearForm();
        showEditBox();
    }

    @FXML
    public void cancelEdit(ActionEvent actionEvent) {
        employeesTable.getSelectionModel().clearSelection();
        selectedEmployee = null;
        editMode = false;
        clearForm();
        hideEditBox();
    }

    @FXML
    public void deleteEmployee(ActionEvent actionEvent) {
        if (selectedEmployee == null) {
            showError("Помилка", "Спочатку оберіть працівника.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Підтвердження");
        confirm.setHeaderText("Видалення працівника");
        confirm.setContentText("Ви точно хочете видалити працівника " +
                selectedEmployee.getSurname() + " " + selectedEmployee.getName() + "?");

        ButtonType result = confirm.showAndWait().orElse(ButtonType.CANCEL);
        if (result != ButtonType.OK) {
            return;
        }

        try {
            employeeDAO.deleteEmployee(selectedEmployee.getId_employee());
            employeeList.remove(selectedEmployee);

            selectedEmployee = null;
            editMode = false;
            clearForm();
            hideEditBox();

        } catch (SQLException e) {
            showError("Помилка БД", e.getMessage());
        }
    }

    @FXML
    public void saveEmployee(ActionEvent actionEvent) {
        if (!validateForm()) {
            return;
        }

        try {
            Employee employee = buildEmployeeFromForm();

            if (editMode) {
                employeeDAO.updateEmployee(employee);
                showInfo("Успіх", "Працівника оновлено.");
            } else {
                employeeDAO.addEmployee(employee);
                showInfo("Успіх", "Працівника додано.");
            }

            loadAllEmployees();
            clearForm();
            hideEditBox();
            selectedEmployee = null;
            editMode = false;

        } catch (SQLException e) {
            showError("Помилка БД", e.getMessage());
        }
    }

    private Employee buildEmployeeFromForm() {
        Employee employee = new Employee();

        employee.setId_employee(employeeIdField.getText().trim());
        employee.setSurname(lastNameField.getText().trim());
        employee.setName(firstNameField.getText().trim());
        employee.setPatronymic(emptyToNull(patronymicField.getText()));
        employee.setRole(roleComboBox.getValue());
        employee.setSalary(Double.parseDouble(salaryField.getText().trim()));
        employee.setDate_of_birth(birthDatePicker.getValue());
        employee.setDate_of_start(startDatePicker.getValue());
        employee.setPhone_number(phoneField.getText().trim());
        employee.setCity(cityField.getText().trim());
        employee.setStreet(streetField.getText().trim());
        employee.setZip_code(zipField.getText().trim());

        return employee;
    }

    private boolean validateForm() {
        if (employeeIdField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                firstNameField.getText().trim().isEmpty() ||
                roleComboBox.getValue() == null ||
                salaryField.getText().trim().isEmpty() ||
                birthDatePicker.getValue() == null ||
                startDatePicker.getValue() == null ||
                phoneField.getText().trim().isEmpty() ||
                cityField.getText().trim().isEmpty() ||
                streetField.getText().trim().isEmpty() ||
                zipField.getText().trim().isEmpty()) {

            showError("Помилка", "Заповніть усі обов’язкові поля.");
            return false;
        }

        try {
            Double.parseDouble(salaryField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Помилка", "Зарплата має бути числом.");
            return false;
        }

        return true;
    }

    private void fillForm(Employee employee) {
        employeeIdField.setText(employee.getId_employee());
        lastNameField.setText(employee.getSurname());
        firstNameField.setText(employee.getName());
        patronymicField.setText(employee.getPatronymic());
        roleComboBox.setValue(employee.getRole());
        phoneField.setText(employee.getPhone_number());
        salaryField.setText(String.valueOf(employee.getSalary()));
        cityField.setText(employee.getCity());
        streetField.setText(employee.getStreet());
        zipField.setText(employee.getZip_code());
        birthDatePicker.setValue(employee.getDate_of_birth());
        startDatePicker.setValue(employee.getDate_of_start());

        employeeIdField.setDisable(true);
    }

    private void clearForm() {
        employeeIdField.clear();
        employeeIdField.setDisable(false);
        lastNameField.clear();
        firstNameField.clear();
        patronymicField.clear();
        roleComboBox.setValue(null);
        phoneField.clear();
        salaryField.clear();
        cityField.clear();
        streetField.clear();
        zipField.clear();
        birthDatePicker.setValue(null);
        startDatePicker.setValue(null);
    }

    private void showEditBox() {
        editEmployeeBox.setManaged(true);
        editEmployeeBox.setVisible(true);

        if (deleteEmployeeButton != null) {
            deleteEmployeeButton.setVisible(editMode);
            deleteEmployeeButton.setManaged(editMode);
        }
    }

    private void hideEditBox() {
        editEmployeeBox.setManaged(false);
        editEmployeeBox.setVisible(false);
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}