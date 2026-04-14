package com.zlagoda.controller.cashier;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class CashierSalesController {

    @FXML
    private TextField upcField;

    @FXML
    private TableView<?> table;

    @FXML
    private Label totalLabel;

    @FXML
    private Label discountSumLabel;

    @FXML
    private Label toPayLabel;

    @FXML
    private void onAddProduct() {
        System.out.println("Додати товар: " + upcField.getText());
    }

    @FXML
    private void onCancel() {
        table.getItems().clear();
    }

    @FXML
    private void onCreate() {
        System.out.println("Чек створено");
    }
}