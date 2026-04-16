package com.zlagoda.controller.cashier;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class CashierChecksController {

    @FXML
    private TextField searchCheckNumberField;

    @FXML
    private Pane checkDetailsBox;



    @FXML
    private void handleCloseDetails() {
        checkDetailsBox.setVisible(false);
        checkDetailsBox.setManaged(false);
        searchCheckNumberField.clear();
    }
}