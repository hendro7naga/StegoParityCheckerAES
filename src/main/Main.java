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
        Font.loadFont(getClass().getResourceAsStream("/resources/fonts/ubuntu-c-webfont.ttf"), 14);
        Parent root = FXMLLoader.load(getClass().getResource("maindoc.fxml"));
        primaryStage.setTitle("Aplikasi Steganografi");
        primaryStage.setScene(new Scene(root, 756, 485));
        Main.mainStage = primaryStage;
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
