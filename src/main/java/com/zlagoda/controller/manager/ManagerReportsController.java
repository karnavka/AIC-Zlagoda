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

import com.zlagoda.dao.CheckDAO;
import com.zlagoda.dao.Customer_CardDAO;
import com.zlagoda.dao.EmployeeDAO;
import com.zlagoda.model.Check;
import com.zlagoda.model.Customer_Card;
import com.zlagoda.model.Employee;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ManagerReportsController {

    @FXML
    private DatePicker reportDatePicker;
    @FXML
    private DatePicker reportStartDatePicker;
    @FXML
    private DatePicker reportEndDatePicker;

    private final Customer_CardDAO customerDAO = new Customer_CardDAO();
    private final CheckDAO checkDAO = new CheckDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @FXML
    public void initialize() {
        LocalDate today = LocalDate.now();
        if (reportDatePicker != null) reportDatePicker.setValue(today);
        if (reportStartDatePicker != null) reportStartDatePicker.setValue(today.minusWeeks(1));
        if (reportEndDatePicker != null) reportEndDatePicker.setValue(today);
    }

    // --- 1. ЗВІТ ПО КЛІЄНТАХ ---
    @FXML
    private void handlePrintClientsReport() {
        try {
            LocalDate selectedDate = (reportDatePicker != null && reportDatePicker.getValue() != null)
                    ? reportDatePicker.getValue() : LocalDate.now();

            if (showConfirmAlert("Клієнти", "Сформувати список усіх постійних клієнтів?")) {
                List<Customer_Card> clients = customerDAO.getAllCustomerCardsOrderBySurname();

                if (clients.isEmpty()) {
                    showAlert("Увага", "Список клієнтів порожній.", Alert.AlertType.WARNING);
                    return;
                }

                File file = getSaveLocation("Clients_Report_" + selectedDate + ".pdf");
                if (file != null) {
                    generateClientsPdf(clients, file.getAbsolutePath(), selectedDate);
                    showOpenReportDialog(file);
                }
            }
        } catch (Exception e) {
            showAlert("Помилка", "Не вдалося створити звіт: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // --- 2. ЗВІТ ПО ЧЕКАХ ЗА ПЕРІОД ---
    @FXML
    private void handlePrintChecksReport() {
        try {
            LocalDate start = reportStartDatePicker.getValue();
            LocalDate end = reportEndDatePicker.getValue();

            if (start == null || end == null || start.isAfter(end)) {
                showAlert("Помилка", "Оберіть коректний період дат.", Alert.AlertType.WARNING);
                return;
            }

            if (showConfirmAlert("Чеки", "Сформувати звіт по чеках за період: " + start + " - " + end + "?")) {
                List<Check> checks = checkDAO.getChecksByPeriod(start.atStartOfDay(), end.atTime(23, 59, 59));

                if (checks.isEmpty()) {
                    showAlert("Інформація", "За цей період чеків не знайдено.", Alert.AlertType.INFORMATION);
                    return;
                }

                File file = getSaveLocation("Checks_Report_" + start + "_to_" + end + ".pdf");
                if (file != null) {
                    generateChecksPdf(checks, file.getAbsolutePath(), start, end);
                    showOpenReportDialog(file);
                }
            }
        } catch (Exception e) {
            showAlert("Помилка", "Помилка при генерації чеків: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // --- 3. ЗВІТ ПО ПРАЦІВНИКАХ ---
    @FXML
    private void handlePrintEmployeesReport() {
        try {
            if (showConfirmAlert("Працівники", "Сформувати звіт про персонал магазину?")) {
                List<Employee> employees = employeeDAO.getAllEmployeesOrderBySurname();

                if (employees.isEmpty()) {
                    showAlert("Увага", "Список працівників порожній.", Alert.AlertType.WARNING);
                    return;
                }

                File file = getSaveLocation("Employees_Report_" + LocalDate.now() + ".pdf");
                if (file != null) {
                    generateEmployeesPdf(employees, file.getAbsolutePath());
                    showOpenReportDialog(file);
                }
            }
        } catch (Exception e) {
            showAlert("Помилка", "Помилка звіту працівників: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void generateClientsPdf(List<Customer_Card> clients, String dest, LocalDate date) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setFont(getPdfFont());

        document.add(new Paragraph("ЗВІТ: ПОСТІЙНІ КЛІЄНТИ").setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(18));
        document.add(new Paragraph("Станом на: " + date + "\n\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 4, 4, 2})).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("№ Карти").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Прізвище").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Ім'я").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Знижка %").setBold()));

        for (Customer_Card c : clients) {
            table.addCell(c.getCard_number());
            table.addCell(c.getSurname());
            table.addCell(c.getName());
            table.addCell(c.getPercent() + "%");
        }
        document.add(table);
        document.close();
    }

    private void generateChecksPdf(List<Check> checks, String dest, LocalDate start, LocalDate end) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setFont(getPdfFont());

        document.add(new Paragraph("ЗВІТ ПО ПРОДАЖАХ (ЧЕКИ)").setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(18));
        document.add(new Paragraph("Період: " + start + " - " + end + "\n\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 4, 2})).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("№ Чека").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Дата").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Касир").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Сума").setBold()));

        double total = 0;
        for (Check c : checks) {
            table.addCell(c.getCheck_number());
            table.addCell(c.getPrint_date().toLocalDate().toString());
            table.addCell(c.getId_employee());
            table.addCell(String.format("%.2f", c.getSum_total()));
            total += c.getSum_total();
        }
        document.add(table);
        document.add(new Paragraph("\nРАЗОМ ЗА ПЕРІОД: " + String.format("%.2f", total) + " грн").setBold().setTextAlignment(TextAlignment.RIGHT));
        document.close();
    }

    private void generateEmployeesPdf(List<Employee> employees, String dest) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setFont(getPdfFont());

        document.add(new Paragraph("ЗВІТ: ПЕРСОНАЛ").setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(18));
        document.add(new Paragraph("Дата формування: " + LocalDate.now() + "\n\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 2})).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("Прізвище").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Ім'я").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Посада").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Зарплата").setBold()));

        for (Employee e : employees) {

            table.addCell(e.getSurname());
            table.addCell(e.getName());
            table.addCell(e.getRole());
            table.addCell(String.format("%.2f", e.getSalary()));
        }
        document.add(table);
        document.close();
    }

    private PdfFont getPdfFont() throws Exception {

        String fontPath = "src/main/resources/ARIAL.TTF";
        return PdfFontFactory.createFont(fontPath, "Identity-H");
    }

    private File getSaveLocation(String defaultName) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Зберегти звіт");
        fc.setInitialFileName(defaultName);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        return fc.showSaveDialog(null);
    }

    private boolean showConfirmAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Підтвердження");
        alert.setHeaderText(title);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showOpenReportDialog(File file) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успіх");
        alert.setHeaderText("Звіт збережено!");
        alert.setContentText("Бажаєте переглянути файл зараз?");

        ButtonType openBtn = new ButtonType("Відкрити");
        ButtonType closeBtn = new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(openBtn, closeBtn);

        alert.showAndWait().ifPresent(type -> {
            if (type == openBtn && Desktop.isDesktopSupported()) {
                try { Desktop.getDesktop().open(file); } catch (Exception ignored) {}
            }
        });
    }

    private void showAlert(String title, String text, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    @FXML private void handlePrintProductsReport() {}
    @FXML private void handlePrintStoreProductsReport() {}
    @FXML private void handlePrintCategoriesReport() {}
}