package stegano;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import kelas.AlertInfo;
import main.AppControll;
import main.Main;
import main.MainController;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by hendro.sinaga on 15-Jul-16.
 */
public class TestResult {
    @FXML
    Button btnBacaData, btnMainMenu;
    @FXML
    TableView tableViewData;
    @FXML
    TextArea textAreaData;


    @FXML void handleBacaData (ActionEvent actionEvent) {

        ResultSet rs = null;
        try {
            if (!MainController.appControll.getInitializeStatus()) {
                MainController.appControll.init();
            }
            if (!MainController.appControll.sqLiteDB.getInitializeStatus()) {
                MainController.appControll.sqLiteDB.initialize(AppControll.DB_NAME);
            }
            String sql = "SELECT * FROM " + AppControll.TABLE_STEGANO_NAME;
            rs = MainController.appControll.sqLiteDB.SelectQuery(sql);
            while (rs.next()) {
                this.textAreaData.appendText(
                        rs.getInt("id") + "  "
                        + rs.getString("oriImageName") + "\t"
                        + rs.getString("stegoImageName") + "\t"
                        + rs.getInt("msgLength") + "\t"
                        + rs.getDouble("mseVal") + "\t"
                        + rs.getDouble("psnrVal") + "\n"
                );
            }
            rs.close();
            MainController.appControll.sqLiteDB.closeConnection();
        } catch (Exception e) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Database",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }
    }

    @FXML void handleMainMenu (ActionEvent actionEvent) {
        Parent parent = null;
        boolean sukses = false;
        try {
            parent = FXMLLoader.load(getClass().getClassLoader().getResource("main/maindoc.fxml"));
            if (parent != null) {
                sukses = true;
            }
        } catch (IOException ex) {
            sukses = false;
        }

        if (!sukses) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Open Scene",
                    "Gagal membuka scene Main Menu",
                    ButtonType.OK
            );
        } else {
            Main.mainStage.setTitle("Aplikasi Steganografi");
            Main.mainStage.setScene(new Scene(parent));
            Main.mainStage.centerOnScreen();
        }

    }
}
