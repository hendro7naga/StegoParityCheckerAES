package stegano;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import main.Main;

import java.io.IOException;

/**
 * Created by hendro.sinaga on 08-Jun-16.
 */
public class EmbeddingController {
    @FXML
    private void handleBtnMainMenu(ActionEvent event) {
        Parent p = null;
        boolean loadSukses = true;

        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("main/maindoc.fxml"));
            if (p == null) {
                loadSukses = false;
            }
        } catch (IOException ex) {
            loadSukses = false;
        }

        if (!loadSukses) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Gagal membuka scene maindoc fxml",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi");
            alert.setHeaderText("File tidak ditemukan");
            alert.show();
        }
        else {
            Main.mainStage.setTitle("Aplikasi Steganografi");
            Main.mainStage.setScene(new Scene(p));
        }
    }
}
