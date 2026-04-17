package com.zlagoda.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class CashierMainController {
    private final ContextMenu profileMenu = new ContextMenu();

    @FXML
    public void initialize() {
        // Контейнер для стилізації через CSS
        profileMenu.getStyleClass().add("minimal-profile-menu");

        // Інформаційний пункт (Ім'я)
        MenuItem infoItem = new MenuItem("Касир Піпяо");
        infoItem.setDisable(true);
        infoItem.setStyle("-fx-opacity: 1.0; -fx-font-weight: bold; -fx-padding: 10 20 5 20; -fx-text-fill: #2c3e50;");

        // Роль
        MenuItem roleItem = new MenuItem("Працівник (Manager)");
        roleItem.setDisable(true);
        roleItem.setStyle("-fx-opacity: 0.7; -fx-font-size: 11px; -fx-padding: 0 20 10 20;");

        MenuItem settingsItem = new MenuItem("⚙ Налаштування");
        MenuItem logoutItem = new MenuItem("🚪 Вийти");

        logoutItem.setOnAction(e -> handleLogout());

        profileMenu.getItems().addAll(infoItem, roleItem, new SeparatorMenuItem(), settingsItem, logoutItem);
        profileMenu.setAutoHide(true);
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        Node source = (Node) event.getSource();
        if (profileMenu.isShowing()) {
            profileMenu.hide();
        } else {
            // Відображаємо меню трохи нижче іконки
            profileMenu.show(source, event.getScreenX() - 120, event.getScreenY() + 10);
        }
    }

    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) profileMenu.getOwnerWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

