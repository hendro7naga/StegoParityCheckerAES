package about;

import interfaces.OpenScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import kelas.AlertInfo;

/**
 * Created by hendro.sinaga on 24-Jul-16.
 */
public class AboutControl implements OpenScene {
    @FXML
    Button btnMainMenu;

    @FXML void handleMainMenu (ActionEvent actionEvent) {
        try {
            open("main", 756.0, 485.0);
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
