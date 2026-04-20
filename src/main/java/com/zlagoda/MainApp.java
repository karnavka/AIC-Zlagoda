package com.zlagoda;

import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Group root = new Group();

        Scene scene = new Scene(root, 900, 600, Color.web("#160822"));

        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.setTitle("JavaFX Scene Graph Demo");
        stage.setScene(scene);
        stage.show();

        ParallelTransition transition = introTransition(root);

        transition.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        MainApp.class.getResource("/fxml/login.fxml")
                );

                Scene loginScene = new Scene(loader.load(), 900, 600);

                stage.setTitle("Zlagoda AIS");
                stage.setScene(loginScene);
                stage.setMinWidth(900);
                stage.setMinHeight(600);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        transition.play();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private ParallelTransition introTransition(Group root) {
        Image ponyImage = new Image(
                MainApp.class.getResource("/images/pony.gif").toExternalForm()
        );

        ImageView ponyView = new ImageView(ponyImage);
        ponyView.setFitWidth(250);
        ponyView.setFitHeight(250);
        ponyView.setPreserveRatio(true);
        ponyView.setX(40);
        ponyView.setY(40);

        DropShadow ponyShadow = new DropShadow();
        ponyShadow.setRadius(25);
        ponyShadow.setColor(Color.web("#d88cff"));
        ponyView.setEffect(ponyShadow);

        Text text = new Text("Welcome to Zlagoda");
        text.setFill(Color.web("#f4dcff"));
        text.setFont(Font.font("Times New Roman", FontWeight.BOLD, 50));
        text.setX(230);
        text.setY(320);

        DropShadow textShadow = new DropShadow();
        textShadow.setOffsetY(3.0);
        textShadow.setOffsetX(3.0);
        textShadow.setRadius(15);
        textShadow.setColor(Color.web("#9a4dff"));
        text.setEffect(textShadow);

        root.getChildren().addAll(ponyView, text);

        TranslateTransition translate =
                new TranslateTransition(Duration.millis(2200), ponyView);
        translate.setToX(620);
        translate.setToY(280);

        RotateTransition rotate =
                new RotateTransition(Duration.millis(2200), ponyView);
        rotate.setToAngle(360);

        ScaleTransition scale =
                new ScaleTransition(Duration.millis(2200), ponyView);
        scale.setToX(0.4);
        scale.setToY(0.4);

        FadeTransition fadePony =
                new FadeTransition(Duration.millis(2200), ponyView);
        fadePony.setFromValue(1.0);
        fadePony.setToValue(0.75);

        RotateTransition textRotate =
                new RotateTransition(Duration.millis(1900), text);
        textRotate.setToAngle(360);



        FillTransition fillText =
                new FillTransition(Duration.millis(2200), text);
        fillText.setToValue(Color.web("#ffb3ff"));

        FadeTransition fadeText =
                new FadeTransition(Duration.millis(2200), text);
        fadeText.setFromValue(1.0);
        fadeText.setToValue(0.85);

        ParallelTransition transition = new ParallelTransition(
                translate,
                rotate,
                scale,
                fadePony,
                textRotate,

                fillText,
                fadeText
        );

        transition.setCycleCount(1);
        transition.setAutoReverse(true);

        return transition;
    }
}