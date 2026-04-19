package com.zlagoda.controller;

import com.zlagoda.controller.cashier.CashierChecksController;
import com.zlagoda.controller.cashier.CashierSalesController;
import com.zlagoda.dao.EmployeeDAO;
import com.zlagoda.model.Employee;
import com.zlagoda.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CashierMainController {
    private final ContextMenu profileMenu = new ContextMenu();

    @FXML
    private Label employeeNameLabel;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private User currentUser;
    private Employee currentEmployee;

    @FXML
    private CashierChecksController checksIncludeController;
    @FXML
    private CashierSalesController salesIncludeController;
    @FXML
    public void initialize() {
        profileMenu.getStyleClass().add("minimal-profile-menu");
        profileMenu.setAutoHide(true);
    }

    public void initData(User user) {
        this.currentUser = user;
        if (checksIncludeController != null) {
            checksIncludeController.initData(user);
            salesIncludeController.initData(user);
        }
        try {
            this.currentEmployee = employeeDAO.getEmployeeById(user.getEmployeeId());

            if (currentEmployee != null) {
                String fullName = currentEmployee.getSurname() + " " + currentEmployee.getName();
                employeeNameLabel.setText(fullName);

                MenuItem infoItem = new MenuItem("Касир: " + fullName);
                infoItem.setDisable(true);
                infoItem.setStyle("-fx-opacity: 1.0; -fx-font-weight: bold; -fx-padding: 10 20 5 20; -fx-text-fill: #2c3e50;");

                MenuItem detailsItem = new MenuItem("ℹ Детальна інформація");
                detailsItem.setOnAction(e -> showEmployeeDetails());

                MenuItem logoutItem = new MenuItem("🚪 Вийти");
                logoutItem.setOnAction(e -> handleLogout());

                profileMenu.getItems().setAll(infoItem, detailsItem, new SeparatorMenuItem(), logoutItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showEmployeeDetails() {
        if (currentEmployee == null) return;

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Профіль працівника");
        alert.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(12);
        grid.setPadding(new Insets(25));

        // Використовуємо твої правильні геттери
        addInfoRow(grid, 0, "ID КАСИРА:", currentEmployee.getId_employee());
        addInfoRow(grid, 1, "ПРІЗВИЩЕ:", currentEmployee.getSurname());
        addInfoRow(grid, 2, "ІМ'Я:", currentEmployee.getName());
        addInfoRow(grid, 3, "ПО БАТЬКОВІ:", currentEmployee.getPatronymic() != null ? currentEmployee.getPatronymic() : "-");

        Label roleLabel = new Label("ПОСАДА:");
        roleLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-weight: bold; -fx-font-size: 10px;");
        Label roleValue = new Label("КАСИР");
        roleValue.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(roleLabel, 0, 4);
        grid.add(roleValue, 1, 4);

        addInfoRow(grid, 5, "ЗАРПЛАТА:", String.format("%.2f грн", currentEmployee.getSalary()));
        addInfoRow(grid, 6, "ТЕЛЕФОН:", currentEmployee.getPhone_number());
        addInfoRow(grid, 7, "ДАТА НАРОДЖЕННЯ:", currentEmployee.getDate_of_birth().toString());
        addInfoRow(grid, 8, "ДАТА ПОЧАТКУ:", currentEmployee.getDate_of_start().toString());
        addInfoRow(grid, 9, "АДРЕСА:", String.format("м. %s, вул. %s", currentEmployee.getCity(), currentEmployee.getStreet()));
        addInfoRow(grid, 10, "ІНДЕКС:", currentEmployee.getZip_code());

        alert.getDialogPane().setContent(grid);
        alert.getDialogPane().setStyle("-fx-background-color: white;");
        alert.getDialogPane().setMinWidth(480);

        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");

        alert.showAndWait();
    }

    private void addInfoRow(GridPane grid, int row, String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #95a5a6; -fx-font-weight: bold; -fx-font-size: 10px;");

        Label value = new Label(valueText);
        value.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px; -fx-font-weight: 500;");

        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        Node source = (Node) event.getSource();
        if (profileMenu.isShowing()) {
            profileMenu.hide();
        } else {
            profileMenu.show(source, event.getScreenX() - 120, event.getScreenY() + 10);
        }
    }

    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) employeeNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}