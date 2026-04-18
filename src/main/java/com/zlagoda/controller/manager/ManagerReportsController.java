package com.zlagoda.controller.manager;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.zlagoda.dao.Customer_CardDAO;
import com.zlagoda.model.Customer_Card;
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

import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;

import java.time.LocalDate;
import java.util.List;

public class ManagerReportsController {

    @FXML
    private Button printClientsReport;
    @FXML
    private DatePicker reportDatePicker;

    private final Customer_CardDAO customerDAO = new Customer_CardDAO();

    @FXML
    public void initialize() {
        if (reportDatePicker != null) {
            reportDatePicker.setValue(LocalDate.now());
        }
    }

    @FXML
    private void handlePrintClientsReport() {
        try {
            LocalDate selectedDate = (reportDatePicker != null && reportDatePicker.getValue() != null)
                    ? reportDatePicker.getValue()
                    : LocalDate.now();

            // 1. Етап налаштування (Вікно підтвердження)
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

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Зберегти PDF звіт");
                fileChooser.setInitialFileName("Clients_Report_" + selectedDate + ".pdf");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
                File file = fileChooser.showSaveDialog(null);

                if (file != null) {
                    generateClientsPdf(clients, file.getAbsolutePath());

                    showOpenReportDialog(file);
                }
            }

        } catch (Exception e) {
            showAlert("Помилка", "Не вдалося створити звіт: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showOpenReportDialog(File file) {
        Alert openAlert = new Alert(Alert.AlertType.INFORMATION);
        openAlert.setTitle("Звіт готовий");
        openAlert.setHeaderText("Файл успішно збережено!");
        openAlert.setContentText("Бажаєте переглянути звіт зараз?");

        ButtonType openBtn = new ButtonType("Відкрити");
        ButtonType closeBtn = new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE);

        openAlert.getButtonTypes().setAll(openBtn, closeBtn);

        openAlert.showAndWait().ifPresent(type -> {
            if (type == openBtn) {
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    }
                } catch (Exception e) {
                    System.out.println("Не вдалося відкрити файл: " + e.getMessage());
                }
            }
        });
    }

    private void generateClientsPdf(List<Customer_Card> clients, String dest) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        String fontPath = "D:\\Work\\Nonna\\унік\\bd\\arial\\ARIAL.TTF";
        PdfFont ukrainianFont = PdfFontFactory.createFont(fontPath, "Identity-H");

        document.setFont(ukrainianFont);

        document.add(new Paragraph("МЕРЕЖА МАГАЗИНІВ 'ЗЛАГОДА'")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(20));

        document.add(new Paragraph("ЗВІТ: ПОСТІЙНІ КЛІЄНТИ")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14));

        document.add(new Paragraph("Дата формування: " + LocalDate.now() + "\n\n"));

        float[] columnWidths = {2, 4, 4, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        table.addHeaderCell(new Cell().add(new Paragraph("№ Карти").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Прізвище").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Ім'я").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Знижка %").setBold()));

        // Заповнюємо даними
        for (Customer_Card client : clients) {
            table.addCell(new Cell().add(new Paragraph(client.getCard_number())));
            table.addCell(new Cell().add(new Paragraph(client.getSurname())));
            table.addCell(new Cell().add(new Paragraph(client.getName())));
            table.addCell(new Cell().add(new Paragraph(client.getPercent() + "%")));
        }

        document.add(table);
        document.close();
    }

    private void printNode(Node node) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {

            boolean proceed = job.showPrintDialog(null);
            if (proceed) {
                boolean success = job.printPage(node);
                if (success) {
                    job.endJob();
                    showAlert("Успіх", "Звіт відправлено на друк/збереження.", Alert.AlertType.INFORMATION);
                }
            }
        } else {
            showAlert("Помилка", "Не вдалося ініціалізувати принтер.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePrintEmployeesReport() {
    }

    @FXML
    private void handlePrintProductsReport() {
    }

    @FXML
    private void handlePrintChecksReport() {
    }

    @FXML
    private void handlePrintStoreProductsReport() {
    }

    @FXML
    private void handlePrintCategoriesReport() {
    }

    private void showAlert(String title, String text, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}