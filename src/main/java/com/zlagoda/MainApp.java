package com.zlagoda;

import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
       Scene scene = new Scene(root, 800, 500, Color.BLACK);

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
               stage.setMinWidth(800);
               stage.setMinHeight(500);
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

    private ParallelTransition introTransition(Group root){
        Rectangle r = new Rectangle(0, 0, 250, 250);
        r.setFill(Color.BLUE);
        root.getChildren().add(r);
        Text text = new Text("Welcome to Zlagoda");
        text.setFill(Color.GREEN);
        text.setFont(Font.font("Times new roman", FontWeight.BOLD, 50));
        text.setX(200);
        text.setY(250);
        root.getChildren().add(text);


        TranslateTransition translate =
                new TranslateTransition(Duration.millis(2000), r);
        translate.setToX(800);
        translate.setToY(500);

        FillTransition fill = new FillTransition(Duration.millis(2000), r);
        fill.setToValue(Color.RED);

        RotateTransition rotate = new RotateTransition(Duration.millis(2000), r);
        rotate.setToAngle(360);

        ScaleTransition scale = new ScaleTransition(Duration.millis(2000), r);
        scale.setToX(0.1);
        scale.setToY(0.1);

        RotateTransition textTransition = new RotateTransition(Duration.millis(2000), text);
        textTransition.setToAngle(360);


        FillTransition fillText = new FillTransition(Duration.millis(3000), text);
        fillText.setToValue(Color.RED);


        ParallelTransition transition = new ParallelTransition(
                translate, fill, rotate, scale, textTransition, fillText);
        transition.setCycleCount(1);
        transition.setAutoReverse(true);
        return transition;
    }


}