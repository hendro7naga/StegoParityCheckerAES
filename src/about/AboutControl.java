package about;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import kelas.AlertInfo;
import main.Main;

/**
 * Created by hendro.sinaga on 24-Jul-16.
 */
public class AboutControl {
    @FXML
    Button btnMainMenu;

    @FXML void handleMainMenu (ActionEvent actionEvent) {
        try {
            System.gc();
            Main.mainStage.setTitle("Aplikasi Steganografi");
            Main.mainStage.getScene().setRoot(
                    FXMLLoader.load(getClass().getClassLoader().getResource("main/maindoc.fxml"))
            );
            Main.mainStage.sizeToScene();
            Main.mainStage.centerOnScreen();
        } catch (Exception e) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Load main scene",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }
    }
}
