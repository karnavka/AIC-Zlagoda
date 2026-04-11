package com.zlagoda.controller;

import com.zlagoda.MainApp;
import com.zlagoda.dao.UserDAO;
import com.zlagoda.model.User;
import com.zlagoda.util.PassWordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Enter login and password");
            return;
        }

        try {
            User user = userDAO.findByUsername(login);
            System.out.println(user);
            if (user == null) {
                errorLabel.setText("Invalid login or password");
                return;
            }

            boolean passwordMatches = PassWordUtil.verifyPassword(password, user.getPasswordHash());

            if (!passwordMatches) {
                errorLabel.setText("Invalid login or password");
                return;
            }

            switch (user.getRole()) {
                case "MANAGER" -> openScene(event, "/fxml/manager-main.fxml", "Manager");
                case "CASHIER" -> openScene(event, "/fxml/cashier-main.fxml", "Cashier");
                default -> errorLabel.setText("Unknown user role");
            }

        } catch (SQLException e) {
            errorLabel.setText("Database error");
            e.printStackTrace();
        } catch (IOException e) {
            errorLabel.setText("Cannot open window");
            e.printStackTrace();
        }
    }

    private void openScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 1100, 700);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}