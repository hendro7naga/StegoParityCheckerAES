package main;

/**
 * Created by hendro.sinaga on 08-Jun-16.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application{
    public static Stage mainStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("maindoc.fxml"));
        primaryStage.setTitle("Aplikasi Steganografi");
        primaryStage.setScene(new Scene(root, 765.5, 485));
        primaryStage.centerOnScreen();
        Main.mainStage = primaryStage;
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
