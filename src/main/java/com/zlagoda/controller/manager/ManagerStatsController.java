package com.zlagoda.controller.manager;

import com.zlagoda.dao.CategoryDAO;
import com.zlagoda.dao.EmployeeDAO;
import com.zlagoda.dao.ProductDAO;
import com.zlagoda.model.Category;
import com.zlagoda.model.Product;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class ManagerStatsController {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private final ObservableList<CategoryDAO.CategoryStat> categoryStats = FXCollections.observableArrayList();
    private final ObservableList<EmployeeDAO.EmployeeStat> cashierStats = FXCollections.observableArrayList();
    private final ObservableList<ProductDAO.ProductSalesStat> productStats = FXCollections.observableArrayList();

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private TableView<CategoryDAO.CategoryStat> categoryTable;

    @FXML
    private TableColumn<CategoryDAO.CategoryStat, Integer> numberOfCategory;

    @FXML
    private TableColumn<CategoryDAO.CategoryStat, String> nameOfCategory;

    @FXML
    private TableColumn<CategoryDAO.CategoryStat, Integer> numberOfSoldProductsInCategory;

    @FXML
    private TableView<EmployeeDAO.EmployeeStat> cashierTable;

    @FXML
    private TableColumn<EmployeeDAO.EmployeeStat, String> idOfCashier;

    @FXML
    private TableColumn<EmployeeDAO.EmployeeStat, String> surname;

    @FXML
    private TableColumn<EmployeeDAO.EmployeeStat, String> firstName;

    @FXML
    private TableColumn<EmployeeDAO.EmployeeStat, Integer> numberOfSoldProductsByThisCashier;

    @FXML
    private TableColumn<EmployeeDAO.EmployeeStat, Double> sumOfProductsSoldByThisCashier;

    @FXML
    private TableView<ProductDAO.ProductSalesStat> productsTable;

    @FXML
    private TableColumn<ProductDAO.ProductSalesStat, String> productUpcColumn;

    @FXML
    private TableColumn<ProductDAO.ProductSalesStat, String> productNameColumn;

    @FXML
    private TableColumn<ProductDAO.ProductSalesStat, Integer> productQuantityColumn;

    @FXML
    private TableColumn<ProductDAO.ProductSalesStat, Double> productSumColumn;

    @FXML
    private Label productsWithoutSales;

    @FXML
    private Label categoriesWithousSales;

    @FXML
    private Label cashiersWithAllCategories;

    @FXML
    public void initialize() {
        setupCategoryTable();
        setupCashierTable();
        setupProductsTable();
    }

    private void setupCategoryTable() {
        numberOfCategory.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCategoryNumber()).asObject());

        nameOfCategory.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        numberOfSoldProductsInCategory.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTotalNumber()).asObject());

        categoryTable.setItems(categoryStats);
    }

    private void setupCashierTable() {
        idOfCashier.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId_employee()));

        surname.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSurname()));

        firstName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        numberOfSoldProductsByThisCashier.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTotal_products()).asObject());

        sumOfProductsSoldByThisCashier.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotal_sales()).asObject());

        cashierTable.setItems(cashierStats);
    }

    private void setupProductsTable() {
        productUpcColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUpc()));

        productNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        productQuantityColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTotalNumber()).asObject());

        productSumColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalSales()).asObject());

        productsTable.setItems(productStats);
    }

    @FXML
    public void formAnalysis(ActionEvent actionEvent) {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();

        if (start == null || end == null) {
            showAlert("Помилка", "Оберіть початкову і кінцеву дату.");
            return;
        }

        if (end.isBefore(start)) {
            showAlert("Помилка", "Кінцева дата не може бути раніше початкової.");
            return;
        }

        try {
            categoryStats.setAll(categoryDAO.getSoldProductsNumberByCategory(start, end));
            cashierStats.setAll(employeeDAO.getEmployeeSalesStats(start, end));
            productStats.setAll(productDAO.getTotalSalesPerProduct(start, end));

            var neverSoldProducts = productDAO.getProductsNeverSold(start, end);
            var categoriesWithoutSalesList = categoryDAO.getCategoriesWithNoPurchases(start, end);
            var cashiersWithAllCategoriesList = employeeDAO.getCashiersSoldEveryCategory(start, end);

            if (neverSoldProducts.isEmpty()) {
                productsWithoutSales.setText("Товари без продажів: —");
            } else {
                String productNames = neverSoldProducts.stream()
                        .map(Product::getName)
                        .distinct()
                        .collect(Collectors.joining(", "));
                productsWithoutSales.setText("Товари без продажів: " + productNames);
            }

            if (categoriesWithoutSalesList.isEmpty()) {
                categoriesWithousSales.setText("Категорії без продажів: —");
            } else {
                String categoryNames = categoriesWithoutSalesList.stream()
                        .map(Category::getName)
                        .distinct()
                        .collect(Collectors.joining(", "));
                categoriesWithousSales.setText("Категорії без продажів: " + categoryNames);
            }

            if (cashiersWithAllCategoriesList.isEmpty()) {
                cashiersWithAllCategories.setText("Касири, які продавали товари з кожної категорії за заданий період: —");
            } else {
                String cashierNames = cashiersWithAllCategoriesList.stream()
                        .map(e -> e.getId_employee() + " - " + e.getSurname() + " " + e.getName())
                        .distinct()
                        .collect(Collectors.joining(", "));
                cashiersWithAllCategories.setText(
                        "Касири, які продавали товари з кожної категорії за заданий період: " + cashierNames
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Помилка БД", e.getMessage());
        }
    }

    @FXML
    public void cleanAnalysis(ActionEvent actionEvent) {
        startDate.setValue(null);
        endDate.setValue(null);

        categoryStats.clear();
        cashierStats.clear();
        productStats.clear();

        productsWithoutSales.setText("Товари без продажів: —");
        categoriesWithousSales.setText("Категорії без продажів: —");
        cashiersWithAllCategories.setText("Касири, які продавали товари з кожної категорії за заданий період: —");
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}