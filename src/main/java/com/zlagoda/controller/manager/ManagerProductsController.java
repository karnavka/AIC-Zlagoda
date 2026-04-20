package com.zlagoda.controller.manager;

import com.zlagoda.dao.CategoryDAO;
import com.zlagoda.dao.ProductDAO;
import com.zlagoda.dao.Store_ProductDAO;
import com.zlagoda.dto.StoreProductDTO;
import com.zlagoda.model.Category;
import com.zlagoda.model.Product;
import com.zlagoda.model.Store_Product;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
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

        loadCategories();
        loadProducts();
        applyProductFilters();
    }

    private void setupCategoryTable() {
        catIdColumn.setCellValueFactory(new PropertyValueFactory<>("category_number"));
        catNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoriesTable.setItems(categoryList);
    }

    private void setupProductsTable() {
        upcColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUpc()));
        productNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProductName()));
        priceColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrice()).asObject());
        amountColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAmount()).asObject());

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

        filterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyProductFilters());

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
            List<StoreProductDTO> storeProducts = storeProductDAO.getAllStoreProductsOrderByName();

            allProductRows.clear();

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

        } catch (SQLException e) {
            showAlert("Помилка БД", e.getMessage());
        }
    }

    private void applyProductFilters() {
        String upcQuery = productSearchField.getText() == null
                ? ""
                : productSearchField.getText().trim().toLowerCase();

        String filterValue = filterComboBox.getValue() == null
                ? "Усі"
                : filterValueSafe(filterComboBox.getValue());

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

    private String filterValueSafe(String value) {
        return value == null ? "Усі" : value;
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

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Підтвердження");
        confirm.setHeaderText(null);
        confirm.setContentText("Видалити товар з UPC " + selectedProductRow.getUpc() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            storeProductDAO.deleteStoreProduct(selectedProductRow.getUpc());
            loadProducts();
            applyProductFilters();
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
                    showAlert("Помилка", "Не вдалося знайти відповідний Product у базі.");
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

            loadProducts();
            applyProductFilters();
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

            loadCategories();
            hideCategoryEditBox();

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
        } else {
            categoryEditMode = true;
            categoryNameField.setText(category.getName());
        }
    }

    private void hideCategoryEditBox() {
        editCategoryBox.setVisible(false);
        editCategoryBox.setManaged(false);
        categoryNameField.clear();
        categoriesTable.getSelectionModel().clearSelection();
        selectedCategory = null;
    }

    private void showProductEditBox(ProductRow row) {
        editProductBox.setVisible(true);
        editProductBox.setManaged(true);

        if (row == null) {
            productEditMode = false;

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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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