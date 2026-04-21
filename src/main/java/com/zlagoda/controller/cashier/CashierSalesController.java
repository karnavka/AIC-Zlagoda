package com.zlagoda.controller.cashier;

import com.zlagoda.dao.*;
import com.zlagoda.dto.CheckDetailsDTO;
import com.zlagoda.dto.StoreProductDTO;
import com.zlagoda.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class CashierSalesController {
    private User currentUser;
    private Employee currentEmployee;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final Store_ProductDAO storeProductDAO = new Store_ProductDAO();
    private final ObservableList<CheckDetailsDTO> productDetails  = FXCollections.observableArrayList();
    private final   CheckDAO checkDAO = new CheckDAO();
    private final Customer_CardDAO customerCardDAO = new Customer_CardDAO();
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

        clientCardField.textProperty().addListener((obs, oldValue, newValue) -> {
            updateReceiptSummary();
        });

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
            updateReceiptSummary();
            upcInputField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Помилка БД", e.getMessage());
        }


    }



    public void addReciept(ActionEvent actionEvent) {
        if (productDetails.isEmpty()) {
            showAlert("Помилка", "Чек порожній.");
            return;
        }

        Check check = new Check();
        check.setId_employee(currentUser.getEmployeeId());
        check.setCard_number(clientCardField.getText().trim().isEmpty() ? null : clientCardField.getText().trim());
        check.setPrint_date(java.time.LocalDateTime.now());

        double totalRaw = 0.0;
        for (CheckDetailsDTO item : productDetails) {
            totalRaw += item.getProduct_number() * item.getSelling_price();
        }

        int discountPercent = 0;
        String cardNumber = clientCardField.getText().trim();

        if (!cardNumber.isEmpty()) {
            try {
                Customer_Card card = customerCardDAO.getCustomerCardById(cardNumber);
                if (card != null) {
                    discountPercent = card.getPercent();
                }
            } catch (SQLException e) {
                showAlert("Помилка БД", e.getMessage());
                return;
            }
        }

        double discountAmount = totalRaw * discountPercent / 100.0;
        double totalFinal = totalRaw - discountAmount;
        double vat = totalFinal * 0.2;

        check.setSum_total(totalFinal);
        check.setVat(vat);

        try {
            check.setCheck_number(generateUniqueCheckNumber());
            checkDAO.addCheck(check);

            SaleDAO saleDAO = new SaleDAO();
            for (CheckDetailsDTO item : productDetails) {
                Sale sale = new Sale();
                sale.setCheck_number(check.getCheck_number());
                sale.setUPC(item.getUPC());
                sale.setProduct_number(item.getProduct_number());
                sale.setSelling_price(item.getSelling_price());
                saleDAO.addSale(sale);
            }

            checkNumberLabel.setText(check.getCheck_number());
            clearReceiptForm();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Помилка БД", e.getMessage());
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private String generateUniqueCheckNumber() throws SQLException {
        String checkNumber;

        do {
            checkNumber = generateRandomCheckNumber();
        } while (checkDAO.existsByCheckNumber(checkNumber));

        return checkNumber;
    }

    private static final String CHECK_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private String generateRandomCheckNumber() {
        java.util.Random random = new java.util.Random();
        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(CHECK_CHARS.length());
            sb.append(CHECK_CHARS.charAt(index));
        }

        return sb.toString();
    }

    private void updateReceiptSummary() {
        double totalRaw = 0.0;

        for (CheckDetailsDTO item : productDetails) {
            totalRaw += item.getProduct_number() * item.getSelling_price();
        }

        int discountPercent = 0;
        String cardNumber = clientCardField.getText().trim();

        if (!cardNumber.isEmpty()) {
            try {
                Customer_Card card = customerCardDAO.getCustomerCardById(cardNumber);
                if (card != null) {
                    discountPercent = card.getPercent();
                }
            } catch (SQLException e) {
                showAlert("Помилка БД", e.getMessage());
                return;
            }
        }

        double discountAmount = totalRaw * discountPercent / 100.0;
        double totalFinal = totalRaw - discountAmount;
        double vat = totalFinal * 0.2;

        discountPercentLabel.setText(discountPercent + "%");
        totalRawLabel.setText(String.format("%.2f", totalRaw));
        discountAmountLabel.setText(String.format("%.2f", discountAmount));
        totalFinalLabel.setText(String.format("%.2f", totalFinal));
        vatLabel.setText(String.format("%.2f", vat));
    }
    private void clearReceiptForm() {
        productDetails.clear();
        salesTable.refresh();

        upcInputField.clear();
        clientCardField.clear();

        discountPercentLabel.setText("0%");
        totalRawLabel.setText("0.00");
        discountAmountLabel.setText("0.00");
        totalFinalLabel.setText("0.00");
        vatLabel.setText("0.00");

        checkNumberLabel.setText("");
    }

    @FXML
    public void handleUpcCheck(ActionEvent event) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Швидкий пошук за UPC");

        TextField upcInput = new TextField();
        upcInput.setPromptText("UPC...");
        upcInput.setPrefWidth(400);

        TableView<StoreProductDTO> resultTable = new TableView<>();
        resultTable.setPrefHeight(200);
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StoreProductDTO, String> upcCol = new TableColumn<>("UPC");
        upcCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUpc()));

        TableColumn<StoreProductDTO, String> nameCol = new TableColumn<>("Назва");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProductName()));

        TableColumn<StoreProductDTO, Double> priceCol = new TableColumn<>("Ціна");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getSellingPrice()));

        TableColumn<StoreProductDTO, Integer> stockCol = new TableColumn<>("К-ть");
        stockCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getProductsNumber()));

        resultTable.getColumns().addAll(upcCol, nameCol, priceCol, stockCol);

        upcInput.textProperty().addListener((observable, oldValue, newValue) -> {
            String code = newValue.trim();
            if (!code.isEmpty()) {
                try {
                    List<StoreProductDTO> products = storeProductDAO.searchByUpcStartingWith(code);
                    resultTable.setItems(FXCollections.observableArrayList(products));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                resultTable.getItems().clear();
            }
        });

        resultTable.setRowFactory(tv -> {
            TableRow<StoreProductDTO> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && (!row.isEmpty())) {
                    upcInputField.setText(row.getItem().getUpc());
                    dialog.setResult(null);
                    dialog.close();
                }
            });
            return row;
        });

        VBox vbox = new VBox(10, new Label("Введіть код для перевірки ціни та залишку:"), upcInput, resultTable);
        vbox.setPadding(new javafx.geometry.Insets(20));

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}
