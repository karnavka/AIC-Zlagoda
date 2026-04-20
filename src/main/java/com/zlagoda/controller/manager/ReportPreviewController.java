package com.zlagoda.controller.manager;

import com.zlagoda.dto.StoreProductDTO;
import com.zlagoda.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ReportPreviewController {
    @FXML
    private Label reportTitleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private TableView<Object> previewTable;

    private List<?> dataToSave;
    private ManagerReportsController mainController;
    private String currentType;

    public void setEmployeeData(List<Employee> employees, ManagerReportsController controller) {
        this.dataToSave = employees;
        this.mainController = controller;
        this.currentType = "EMPLOYEES";

        reportTitleLabel.setText("ЗВІТ ПО ПРАЦІВНИКАХ");
        dateLabel.setText("Дата: " + LocalDate.now());

        previewTable.getColumns().clear();
        previewTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        addColumn("ID", "id_employee", 50);
        addColumn("Прізвище", "surname", 100);
        addColumn("Ім'я", "name", 100);
        addColumn("Роль", "role", 100);
        addColumn("Телефон", "phone_number", 120);
        addColumn("Зарплата", "salary", 100);

        previewTable.setItems(FXCollections.observableArrayList(employees));
    }

    public void setClientData(List<Customer_Card> clients, ManagerReportsController controller) {
        this.dataToSave = clients;
        this.mainController = controller;
        this.currentType = "CLIENTS";

        reportTitleLabel.setText("ЗВІТ ПО КЛІЄНТАХ");
        dateLabel.setText("Дата: " + LocalDate.now());

        previewTable.getColumns().clear();

        previewTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Задаємо фіксовану ширину, щоб вони не зтискалися
        addColumn("№ Карти", "card_number", 120);
        addColumn("Прізвище", "surname", 120);
        addColumn("Ім'я", "name", 100);
        addColumn("По батькові", "patronymic", 120);
        addColumn("Телефон", "phone_number", 120);
        addColumn("Місто", "city", 100);
        addColumn("Вулиця", "street", 150);
        addColumn("Індекс", "zip_code", 80);
        addColumn("Знижка (%)", "percent", 80);

        previewTable.setItems(FXCollections.observableArrayList(clients));
    }

    public void setCheckData(List<Check> checks, ManagerReportsController controller) {
        this.dataToSave = checks;
        this.mainController = controller;
        this.currentType = "CHECKS";

        reportTitleLabel.setText("ЗВІТ ПО ЧЕКАХ");
        dateLabel.setText("Дата: " + LocalDate.now());

        previewTable.getColumns().clear();
        previewTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        addColumn("№ Чеку", "check_number", 150);
        addColumn("ID Касира", "id_employee", 150);
        addColumn("Дата", "print_date", 180);
        addColumn("Сума", "sum_total", 120);

        previewTable.setItems(FXCollections.observableArrayList(checks));
    }

    public void setCategoryData(List<Category> categories, ManagerReportsController controller) {
        this.dataToSave = categories;
        this.mainController = controller;
        this.currentType = "CATEGORIES";

        reportTitleLabel.setText("ЗВІТ ПО КАТЕГОРІЯХ");
        dateLabel.setText("Дата: " + LocalDate.now());

        previewTable.getColumns().clear();
        previewTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        addColumn("Номер", "category_number", 100);
        addColumn("Назва категорії", "name", 400);

        previewTable.setItems(FXCollections.observableArrayList(categories));
    }

    public void setProductData(List<Product> products, ManagerReportsController controller) {
        this.dataToSave = products;
        this.mainController = controller;
        this.currentType = "PRODUCTS";

        reportTitleLabel.setText("ЗВІТ ПО УСІХ ТОВАРАХ");
        dateLabel.setText("Дата: " + LocalDate.now());

        previewTable.getColumns().clear();
        previewTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        addColumn("ID", "id_product", 50);
        addColumn("Назва", "name", 150);
        addColumn("Виробник", "manufacturer", 120);
        addColumn("Характеристики", "characteristics", 200);
        addColumn("№ Категорії", "category_number", 100);

        previewTable.setItems(FXCollections.observableArrayList(products));
    }

    public void setStoreProductData(List<StoreProductDTO> storeProducts, ManagerReportsController controller) {
        this.dataToSave = storeProducts;
        this.mainController = controller;
        this.currentType = "STORE_PRODUCTS";

        reportTitleLabel.setText("ЗВІТ ПО ТОВАРАХ У МАГАЗИНІ");
        dateLabel.setText("Дата: " + LocalDate.now());

        previewTable.getColumns().clear();
        previewTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Використовуємо назви полів з вашого StoreProductDTO
        addColumn("UPC", "upc", 120);
        addColumn("Назва", "productName", 150);
        addColumn("Категорія", "categoryName", 120);
        addColumn("Ціна", "sellingPrice", 80);
        addColumn("К-сть", "productsNumber", 80);
        addColumn("Акція", "promotional", 80);

        previewTable.setItems(FXCollections.observableArrayList(storeProducts));
    }

    private void addColumn(String title, String property, double width) {
        TableColumn<Object, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        previewTable.getColumns().add(col);
    }

    @FXML
    private void handleSavePdf() {
        if (mainController != null && dataToSave != null) {
            mainController.executeFinalExport(dataToSave, currentType);
            handleClose();
        }
    }

    @FXML
    private void handleClose() {
        if (previewTable.getScene() != null && previewTable.getScene().getWindow() != null) {
            ((Stage) previewTable.getScene().getWindow()).close();
        }
    }
}