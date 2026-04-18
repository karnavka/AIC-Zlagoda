package com.zlagoda.controller;

import com.zlagoda.dao.EmployeeDAO;
import com.zlagoda.model.Employee;
import com.zlagoda.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ManagerMainController {
    private final ContextMenu profileMenu = new ContextMenu();

    @FXML
    private Label employeeNameLabel;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private User currentUser;
    private Employee currentEmployee;

    @FXML
    public void initialize() {
        profileMenu.setAutoHide(true);
    }

    public void initData(User user) {
        this.currentUser = user;
        try {
            this.currentEmployee = employeeDAO.getEmployeeById(user.getEmployeeId());

            if (currentEmployee != null) {
                String fullName = currentEmployee.getSurname() + " " + currentEmployee.getName();
                employeeNameLabel.setText(fullName);

                MenuItem infoItem = new MenuItem("Мій профіль: " + fullName);
                MenuItem settingsItem = new MenuItem("Налаштування");
                MenuItem logoutItem = new MenuItem("Вийти з системи");

                logoutItem.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                        Parent root = loader.load();

                        Stage stage = (Stage) ((MenuItem) e.getSource()).getParentPopup().getOwnerWindow();
                        stage.setScene(new Scene(root));
                        stage.centerOnScreen();
                        stage.show();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                profileMenu.getItems().setAll(infoItem, settingsItem, new SeparatorMenuItem(), logoutItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        if (profileMenu.isShowing()) {
            profileMenu.hide();
        } else {
            Node source = (Node) event.getSource();
            profileMenu.show(source, event.getScreenX(), event.getScreenY());
        }
    }
}
