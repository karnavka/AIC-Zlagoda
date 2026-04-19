package com.zlagoda.controller.cashier;

import com.zlagoda.dao.ProductDAO;
import com.zlagoda.dao.Store_ProductDAO;
import com.zlagoda.model.Product;
import com.zlagoda.model.Store_Product;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CashierProductsController {

    @FXML public TextField nameSearchField;
    @FXML public ComboBox<String> categoryComboBox;
    @FXML public ComboBox<String> allComboBox;
    @FXML public ComboBox<String> sortComboBox;
    @FXML public TableView<ProductRow> productsTable;
    @FXML public TableColumn<ProductRow, String> idColumn;
    @FXML public TableColumn<ProductRow, String> nameColumn;
    @FXML public VBox detailsBox;
    @FXML public Label upcLabel;
    @FXML public Label nameLabel;
    @FXML public Label manufacturerLabel;
    @FXML public Label characteristicsLabel;
    @FXML public Label stockLabel;
    @FXML public Label priceLabel;
    @FXML public Label promoPriceLabel;

    private final Store_ProductDAO storeProductDAO = new Store_ProductDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private final ObservableList<ProductRow> tableData = FXCollections.observableArrayList();
    private final List<ProductRow> allProducts = new ArrayList<>();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> data.getValue().upcProperty());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());

        allComboBox.setItems(FXCollections.observableArrayList("усі", "акційні", "неакційні"));
        allComboBox.setValue("усі");

        sortComboBox.setItems(FXCollections.observableArrayList("НАЗВА", "КІЛЬКІСТЬ"));
        sortComboBox.setValue("НАЗВА");

        categoryComboBox.setItems(FXCollections.observableArrayList("Усі"));
        categoryComboBox.setValue("Усі");

        productsTable.setItems(tableData);

        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showDetails(newVal);
            } else {
                clearDetails();
            }
        });

        loadProducts();
    }

    private void loadProducts() {
        try {
            allProducts.clear();

            List<Store_Product> storeProducts = storeProductDAO.getAllStoreProductsOrderByName();

            Set<String> categoryValues = new TreeSet<>();
            categoryValues.add("Усі");

            for (Store_Product sp : storeProducts) {
                Product p = productDAO.getProductById(sp.getId_product());
                if (p == null) continue;

                String categoryValue = String.valueOf(p.getCategory_number());
                categoryValues.add(categoryValue);

                allProducts.add(new ProductRow(sp, p));
            }

            categoryComboBox.setItems(FXCollections.observableArrayList(categoryValues));
            categoryComboBox.setValue("Усі");

            tableData.setAll(allProducts);
            clearDetails();

        } catch (SQLException e) {
            showError("Помилка завантаження товарів", e.getMessage());
        }
    }

    @FXML
    public void searchProduct(ActionEvent actionEvent) {
        String nameText = nameSearchField.getText() == null ? "" : nameSearchField.getText().trim().toLowerCase();
        String category = categoryComboBox.getValue();
        String promoFilter = allComboBox.getValue();
        String sortValue = sortComboBox.getValue();

        List<ProductRow> filtered = allProducts.stream()
                .filter(row -> nameText.isEmpty() || row.getName().toLowerCase().contains(nameText))
                .filter(row -> category == null || category.equals("Усі") || row.getCategoryNumberText().equals(category))
                .filter(row -> {
                    if ("акційні".equals(promoFilter)) return row.isPromotional();
                    if ("неакційні".equals(promoFilter)) return !row.isPromotional();
                    return true;
                })
                .collect(Collectors.toList());

        if ("КІЛЬКІСТЬ".equals(sortValue)) {
            filtered.sort(Comparator.comparingInt(ProductRow::getProductsNumber));
        } else {
            filtered.sort(Comparator.comparing(ProductRow::getName, String.CASE_INSENSITIVE_ORDER));
        }

        tableData.setAll(filtered);
        productsTable.getSelectionModel().clearSelection();
        clearDetails();
    }

    @FXML
    public void clearButton(ActionEvent actionEvent) {
        nameSearchField.clear();
        categoryComboBox.setValue("Усі");
        allComboBox.setValue("усі");
        sortComboBox.setValue("НАЗВА");

        tableData.setAll(allProducts);
        productsTable.getSelectionModel().clearSelection();
        clearDetails();
    }

    private void showDetails(ProductRow row) {
        try {
            detailsBox.setVisible(true);
            detailsBox.setManaged(true);

            upcLabel.setText(row.getUpc());
            nameLabel.setText(row.getName());
            manufacturerLabel.setText(row.getManufacturer());
            characteristicsLabel.setText(row.getCharacteristics());
            stockLabel.setText(String.valueOf(row.getProductsNumber()));
            priceLabel.setText(String.format("%.2f", row.getSellingPrice()));

            String promoText = "---";

            if (row.isPromotional()) {
                promoText = String.format("%.2f", row.getSellingPrice());
            } else if (row.getUpcProm() != null && !row.getUpcProm().isBlank()) {
                Store_Product promoProduct = storeProductDAO.getFullStoreProductByUPC(row.getUpcProm());
                if (promoProduct != null) {
                    promoText = String.format("%.2f", promoProduct.getSelling_price());
                }
            }

            promoPriceLabel.setText(promoText);

        } catch (SQLException e) {
            showError("Помилка завантаження деталей", e.getMessage());
        }
    }

    private void clearDetails() {
        detailsBox.setVisible(false);
        detailsBox.setManaged(false);

        upcLabel.setText("---");
        nameLabel.setText("---");
        manufacturerLabel.setText("---");
        characteristicsLabel.setText("---");
        stockLabel.setText("---");
        priceLabel.setText("---");
        promoPriceLabel.setText("---");
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class ProductRow {
        private final Store_Product storeProduct;
        private final Product product;

        public ProductRow(Store_Product storeProduct, Product product) {
            this.storeProduct = storeProduct;
            this.product = product;
        }

        public String getUpc() {
            return storeProduct.getUPC();
        }

        public SimpleStringProperty upcProperty() {
            return new SimpleStringProperty(getUpc());
        }

        public String getName() {
            return product.getName();
        }

        public SimpleStringProperty nameProperty() {
            return new SimpleStringProperty(getName());
        }

        public String getManufacturer() {
            return product.getManufacturer();
        }

        public String getCharacteristics() {
            return product.getCharacteristics();
        }

        public int getCategoryNumber() {
            return product.getCategory_number();
        }

        public String getCategoryNumberText() {
            return String.valueOf(product.getCategory_number());
        }

        public int getProductsNumber() {
            return storeProduct.getProducts_number();
        }

        public double getSellingPrice() {
            return storeProduct.getSelling_price();
        }

        public boolean isPromotional() {
            return storeProduct.isPromotional_product();
        }

        public String getUpcProm() {
            return storeProduct.getUPC_prom();
        }
    }
}