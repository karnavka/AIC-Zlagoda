package com.zlagoda.controller;


import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

public class CashierMainController {
    @FXML
    private void handleProfileClick(MouseEvent event) {
        ContextMenu profileMenu = new ContextMenu();

        MenuItem infoItem = new MenuItem("Мій профіль: Касир Піпяо");
        MenuItem settingsItem = new MenuItem("Налаштування");
        MenuItem logoutItem = new MenuItem("Вийти з системи");

        infoItem.setDisable(true);
        infoItem.setStyle("-fx-opacity: 1.0; -fx-font-weight: bold;");

        // ЛОГІКА ВИХОДУ
        logoutItem.setOnAction(e -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                javafx.scene.Parent root = loader.load();

                javafx.stage.Stage stage = (javafx.stage.Stage) ((MenuItem)e.getSource()).getParentPopup().getOwnerWindow();

                // Якщо попередній спосіб не спрацює в ContextMenu, можна використати Node з івенту:
                // javafx.stage.Stage stage = (javafx.stage.Stage) ((Node)event.getSource()).getScene().getWindow();

                // 3. Встановлюємо нову сцену
                stage.setScene(new javafx.scene.Scene(root));
                stage.centerOnScreen(); // Опціонально: центруємо вікно
                stage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Помилка при поверненні на екран логіну.");
            }
        });

        profileMenu.getItems().addAll(infoItem, settingsItem, new javafx.scene.control.SeparatorMenuItem(), logoutItem);

        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        profileMenu.show(source, event.getScreenX(), event.getScreenY());
    }
}
