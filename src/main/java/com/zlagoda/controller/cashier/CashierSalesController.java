package com.zlagoda.controller.cashier;

import com.zlagoda.dao.CheckDAO;
import com.zlagoda.dao.EmployeeDAO;
import com.zlagoda.dao.ProductDAO;
import com.zlagoda.dao.Store_ProductDAO;
import com.zlagoda.dto.CheckDetailsDTO;
import com.zlagoda.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class CashierSalesController {
    private User currentUser;
    private Employee currentEmployee;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final Store_ProductDAO storeProductDAO = new Store_ProductDAO();
    private final ObservableList<CheckDetailsDTO> productDetails  = FXCollections.observableArrayList();
   // private final ObservableList<Product> productList = FXCollections.observableArrayList();

    public TextField upcInputField;
    public Button addButton;
    public Label checkNumberLabel;
    @FXML
    private TableView<CheckDetailsDTO>  salesTable;
    @FXML
    private TableColumn<CheckDetailsDTO, String>  numberColumn;
    @FXML
    private TableColumn<CheckDetailsDTO, String>  upcColumn;
    @FXML
    private TableColumn<CheckDetailsDTO, String>  nameColumn;
    @FXML
    private TableColumn<CheckDetailsDTO, Integer>  quantityColumn;
    @FXML
    private TableColumn<CheckDetailsDTO, Double>  priceColumn;
    @FXML
    private TableColumn<CheckDetailsDTO, Double>  sumColumn;

    public TextField clientCardField;
    public Label discountPercentLabel;
    public Label totalRawLabel;
    public Label discountAmountLabel;
    public Label totalFinalLabel;
    public Label vatLabel;
    public Button cancelButton;
    public Button createButton;
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
    public void initialize() {
        setupTable();
    }
    public void initData(User user) {
        this.currentUser = user;
        try {
            this.currentEmployee = employeeDAO.getEmployeeById(user.getEmployeeId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private void setupTable() {
        numberColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        upcColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUPC()));

        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        quantityColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProduct_number()));

        priceColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSelling_price()));

        sumColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getProduct_number() * cellData.getValue().getSelling_price()
                ));
    }


    @FXML
    private void searchProductByUPC() {
        String productUPC = upcInputField.getText().trim();
System.out.println("Searching for UPC");
        if (productUPC.isEmpty()) {
            showAlert("Помилка", "Введіть UPC товару.");
            return;
        }
        try {
            Store_Product storeProduct = storeProductDAO.getStoreProductByUPC(productUPC);

            if (storeProduct == null) {
                showAlert("Товар не знайдено", "Товару з таким UPC немає.");
                return;
            }

            if (storeProduct.getProducts_number() <= 0) {
                showAlert("Немає в наявності", "Цей товар закінчився.");
                return;
            }
            CheckDetailsDTO existingItem = null;
            for (CheckDetailsDTO item : productDetails) {
                if (item.getUPC() != null && item.getUPC().equals(productUPC)) {
                    existingItem = item;
                    break;
                }
            }
            if (existingItem != null) {
                if (existingItem.getProduct_number() + 1 > storeProduct.getProducts_number()) {
                    showAlert("Недостатньо товару", "На складі недостатньо одиниць цього товару.");
                    return;
                }
                existingItem.setProduct_number(existingItem.getProduct_number() + 1);
                salesTable.refresh();
            } else {
                CheckDetailsDTO dto = new CheckDetailsDTO();
                dto.setUPC(storeProduct.getUPC());
                dto.setProduct_number(1);
                dto.setSelling_price(storeProduct.getSelling_price());
                Product product = productDAO.getProductById(storeProduct.getId_product());
                if (product != null) {
                    dto.setName(product.getName());
                } else {
                    dto.setName("Unknown product");
                }
                productDetails.add(dto);
            }

            salesTable.setItems(productDetails);
            salesTable.refresh();
            upcInputField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Помилка БД", e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        table.getItems().clear();
    }

    @FXML
    private void onCreate() {
        System.out.println("Чек створено");
    }



    public void cancelReciept(ActionEvent actionEvent) {
    }

    public void addReciept(ActionEvent actionEvent) {
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}