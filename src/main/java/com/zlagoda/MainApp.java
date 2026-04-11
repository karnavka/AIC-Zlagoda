package com.zlagoda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("slavaUkraini -> " + BCrypt.hashpw("slavaUkraini", BCrypt.gensalt()));
        System.out.println("HeroyamSlava -> " + BCrypt.hashpw("HeroyamSlava", BCrypt.gensalt()));
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(loader.load(), 900, 600);

        primaryStage.setTitle("Zlagoda AIS");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}