package stegano;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import kelas.AlertInfo;
import main.AppControll;
import main.Main;
import main.MainController;
import model.DataImperceptibility;
import model.DataRobustness;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by hendro.sinaga on 15-Jul-16.
 */
public class TestResult {
    private ObservableList<DataImperceptibility> dataImperceptibilities = FXCollections.observableArrayList();
    private ObservableList<DataRobustness> dataRobustness = FXCollections.observableArrayList();
    @FXML
    Button btnBacaDataImperceptibility, btnBacaDataRobustness, btnMainMenu;
    @FXML
    TableView<DataImperceptibility> tableViewDataImperceptibility;
    @FXML
    TableView<DataRobustness> tableViewDataRobustness;
    @FXML
    TableColumn<DataImperceptibility, Integer> noImperceptibilityTableCol;
    @FXML
    TableColumn<DataImperceptibility, String> oriImageNameImperceptibilityTableCol, stegoImageNameImperceptibilityTableCol;
    @FXML
    TableColumn<DataImperceptibility, Integer> txtLengthImperceptibilityTableCol;
    @FXML
    TableColumn<DataImperceptibility, Double> mseImperceptibilityTableCol, psnrImperceptibilityTableCol;

    @FXML void handleBacaDataImperceptibility (ActionEvent actionEvent) {
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
            this.dataImperceptibilities.clear();
            Integer counter = 1;
            while (rs.next()) {
                this.dataImperceptibilities.add(new DataImperceptibility(
                        counter,
                        rs.getString("oriImageName"),
                        rs.getString("stegoImageName"),
                        rs.getInt("msgLength"),
                        rs.getDouble("mseVal"),
                        rs.getDouble("psnrVal")
                ));
                /*this.textAreaDataImperceptibility.appendText(
                        rs.getInt("id") + "  "
                        + rs.getString("oriImageName") + "\t"
                        + rs.getString("stegoImageName") + "\t"
                        + rs.getInt("msgLength") + "\t"
                        + rs.getDouble("mseVal") + "\t"
                        + rs.getDouble("psnrVal") + "\n"
                );*/
                counter += 1;
            }
            this.tableViewDataImperceptibility.setItems(this.dataImperceptibilities);
            this.tableViewDataImperceptibility.autosize();
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

    @FXML void handleBacaDataRobustness (ActionEvent actionEvent) {
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM " + AppControll.TABLE_STEGANO_NOISE_NAME;
            rs = MainController.appControll.sqLiteDB.SelectQuery(sql);
            this.dataRobustness.clear();
            Integer counter = 1;
            while (rs.next()) {
                ResultSet rss = MainController.appControll.sqLiteDB.SelectQuery(
                        "SELECT stegoImageName FROM " + AppControll.TABLE_STEGANO_NAME + " WHERE "
                        + "id = " + rs.getInt("sid") + ";"
                );
                this.dataRobustness.add(new DataRobustness(
                        counter,
                        rss.getString("stegoImageName"),
                        rs.getDouble("noiseProb"),
                        rs.getDouble("percentage")
                ));
                /*this.textAreaDataRobustness.appendText(
                        rs.getInt("nid") + "  "
                                + rs.getInt("sid") + "\t"
                                + rss.getString("stegoImageName") + "\t"
                                + rs.getDouble("noiseProb") + "\t"
                                + rs.getDouble("percentage") + "\n"
                );*/
                counter += 1;
            }
            this.tableViewDataRobustness.setItems(this.dataRobustness);
            this.tableViewDataImperceptibility.autosize();
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
