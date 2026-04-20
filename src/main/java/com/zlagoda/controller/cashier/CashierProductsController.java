package com.zlagoda.controller.cashier;

import com.zlagoda.dao.CategoryDAO;
import com.zlagoda.dao.Store_ProductDAO;
import com.zlagoda.dto.StoreProductDTO;
import com.zlagoda.model.Category;
import com.zlagoda.model.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class CashierProductsController {

    private User currentUser;
    private final Store_ProductDAO storeProductDAO = new Store_ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ObservableList<StoreProductDTO> tableData = FXCollections.observableArrayList();

    @FXML public TextField nameSearchField;
    @FXML public ComboBox<String> categoryComboBox;
    @FXML public ComboBox<String> allComboBox;
    @FXML public ComboBox<String> sortComboBox;

    @FXML private TableView<StoreProductDTO> productsTable;
    @FXML private TableColumn<StoreProductDTO, String> idColumn;
    @FXML private TableColumn<StoreProductDTO, String> nameColumn;
    @FXML private TableColumn<StoreProductDTO, String> categoryColumn;
    @FXML private TableColumn<StoreProductDTO, String> manufacturerColumn;
    @FXML private TableColumn<StoreProductDTO, Double> priceColumn;
    @FXML private TableColumn<StoreProductDTO, Integer> stockColumn;
    @FXML private TableColumn<StoreProductDTO, String> promoColumn;

    @FXML private VBox detailsBox;
    @FXML private Label upcLabel, nameLabel, manufacturerLabel, characteristicsLabel, stockLabel, priceLabel, promoPriceLabel;

    @FXML public Button upcCheckButton;

    @FXML
    public void initialize() {
        setupTable();

        allComboBox.setItems(FXCollections.observableArrayList("усі", "акційні", "неакційні", "наявні"));
        allComboBox.setValue("усі");

        sortComboBox.setItems(FXCollections.observableArrayList("НАЗВА", "КІЛЬКІСТЬ"));
        sortComboBox.setValue("НАЗВА");

        sortComboBox.setOnAction(e -> refreshTable());

        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) showDetails(newVal);
            else clearDetails();
        });

        loadCategories();
        refreshTable();
    }

    private void setupTable() {
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUpc()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoryName()));
        manufacturerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getManufacturer()));
        priceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSellingPrice()));
        stockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProductsNumber()));
        promoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isPromotional() ? "Так" : "Ні"));

        productsTable.setItems(tableData);
    }

    @FXML
    public void searchProduct(ActionEvent event) {
        refreshTable();
    }

    private void refreshTable() {
        try {
            String name = (nameSearchField.getText() == null) ? "" : nameSearchField.getText().trim();
            String category = categoryComboBox.getValue();
            String promo = allComboBox.getValue();
            String sort = sortComboBox.getValue();

            List<StoreProductDTO> results;

            if (!name.isEmpty()) {
                if ("КІЛЬКІСТЬ".equals(sort)) {
                    results = storeProductDAO.searchByProductNameOrderByNumber(name);
                } else {
                    results = storeProductDAO.searchByProductName(name);
                }
            }
            else if ("акційні".equals(promo)) {
                if ("КІЛЬКІСТЬ".equals(sort)) results = storeProductDAO.getPromotionalProductsOrderByNumber();
                else results = storeProductDAO.getPromotionalProductsOrderByName();
            }
            else if ("неакційні".equals(promo)) {
                if ("КІЛЬКІСТЬ".equals(sort)) results = storeProductDAO.getNonPromotionalProductsOrderByNumber();
                else results = storeProductDAO.getNonPromotionalProductsOrderByName();
            }
            else {
                if ("КІЛЬКІСТЬ".equals(sort)) results = storeProductDAO.getAllStoreProductsOrderByNumber();
                else results = storeProductDAO.getAllStoreProductsOrderByName();
            }

            ObservableList<StoreProductDTO> filteredList = FXCollections.observableArrayList();

            for (StoreProductDTO item : results) {
                boolean matchesCategory = (category == null || category.equals("Усі")) ||
                        (item.getCategoryName() != null && item.getCategoryName().equals(category));

                boolean matchesStock = !"у наявності".equals(promo) || item.getProductsNumber() > 0;

                boolean matchesPromoStatus = true;
                if (!name.isEmpty()) {
                    if ("акційні".equals(promo)) matchesPromoStatus = item.isPromotional();
                    else if ("неакційні".equals(promo)) matchesPromoStatus = !item.isPromotional();
                }

                if (matchesCategory && matchesStock && matchesPromoStatus) {
                    filteredList.add(item);
                }
            }

            tableData.setAll(filteredList);

            productsTable.getSortOrder().clear();

        } catch (SQLException e) {
            showAlert("Помилка", e.getMessage());
        }
    }

    @FXML
    public void clearButton(ActionEvent event) {
        nameSearchField.clear();
        categoryComboBox.setValue("Усі");
        allComboBox.setValue("усі");
        sortComboBox.setValue("НАЗВА");
        refreshTable();
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAllCategoriesOrderByName();
            ObservableList<String> names = FXCollections.observableArrayList("Усі");
            for (Category c : categories) names.add(c.getName());
            categoryComboBox.setItems(names);
            categoryComboBox.setValue("Усі");
        } catch (SQLException e) {
            showAlert("Помилка категорій", e.getMessage());
        }
    }

    private void showDetails(StoreProductDTO dto) {
        detailsBox.setVisible(true);
        detailsBox.setManaged(true);

        nameLabel.setText(dto.getProductName());
        upcLabel.setText(dto.getUpc());
        manufacturerLabel.setText(dto.getManufacturer());
        characteristicsLabel.setText(dto.getCharacteristics());
        stockLabel.setText(String.valueOf(dto.getProductsNumber()));

        if (dto.isPromotional()) {
            double promoPrice = dto.getSellingPrice();
            double basePrice = promoPrice / 0.8;

            priceLabel.setText(String.format("%.2f грн", basePrice));
            promoPriceLabel.setText(String.format("%.2f грн", promoPrice));
        } else {
            priceLabel.setText(String.format("%.2f грн", dto.getSellingPrice()));
            promoPriceLabel.setText("---");
        }
    }

    @FXML
    public void handleUpcCheck(ActionEvent event) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Швидкий пошук за UPC");

        TextField upcInput = new TextField();
        upcInput.setPromptText("UPC...");

        TableView<StoreProductDTO> resultTable = new TableView<>();
        resultTable.setPrefHeight(150);
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StoreProductDTO, String> upcCol = new TableColumn<>("UPC");
        upcCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUpc()));

        TableColumn<StoreProductDTO, String> nameCol = new TableColumn<>("Назва");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));

        TableColumn<StoreProductDTO, Double> priceCol = new TableColumn<>("Ціна");
        priceCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSellingPrice()));

        TableColumn<StoreProductDTO, Integer> stockCol = new TableColumn<>("К-ть");
        stockCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getProductsNumber()));

        resultTable.getColumns().addAll(upcCol, nameCol, priceCol, stockCol);

        upcInput.textProperty().addListener((observable, oldValue, newValue) -> {
            String code = newValue.trim();
            if (code.length() >= 1) {
                try {
                    List<StoreProductDTO> products = storeProductDAO.searchByUpcStartingWith(code);
                    resultTable.setItems(FXCollections.observableArrayList(products));
                } catch (SQLException ex) {
                    System.err.println("Помилка пошуку: " + ex.getMessage());
                }
            } else {
                resultTable.getItems().clear();
            }
        });

        VBox vbox = new VBox(10, new Label("Введіть UPC товару:"), upcInput, resultTable);
        vbox.setPadding(new javafx.geometry.Insets(20));
        vbox.setPrefWidth(550);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void clearDetails() {
        detailsBox.setVisible(false);
        detailsBox.setManaged(false);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void initData(User user) {
        this.currentUser = user;
    }
}