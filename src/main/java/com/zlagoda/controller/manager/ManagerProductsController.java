package com.zlagoda.controller.manager;

import com.zlagoda.dao.CategoryDAO;
import com.zlagoda.dao.ProductDAO;
import com.zlagoda.dao.SaleDAO;
import com.zlagoda.dao.Store_ProductDAO;
import com.zlagoda.dto.ProductCatalogDTO;
import com.zlagoda.dto.StoreProductDTO;
import com.zlagoda.model.Category;
import com.zlagoda.model.Product;
import com.zlagoda.model.Store_Product;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManagerProductsController {

    @FXML private TextField productSearchField;

    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, Integer> catIdColumn;
    @FXML private TableColumn<Category, String> catNameColumn;

    @FXML private Button addCategoryButton;
    @FXML private VBox editCategoryBox;
    @FXML private TextField categoryNameField;
    @FXML private Button cancelCategoryEdit;
    @FXML private Button saveCategoryButton;

    @FXML private ComboBox<String> filterComboBox;

    @FXML private TableView<ProductRow> productsTable;
    @FXML private TableColumn<ProductRow, String> upcColumn;
    @FXML private TableColumn<ProductRow, String> productNameColumn;
    @FXML private TableColumn<ProductRow, Double> priceColumn;
    @FXML private TableColumn<ProductRow, Integer> amountColumn;

    @FXML private VBox editProductBox;
    @FXML private TextField editUpcField;
    @FXML private TextField editProductNameField;
    @FXML private TextField editManufacturerField;
    @FXML private TextField editCharacteristicsField;
    @FXML private TextField editPriceField;
    @FXML private TextField editAmountField;
    @FXML private CheckBox promotionalCheckBox;
    @FXML private TextField upcPromField;
    @FXML private Button deleteProductButton;
    @FXML private Button deleteCategoryButton;

    private final SaleDAO saleDAO = new SaleDAO();


    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final Store_ProductDAO storeProductDAO = new Store_ProductDAO();

    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private final ObservableList<ProductRow> allProductRows = FXCollections.observableArrayList();
    private final ObservableList<ProductRow> visibleProductRows = FXCollections.observableArrayList();

    private Category selectedCategory;
    private ProductRow selectedProductRow;

    private boolean categoryEditMode = false;
    private boolean productEditMode = false;

    @FXML
    public void initialize() {
        setupCategoryTable();
        setupProductsTable();
        setupListeners();
        setupValidation();

        if (filterComboBox.getValue() == null) {
            filterComboBox.setValue("Усі");
        }

        hideCategoryEditBox();
        hideProductEditBox();

        refreshAllData();
    }

    private void setupCategoryTable() {
        catIdColumn.setCellValueFactory(new PropertyValueFactory<>("category_number"));
        catNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoriesTable.setItems(categoryList);
    }

    private void setupProductsTable() {
        upcColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getUpc()));
        productNameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getProductName()));
        priceColumn.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getPrice()).asObject());
        amountColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getAmount()).asObject());

        productsTable.setItems(visibleProductRows);
    }

    private void setupListeners() {
        categoriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedCategory = newVal;
            applyProductFilters();

            if (newVal != null) {
                showCategoryEditBox(newVal);
            }
        });

        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedProductRow = newVal;
            if (newVal != null) {
                showProductEditBox(newVal);
            }
        });

        filterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            loadProducts();
            applyProductFilters();

            updateTableColumns();
        });

        addCategoryButton.setOnAction(e -> {
            categoryEditMode = false;
            showCategoryEditBox(null);
        });

        cancelCategoryEdit.setOnAction(e -> hideCategoryEditBox());

        saveCategoryButton.setOnAction(e -> saveCategory());

        productSearchField.textProperty().addListener((obs, oldVal, newVal) -> applyProductFilters());
    }

    private void setupValidation() {
        editPriceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,4})?")) {
                editPriceField.setText(oldVal);
            }
        });

        editAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                editAmountField.setText(oldVal);
            }
        });
    }

    private void refreshAllData() {
        loadCategories();
        loadProducts();
        applyProductFilters();
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAllCategoriesOrderByName();
            categoryList.setAll(categories);
        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    private void loadProducts() {
        try {
            String filterValue = filterComboBox.getValue() == null ? "Усі" : filterComboBox.getValue();

            allProductRows.clear();

            if ("Усі".equals(filterValue)) {
                List<ProductCatalogDTO> products = productDAO.getAllProductsWithCategoryName();

                for (ProductCatalogDTO p : products) {
                    ProductRow row = new ProductRow(
                            null,
                            p.getProductName(),
                            0.0,
                            0,
                            p.getCategoryName(),
                            p.getManufacturer(),
                            p.getCharacteristics(),
                            false,
                            null
                    );
                    allProductRows.add(row);
                }

            } else {
                List<StoreProductDTO> storeProducts = switch (filterValue) {
                    case "Наявні" -> storeProductDAO.getAllStoreProductsOrderByName();
                    case "Акційні" -> storeProductDAO.getPromotionalProductsOrderByName();
                    case "Не акційні" -> storeProductDAO.getNonPromotionalProductsOrderByName();
                    default -> storeProductDAO.getAllStoreProductsOrderByName();
                };

                for (StoreProductDTO sp : storeProducts) {
                    ProductRow row = new ProductRow(
                            sp.getUpc(),
                            sp.getProductName(),
                            sp.getSellingPrice(),
                            sp.getProductsNumber(),
                            sp.getCategoryName(),
                            sp.getManufacturer(),
                            sp.getCharacteristics(),
                            sp.isPromotional(),
                            sp.getUpcProm()
                    );
                    allProductRows.add(row);
                }
            }

        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    private void updateTableColumns() {
        String filter = filterComboBox.getValue();
        boolean isCatalogMode = "Усі".equals(filter);

        priceColumn.setVisible(!isCatalogMode);
        amountColumn.setVisible(!isCatalogMode);

        upcColumn.setVisible(!isCatalogMode);
    }

    private void applyProductFilters() {
        String upcQuery = productSearchField.getText() == null
                ? ""
                : productSearchField.getText().trim().toLowerCase();

        String filterValue = filterComboBox.getValue() == null
                ? "Усі"
                : filterComboBox.getValue();

        List<ProductRow> filtered = new ArrayList<>();

        for (ProductRow row : allProductRows) {
            if (selectedCategory != null && !Objects.equals(row.getCategoryName(), selectedCategory.getName())) {
                continue;
            }

            if (!upcQuery.isEmpty() && !row.getUpc().toLowerCase().contains(upcQuery)) {
                continue;
            }

            boolean matchesFilter = switch (filterValue) {
                case "Наявні" -> row.getAmount() > 0;
                case "Акційні" -> row.isPromotional();
                case "Не акційні" -> !row.isPromotional();
                default -> true;
            };

            if (matchesFilter) {
                filtered.add(row);
            }
        }

        visibleProductRows.setAll(filtered);
    }

    public void searchProduct(javafx.event.ActionEvent actionEvent) {
        applyProductFilters();
    }

    public void addProduct(javafx.event.ActionEvent actionEvent) {
        productEditMode = false;
        showProductEditBox(null);
    }

    public void deleteProduct(javafx.event.ActionEvent actionEvent) {
        if (selectedProductRow == null) {
            showAlert("Помилка", "Оберіть товар для видалення.");
            return;
        }

        String filterValue = filterComboBox.getValue() == null ? "Усі" : filterComboBox.getValue();

        try {
            Product product = findProductByRow(selectedProductRow);
            if (product == null) {
                showAlert("Помилка", "Не вдалося знайти товар у таблиці Product.");
                return;
            }

            if ("Усі".equals(filterValue)) {
                boolean existsInStore = storeProductDAO.existsByProductId(product.getId_product());

                if (existsInStore) {
                    showAlert(
                            "Неможливо видалити",
                            "Цей товар є у Store_Product. Спочатку видаліть його зі складу."
                    );
                    return;
                }

                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Підтвердження");
                confirm.setHeaderText(null);
                confirm.setContentText("Видалити товар \"" + selectedProductRow.getProductName() + "\" з Product?");

                if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                    return;
                }

                productDAO.deleteProduct(product.getId_product());

            } else if ("Наявні".equals(filterValue)
                    || "Акційні".equals(filterValue)
                    || "Не акційні".equals(filterValue)) {

                Store_Product storeProduct = storeProductDAO.getFullStoreProductByUPC(selectedProductRow.getUpc());
                if (storeProduct == null) {
                    showAlert("Помилка", "Не вдалося знайти товар у Store_Product.");
                    return;
                }

                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Підтвердження");
                confirm.setHeaderText(null);
                confirm.setContentText("Видалити товар з UPC " + selectedProductRow.getUpc() + " зі Store_Product?");

                if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                    return;
                }

                storeProductDAO.deleteStoreProduct(selectedProductRow.getUpc());

            } else {
                showAlert("Помилка", "Невідомий режим фільтра.");
                return;
            }

            refreshAllData();
            hideProductEditBox();

        } catch (SQLException e) {
            showAlert("Помилка видалення", e.getMessage());
        }
    }

    public void cancelProductEdit(javafx.event.ActionEvent actionEvent) {
        hideProductEditBox();
    }

    public void saveProductEdit(javafx.event.ActionEvent actionEvent) {
        String upc = editUpcField.getText().trim();
        String name = editProductNameField.getText().trim();
        String manufacturer = editManufacturerField.getText().trim();
        String characteristics = editCharacteristicsField.getText().trim();
        String priceRaw = editPriceField.getText().trim();
        String amountRaw = editAmountField.getText().trim();
        String upcProm = upcPromField.getText().trim();
        boolean promotional = promotionalCheckBox.isSelected();

        if (upc.isEmpty() || name.isEmpty() || manufacturer.isEmpty() || characteristics.isEmpty()
                || priceRaw.isEmpty() || amountRaw.isEmpty()) {
            showAlert("Помилка", "Заповни всі обов'язкові поля товару.");
            return;
        }

        try {
            double price = Double.parseDouble(priceRaw);
            int amount = Integer.parseInt(amountRaw);

            if (price < 0 || amount < 0) {
                showAlert("Помилка", "Ціна і кількість не можуть бути від’ємними.");
                return;
            }

            if (productEditMode) {
                if (selectedProductRow == null) {
                    showAlert("Помилка", "Немає вибраного товару.");
                    return;
                }

                Product existingProduct = findProductByRow(selectedProductRow);
                if (existingProduct == null) {
                    showAlert("Помилка", "Не вдалося знайти Product для цього товару.");
                    return;
                }

                Product product = new Product();
                product.setId_product(existingProduct.getId_product());
                product.setName(name);
                product.setManufacturer(manufacturer);
                product.setCharacteristics(characteristics);
                product.setCategory_number(existingProduct.getCategory_number());

                Store_Product storeProduct = new Store_Product();
                storeProduct.setUPC(selectedProductRow.getUpc());
                storeProduct.setUPC_prom(upcProm.isEmpty() ? null : upcProm);
                storeProduct.setId_product(existingProduct.getId_product());
                storeProduct.setSelling_price(price);
                storeProduct.setProducts_number(amount);
                storeProduct.setPromotional_product(promotional);

                productDAO.updateProduct(product);
                storeProductDAO.updateStoreProduct(storeProduct);

            } else {
                if (selectedCategory == null) {
                    showAlert("Помилка", "Для додавання нового товару спочатку обери категорію.");
                    return;
                }

                Store_Product existingStoreProduct = storeProductDAO.getStoreProductByUPC(upc);
                if (existingStoreProduct != null) {
                    showAlert("Помилка", "Товар з таким UPC уже існує.");
                    return;
                }

                int nextProductId = getNextProductId();

                Product product = new Product();
                product.setId_product(nextProductId);
                product.setName(name);
                product.setManufacturer(manufacturer);
                product.setCharacteristics(characteristics);
                product.setCategory_number(selectedCategory.getCategory_number());

                Store_Product storeProduct = new Store_Product();
                storeProduct.setUPC(upc);
                storeProduct.setUPC_prom(upcProm.isEmpty() ? null : upcProm);
                storeProduct.setId_product(nextProductId);
                storeProduct.setSelling_price(price);
                storeProduct.setProducts_number(amount);
                storeProduct.setPromotional_product(promotional);

                productDAO.addProduct(product);
                storeProductDAO.addStoreProduct(storeProduct);
            }

            refreshAllData();
            hideProductEditBox();

        } catch (NumberFormatException e) {
            showAlert("Помилка формату", "Ціна або кількість введені некоректно.");
        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    private void saveCategory() {
        String categoryName = categoryNameField.getText().trim();

        if (categoryName.isEmpty()) {
            showAlert("Помилка", "Введи назву категорії.");
            return;
        }

        try {
            Category category = new Category();
            category.setName(categoryName);

            if (categoryEditMode) {
                if (selectedCategory == null) {
                    showAlert("Помилка", "Немає вибраної категорії.");
                    return;
                }

                category.setCategory_number(selectedCategory.getCategory_number());
                categoryDAO.updateCategory(category);
            } else {
                category.setCategory_number(getNextCategoryNumber());
                categoryDAO.addCategory(category);
            }

            hideCategoryEditBox();
            refreshAllData();

        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    private int getNextCategoryNumber() throws SQLException {
        List<Category> categories = categoryDAO.getAllCategoriesOrderByName();
        int max = 0;

        for (Category category : categories) {
            if (category.getCategory_number() > max) {
                max = category.getCategory_number();
            }
        }

        return max + 1;
    }

    private int getNextProductId() throws SQLException {
        List<Product> products = productDAO.getAllProductsOrderByName();
        int max = 0;

        for (Product product : products) {
            if (product.getId_product() > max) {
                max = product.getId_product();
            }
        }

        return max + 1;
    }

    private Product findProductByRow(ProductRow row) throws SQLException {
        List<Product> products = productDAO.getAllProductsOrderByName();
        Integer categoryNumber = findCategoryNumberByName(row.getCategoryName());

        if (categoryNumber == null) {
            return null;
        }

        for (Product product : products) {
            if (Objects.equals(product.getName(), row.getProductName())
                    && Objects.equals(product.getManufacturer(), row.getManufacturer())
                    && Objects.equals(product.getCharacteristics(), row.getCharacteristics())
                    && product.getCategory_number() == categoryNumber) {
                return product;
            }
        }

        return null;
    }

    private Integer findCategoryNumberByName(String categoryName) {
        for (Category category : categoryList) {
            if (Objects.equals(category.getName(), categoryName)) {
                return category.getCategory_number();
            }
        }
        return null;
    }

    private void showCategoryEditBox(Category category) {
        editCategoryBox.setVisible(true);
        editCategoryBox.setManaged(true);

        if (category == null) {
            categoryEditMode = false;
            categoryNameField.clear();

            deleteCategoryButton.setVisible(false);
            deleteCategoryButton.setManaged(false);
        } else {
            categoryEditMode = true;
            categoryNameField.setText(category.getName());

            deleteCategoryButton.setVisible(true);
            deleteCategoryButton.setManaged(true);
        }
    }

    private void hideCategoryEditBox() {
        editCategoryBox.setVisible(false);
        editCategoryBox.setManaged(false);
        categoryNameField.clear();

        deleteCategoryButton.setVisible(false);
        deleteCategoryButton.setManaged(false);

        categoriesTable.getSelectionModel().clearSelection();
        selectedCategory = null;
    }

    private void showProductEditBox(ProductRow row) {
        editProductBox.setVisible(true);
        editProductBox.setManaged(true);

        String filter = filterComboBox.getValue();
        boolean isCatalogOnly = "Усі".equals(filter);

        editProductNameField.setEditable(true);
        editManufacturerField.setEditable(true);
        editCharacteristicsField.setEditable(true);

        editUpcField.setEditable(!isCatalogOnly && !productEditMode);
        editPriceField.setEditable(!isCatalogOnly);
        editAmountField.setEditable(!isCatalogOnly);
        promotionalCheckBox.setDisable(isCatalogOnly);
        upcPromField.setEditable(!isCatalogOnly);

        if (row == null) {
            productEditMode = false;

            if (deleteProductButton != null) {
                deleteProductButton.setVisible(false);
                deleteProductButton.setManaged(false);
            }

            editUpcField.clear();
            editUpcField.setEditable(true);
            editProductNameField.clear();
            editManufacturerField.clear();
            editCharacteristicsField.clear();
            editPriceField.clear();
            editAmountField.clear();
            promotionalCheckBox.setSelected(false);
            upcPromField.clear();
        } else {
            productEditMode = true;
            editUpcField.setText(row.getUpc() == null ? "" : row.getUpc());
            editProductNameField.setText(row.getProductName());

            if (deleteProductButton != null) {
                deleteProductButton.setVisible(true);
                deleteProductButton.setManaged(true);
            }

            editUpcField.setText(row.getUpc());
            editUpcField.setEditable(false);
            editProductNameField.setText(row.getProductName());
            editManufacturerField.setText(row.getManufacturer());
            editCharacteristicsField.setText(row.getCharacteristics());
            editPriceField.setText(String.valueOf(row.getPrice()));
            editAmountField.setText(String.valueOf(row.getAmount()));
            promotionalCheckBox.setSelected(row.isPromotional());
            upcPromField.setText(row.getUpcProm() == null ? "" : row.getUpcProm());
        }
    }
    public void deleteCategory(javafx.event.ActionEvent actionEvent) {
        if (selectedCategory == null || !categoryEditMode) {
            showAlert("Помилка", "Оберіть категорію для видалення.");
            return;
        }

        try {
            boolean hasProducts = categoryDAO.hasProductsInCategory(selectedCategory.getCategory_number());

            if (hasProducts) {
                showAlert(
                        "Неможливо видалити",
                        "У цій категорії є товари в Product. Спочатку видаліть їх."
                );
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Підтвердження");
            confirm.setHeaderText(null);
            confirm.setContentText("Видалити категорію \"" + selectedCategory.getName() + "\"?");

            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            categoryDAO.deleteCategory(selectedCategory.getCategory_number());

            hideCategoryEditBox();
            refreshAllData();

        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }
    private void hideProductEditBox() {
        editProductBox.setVisible(false);
        editProductBox.setManaged(false);

        editUpcField.clear();
        editUpcField.setEditable(true);
        editProductNameField.clear();
        editManufacturerField.clear();
        editCharacteristicsField.clear();
        editPriceField.clear();
        editAmountField.clear();
        promotionalCheckBox.setSelected(false);
        upcPromField.clear();

        productsTable.getSelectionModel().clearSelection();
        selectedProductRow = null;
    }

    public void showAllCategories(javafx.event.ActionEvent actionEvent) {
        selectedCategory = null;
        categoriesTable.getSelectionModel().clearSelection();
        editCategoryBox.setVisible(false);
        editCategoryBox.setManaged(false);
        categoryNameField.clear();
        productSearchField.clear();
        filterComboBox.setValue("Усі");
        applyProductFilters();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void showProductSalesPopup(ActionEvent actionEvent) {
        Object selectedItem = productsTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Увага");
            alert.setHeaderText(null);
            alert.setContentText("Оберіть товар.");
            alert.showAndWait();
            return;
        }

        String upc = editUpcField.getText();

        if (upc == null || upc.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setHeaderText(null);
            alert.setContentText("Не вдалося визначити UPC вибраного товару.");
            alert.showAndWait();
            return;
        }

        String productName = editProductNameField.getText();
        if (productName == null || productName.isBlank()) {
            productName = "Товар";
        }

        String finalUpc = upc.trim();
        String finalProductName = productName.trim();

        Stage popupStage = new Stage();
        popupStage.setTitle("Продажі товару");
        popupStage.initModality(Modality.APPLICATION_MODAL);

        Label titleLabel = new Label("Продажі товару: " + finalProductName + " (UPC: " + finalUpc + ")");
        Label fromLabel = new Label("Дата від:");
        DatePicker fromDatePicker = new DatePicker();

        Label toLabel = new Label("Дата до:");
        DatePicker toDatePicker = new DatePicker();

        Button getSalesButton = new Button("ОТРИМАТИ КІЛЬКІСТЬ ПРОДАЖІВ ЦЬОГО ТОВАРУ");
        Label resultLabel = new Label();
        resultLabel.setWrapText(true);

        getSalesButton.setOnAction(e -> {
            try {
                LocalDate dateFrom = fromDatePicker.getValue();
                LocalDate dateTo = toDatePicker.getValue();

                if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Некоректний період");
                    alert.setHeaderText(null);
                    alert.setContentText("Дата 'від' не може бути пізніше за дату 'до'.");
                    alert.showAndWait();
                    return;
                }

                int totalSold = saleDAO.getTotalSoldByUpc(finalUpc, dateFrom, dateTo);

                String periodText;
                if (dateFrom == null && dateTo == null) {
                    periodText = "за весь період";
                } else if (dateFrom != null && dateTo != null) {
                    periodText = "за період з " + dateFrom + " до " + dateTo;
                } else if (dateFrom != null) {
                    periodText = "за період від " + dateFrom;
                } else {
                    periodText = "за період до " + dateTo;
                }

                resultLabel.setText("Кількість продажів " + periodText + ": " + totalSold);

            } catch (SQLException ex) {
                ex.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Не вдалося отримати дані про продажі.");
                alert.showAndWait();
            }
        });

        VBox root = new VBox(12);
        root.setStyle("-fx-padding: 20; -fx-background-color: white;");
        root.getChildren().addAll(
                titleLabel,
                fromLabel, fromDatePicker,
                toLabel, toDatePicker,
                getSalesButton,
                resultLabel
        );

        Scene scene = new Scene(root, 500, 280);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    public static class ProductRow {
        private final String upc;
        private final String productName;
        private final double price;
        private final int amount;
        private final String categoryName;
        private final String manufacturer;
        private final String characteristics;
        private final boolean promotional;
        private final String upcProm;

        public ProductRow(String upc, String productName, double price, int amount,
                          String categoryName, String manufacturer,
                          String characteristics, boolean promotional, String upcProm) {
            this.upc = upc;
            this.productName = productName;
            this.price = price;
            this.amount = amount;
            this.categoryName = categoryName;
            this.manufacturer = manufacturer;
            this.characteristics = characteristics;
            this.promotional = promotional;
            this.upcProm = upcProm;
        }

        public String getUpc() {
            return upc;
        }

        public String getProductName() {
            return productName;
        }

        public double getPrice() {
            return price;
        }

        public int getAmount() {
            return amount;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getCharacteristics() {
            return characteristics;
        }

        public boolean isPromotional() {
            return promotional;
        }

        public String getUpcProm() {
            return upcProm;
        }
    }
}