package com.zlagoda.controller.manager;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.zlagoda.dao.Customer_CardDAO;
import com.zlagoda.dao.CheckDAO; // Не забудьте додати ваш DAO для чеків
import com.zlagoda.model.Customer_Card;
import com.zlagoda.model.Check; // Ваша модель чека
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.*;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ManagerReportsController {

    @FXML
    private DatePicker reportDatePicker;
    @FXML
    private DatePicker reportStartDatePicker; // З вашого FXML
    @FXML
    private DatePicker reportEndDatePicker;   // З вашого FXML

    private final Customer_CardDAO customerDAO = new Customer_CardDAO();
    private final CheckDAO checkDAO = new CheckDAO(); // Ініціалізація DAO для чеків

    @FXML
    public void initialize() {
        LocalDate today = LocalDate.now();
        if (reportDatePicker != null) reportDatePicker.setValue(today);
        if (reportStartDatePicker != null) reportStartDatePicker.setValue(today.minusWeeks(1));
        if (reportEndDatePicker != null) reportEndDatePicker.setValue(today);
    }

    // --- ЗВІТ ПО КЛІЄНТАХ ---
    @FXML
    private void handlePrintClientsReport() {
        try {
            LocalDate selectedDate = (reportDatePicker != null && reportDatePicker.getValue() != null)
                    ? reportDatePicker.getValue()
                    : LocalDate.now();

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Налаштування звіту");
            confirmAlert.setHeaderText("Сформувати звіт про постійних клієнтів?");
            confirmAlert.setContentText("Буде згенеровано список усіх клієнтів станом на: " + selectedDate);

            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
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

    @FXML
    private void handlePrintChecksReport() {
        try {
            LocalDate start = reportStartDatePicker.getValue();
            LocalDate end = reportEndDatePicker.getValue();

            if (start == null || end == null || start.isAfter(end)) {
                showAlert("Помилка дат", "Будь ласка, оберіть коректний період.", Alert.AlertType.WARNING);
                return;
            }
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Налаштування звіту");
            confirmAlert.setHeaderText("Сформувати звіт по чеках?");
            confirmAlert.setContentText("Буде згенеровано звіт за період:\nЗ: " + start + "\nПо: " + end);

            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

                List<Check> checks = checkDAO.getChecksByPeriod(start.atStartOfDay(), end.atTime(23, 59, 59));

                if (checks.isEmpty()) {
                    showAlert("Інформація", "За вказаний період чеків не знайдено.", Alert.AlertType.INFORMATION);
                    return;
                }

                File file = getSaveLocation("Checks_Report_" + start + "_to_" + end + ".pdf");

                if (file != null) {

                    generateChecksPdf(checks, file.getAbsolutePath(), start, end);

                    showOpenReportDialog(file);
                }
            }
        } catch (Exception e) {
            showAlert("Помилка", "Не вдалося згенерувати звіт по чеках: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void generateChecksPdf(List<Check> checks, String dest, LocalDate start, LocalDate end) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont font = PdfFontFactory.createFont("C:\\Windows\\Fonts\\arial.ttf", "Identity-H");
        document.setFont(font);

        document.add(new Paragraph("МЕРЕЖА МАГАЗИНІВ 'ЗЛАГОДА'")
                .setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(18));
        document.add(new Paragraph("ЗВІТ ПО ПРОДАЖАХ (ЧЕКИ)")
                .setTextAlignment(TextAlignment.CENTER).setFontSize(14));
        document.add(new Paragraph("Період: з " + start + " по " + end + "\n\n"));

        float[] columnWidths = {3, 3, 4, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        table.addHeaderCell(new Cell().add(new Paragraph("№ Чека").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Дата").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Касир").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Сума").setBold()));

        double totalSum = 0;
        for (Check c : checks) {
            table.addCell(new Cell().add(new Paragraph(c.getCheck_number())));
            table.addCell(new Cell().add(new Paragraph(c.getPrint_date().toString())));
            table.addCell(new Cell().add(new Paragraph(c.getId_employee()))); // Прізвище касира
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", c.getSum_total()))));
            totalSum += c.getSum_total();
        }

        document.add(table);
        document.add(new Paragraph("\nЗАГАЛЬНА СУМА ЗА ПЕРІОД: " + String.format("%.2f", totalSum) + " грн")
                .setBold().setTextAlignment(TextAlignment.RIGHT));

        document.close();
    }

    private void generateClientsPdf(List<Customer_Card> clients, String dest, LocalDate date) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont font = PdfFontFactory.createFont("C:\\Windows\\Fonts\\arial.ttf", "Identity-H");
        document.setFont(font);

        document.add(new Paragraph("МЕРЕЖА МАГАЗИНІВ 'ЗЛАГОДА'").setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(20));
        document.add(new Paragraph("ЗВІТ: ПОСТІЙНІ КЛІЄНТИ").setTextAlignment(TextAlignment.CENTER).setFontSize(14));
        document.add(new Paragraph("Станом на: " + date + "\n\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 4, 4, 2})).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("№ Карти").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Прізвище").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Ім'я").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Знижка %").setBold()));

        for (Customer_Card client : clients) {
            table.addCell(new Cell().add(new Paragraph(client.getCard_number())));
            table.addCell(new Cell().add(new Paragraph(client.getSurname())));
            table.addCell(new Cell().add(new Paragraph(client.getName())));
            table.addCell(new Cell().add(new Paragraph(client.getPercent() + "%")));
        }

        document.add(table);
        document.close();
    }

    // Допоміжні методи
    private File getSaveLocation(String defaultName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти PDF звіт");
        fileChooser.setInitialFileName(defaultName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        return fileChooser.showSaveDialog(null);
    }

    private void showOpenReportDialog(File file) {
        Alert openAlert = new Alert(Alert.AlertType.INFORMATION);
        openAlert.setTitle("Успіх");
        openAlert.setHeaderText("Звіт збережено!");
        openAlert.setContentText("Бажаєте переглянути звіт зараз?");

        ButtonType openBtn = new ButtonType("Відкрити");
        ButtonType closeBtn = new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE);
        openAlert.getButtonTypes().setAll(openBtn, closeBtn);

        openAlert.showAndWait().ifPresent(type -> {
            if (type == openBtn) {
                try {
                    if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
                } catch (Exception e) { e.printStackTrace(); }
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

    // Інші заглушки залишаються без змін
    @FXML private void handlePrintEmployeesReport() {}
    @FXML private void handlePrintProductsReport() {}
    @FXML private void handlePrintStoreProductsReport() {}
    @FXML private void handlePrintCategoriesReport() {}
}