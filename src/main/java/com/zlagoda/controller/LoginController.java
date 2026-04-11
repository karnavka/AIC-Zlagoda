package com.zlagoda.controller;

import com.zlagoda.MainApp;
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

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String login = loginField.getText();
        String password = passwordField.getText();

        if ("manager".equals(login) && "1234".equals(password)) {
            openScene(event, "/fxml/manager-main.fxml", "Manager");
        } else if ("cashier".equals(login) && "1234".equals(password)) {
            openScene(event, "/fxml/cashier-main.fxml", "Cashier");
        } else {
            errorLabel.setText("Invalid login or password");
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