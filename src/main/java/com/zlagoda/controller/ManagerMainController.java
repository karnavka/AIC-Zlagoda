package com.zlagoda.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;

public class ManagerMainController {
    private final ContextMenu profileMenu = new ContextMenu();

    @FXML
    public void initialize() {
        MenuItem infoItem = new MenuItem("Мій профіль: Менеджер Ніф-ніф");
        MenuItem settingsItem = new MenuItem("Налаштування");
        MenuItem logoutItem = new MenuItem("Вийти з системи");


        logoutItem.setOnAction(e -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                javafx.scene.Parent root = loader.load();

                javafx.stage.Stage stage = (javafx.stage.Stage) ((MenuItem)e.getSource()).getParentPopup().getOwnerWindow();


                stage.setScene(new javafx.scene.Scene(root));
                stage.centerOnScreen(); // Опціонально: центруємо вікно
                stage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Помилка при поверненні на екран логіну.");
            }
        });

        profileMenu.getItems().addAll(infoItem, settingsItem, new SeparatorMenuItem(), logoutItem);

        profileMenu.setAutoHide(true);
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        if (profileMenu.isShowing()) {
            profileMenu.hide();
        } else {
            Node source = (Node) event.getSource();
            profileMenu.show(source, event.getScreenX(), event.getScreenY());
        }
    }
}
