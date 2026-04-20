package com.zlagoda.controller.manager;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.zlagoda.dao.*;
import com.zlagoda.dto.StoreProductDTO;
import com.zlagoda.model.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.awt.Desktop;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ManagerReportsController {

    @FXML
    private DatePicker reportStartDatePicker;
    @FXML
    private DatePicker reportEndDatePicker;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final Customer_CardDAO customerDAO = new Customer_CardDAO();
    private final CheckDAO checkDAO = new CheckDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final Store_ProductDAO storeProductDAO = new Store_ProductDAO();

    private static final String ARIAL_FONT = "src/main/resources/ARIAL.TTF";

    @FXML
    private void handlePrintEmployeesReport() {
        try {
            List<Employee> data = employeeDAO.getAllEmployeesOrderBySurname();
            openPreview(controller -> controller.setEmployeeData(data, this), "Працівники");
        } catch (SQLException e) {
            showAlert("Помилка БД", "Не вдалося отримати дані працівників", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePrintClientsReport() {
        try {
            List<Customer_Card> data = customerDAO.getAllCustomerCardsOrderBySurname();
            openPreview(controller -> controller.setClientData(data, this), "Клієнти");
        } catch (SQLException e) {
            showAlert("Помилка БД", "Не вдалося отримати дані клієнтів", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePrintChecksReport() {
        LocalDate start = reportStartDatePicker.getValue();
        LocalDate end = reportEndDatePicker.getValue();
        if (start == null || end == null) {
            showAlert("Увага", "Оберіть початкову та кінцеву дати", Alert.AlertType.WARNING);
            return;
        }
        try {
            List<Check> data = checkDAO.getChecksByPeriod(start.atStartOfDay(), end.atTime(23, 59, 59));
            openPreview(controller -> controller.setCheckData(data, this), "Чеки");
        } catch (SQLException e) {
            showAlert("Помилка БД", "Не вдалося отримати чеки", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePrintCategoriesReport() {
        try {
            List<Category> data = categoryDAO.getAllCategoriesOrderByName();
            openPreview(controller -> controller.setCategoryData(data, this), "Категорії");
        } catch (SQLException e) {
            showAlert("Помилка БД", "Не вдалося отримати категорії", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePrintProductsReport() {
        try {
            List<Product> data = productDAO.getAllProductsOrderByName();
            openPreview(controller -> controller.setProductData(data, this), "Товари");
        } catch (SQLException e) {
            showAlert("Помилка БД", "Не вдалося отримати список товарів", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePrintStoreProductsReport() {
        try {
            List<StoreProductDTO> data = storeProductDAO.getAllStoreProductsOrderByName();

            openPreview(controller -> controller.setStoreProductData(data, this), "Товари у магазині");
        } catch (SQLException e) {
            showAlert("Помилка БД", "Не вдалося отримати товари в магазині", Alert.AlertType.ERROR);
        }
    }

    private void openPreview(java.util.function.Consumer<ReportPreviewController> dataSetter, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager/report_preview.fxml"));
            Parent root = loader.load();
            ReportPreviewController controller = loader.getController();
            dataSetter.accept(controller);
            showStage(root, "Перегляд звіту: " + title);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося відкрити вікно перегляду", Alert.AlertType.ERROR);
        }
    }

    public void executeFinalExport(List<?> data, String type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("Report_" + type + "_" + LocalDate.now() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                PdfFont font = PdfFontFactory.createFont(ARIAL_FONT, "Identity-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

                document.add(new Paragraph("МЕРЕЖА МАГАЗИНІВ «ЗЛАГОДА»").setFont(font).setFontSize(10));
                document.add(new Paragraph(getReportTitleByType(type)).setFont(font).setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("Дата формування: " + LocalDate.now()).setFont(font).setTextAlignment(TextAlignment.RIGHT));
                document.add(new Paragraph("\n"));

                Table table = createTableByType(type, data, font);
                if (table != null) {
                    document.add(table.useAllAvailableWidth());
                }

                document.add(new Paragraph("\nПідпис відповідальної особи: ____________________").setFont(font).setFontSize(10));

                document.close();
                showOpenReportDialog(file);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Помилка експорту", "Не вдалося створити PDF: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private String getReportTitleByType(String type) {
        return switch (type) {
            case "EMPLOYEES" -> "ЗВІТ ПО ПРАЦІВНИКАХ";
            case "CLIENTS" -> "ЗВІТ ПО КЛІЄНТАХ";
            case "CHECKS" -> "ЗВІТ ПО ЧЕКАХ";
            case "CATEGORIES" -> "ЗВІТ ПО КАТЕГОРІЯХ";
            case "PRODUCTS" -> "ЗВІТ ПО УСІХ ТОВАРАХ";
            case "STORE_PRODUCTS" -> "ЗВІТ ПО ТОВАРАХ У МАГАЗИНІ";
            default -> "ЗВІТ";
        };
    }

    private Table createTableByType(String type, List<?> data, PdfFont font) {
        return switch (type) {
            case "EMPLOYEES" -> createEmployeePdfTable((List<Employee>) data, font);
            case "CLIENTS" -> createClientPdfTable((List<Customer_Card>) data, font);
            case "CHECKS" -> createCheckPdfTable((List<Check>) data, font);
            case "CATEGORIES" -> createCategoryPdfTable((List<Category>) data, font);
            case "PRODUCTS" -> createProductPdfTable((List<Product>) data, font);
            case "STORE_PRODUCTS" -> createStoreProductPdfTable((List<com.zlagoda.dto.StoreProductDTO>) data, font);
            default -> null;
        };
    }


    private Table createCategoryPdfTable(List<Category> data, PdfFont font) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4}));
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setFont(font).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Назва категорії").setFont(font).setBold()));
        for (Category c : data) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(c.getCategory_number())).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(c.getName()).setFont(font)));
        }
        return table;
    }

    private Table createProductPdfTable(List<Product> data, PdfFont font) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 3}));
        String[] headers = {"ID", "Назва", "Виробник", "Характеристики"};
        for (String h : headers) table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(font).setBold()));
        for (Product p : data) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getId_product())).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(p.getName()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(p.getManufacturer()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(p.getCharacteristics()).setFont(font)));
        }
        return table;
    }

    private Table createStoreProductPdfTable(List<com.zlagoda.dto.StoreProductDTO> data, PdfFont font) {

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 2, 1, 1, 1}));
        String[] headers = {"UPC", "Назва", "Категорія", "Ціна", "К-сть", "Акція"};

        for (String h : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(font).setBold()));
        }

        for (com.zlagoda.dto.StoreProductDTO item : data) {
            table.addCell(new Cell().add(new Paragraph(item.getUpc()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(item.getProductName()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(item.getCategoryName()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getSellingPrice())).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getProductsNumber())).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(item.isPromotional() ? "Так" : "Ні").setFont(font)));
        }
        return table;
    }


    private Table createEmployeePdfTable(List<Employee> data, PdfFont font) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2}));
        String[] headers = {"ID", "Прізвище", "Ім'я", "Роль", "Телефон"};
        for (String h : headers) table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(font).setBold()));
        for (Employee e : data) {
            table.addCell(new Cell().add(new Paragraph(e.getId_employee()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(e.getSurname()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(e.getName()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(e.getRole()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(e.getPhone_number()).setFont(font)));
        }
        return table;
    }


    private Table createClientPdfTable(List<Customer_Card> data, PdfFont font) {

        Table table = new Table(UnitValue.createPercentArray(new float[]{1.5f, 1.5f, 1.2f, 1.2f, 1.5f, 1.2f, 1.5f, 1f, 0.7f}));

        String[] headers = {
                "№ Карти", "Прізвище", "Ім'я", "По батькові",
                "Телефон", "Місто", "Вулиця", "Індекс", "%"
        };

        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFont(font).setBold().setFontSize(8))
                    .setTextAlignment(TextAlignment.CENTER));
        }

        for (Customer_Card c : data) {
            table.addCell(new Cell().add(new Paragraph(c.getCard_number() != null ? c.getCard_number() : "").setFont(font).setFontSize(7)));
            table.addCell(new Cell().add(new Paragraph(c.getSurname() != null ? c.getSurname() : "").setFont(font).setFontSize(7)));
            table.addCell(new Cell().add(new Paragraph(c.getName() != null ? c.getName() : "").setFont(font).setFontSize(7)));
            table.addCell(new Cell().add(new Paragraph(c.getPatronymic() != null ? c.getPatronymic() : "").setFont(font).setFontSize(7)));
            table.addCell(new Cell().add(new Paragraph(c.getPhone_number() != null ? c.getPhone_number() : "").setFont(font).setFontSize(7)));
            table.addCell(new Cell().add(new Paragraph(c.getCity() != null ? c.getCity() : "").setFont(font).setFontSize(7)));
            table.addCell(new Cell().add(new Paragraph(c.getStreet() != null ? c.getStreet() : "").setFont(font).setFontSize(7)));
            table.addCell(new Cell().add(new Paragraph(c.getZip_code() != null ? c.getZip_code() : "").setFont(font).setFontSize(7)));
            table.addCell(new Cell().add(new Paragraph(c.getPercent() + "%").setFont(font).setFontSize(7)));
        }
        return table;
    }


    private Table createCheckPdfTable(List<Check> data, PdfFont font) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 3, 2}));
        String[] headers = {"№ Чеку", "ID Касира", "Дата", "Сума"};
        for (String h : headers) table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(font).setBold()));
        for (Check c : data) {
            table.addCell(new Cell().add(new Paragraph(c.getCheck_number()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(c.getId_employee()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(c.getPrint_date().toString()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(c.getSum_total())).setFont(font)));
        }
        return table;
    }


    private void showOpenReportDialog(File file) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Успіх");
        alert.setHeaderText("Звіт успішно збережено!");
        alert.setContentText("Бажаєте відкрити файл для перегляду?");
        ButtonType btnOpen = new ButtonType("ВІДКРИТИ");
        ButtonType btnClose = new ButtonType("ЗАКРИТИ", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnOpen, btnClose);
        alert.showAndWait().ifPresent(response -> {
            if (response == btnOpen) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (Exception e) {
                    showAlert("Помилка", "Не вдалося відкрити файл", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showStage(Parent root, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private void showAlert(String title, String text, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private String nonNull(String str) {
        return (str == null || str.trim().isEmpty()) ? "-" : str;
    }
}