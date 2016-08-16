package stegano;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import kelas.AlertInfo;
import main.AppControll;
import main.Main;
import main.MainController;
import model.DataImperceptibility;
import model.DataRobustness;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.Optional;
/**
 * Created by hendro.sinaga on 15-Jul-16.
 */
public class TestResult {
    private ObservableList<DataImperceptibility> dataImperceptibilities = FXCollections.observableArrayList();
    private ObservableList<DataRobustness> dataRobustness = FXCollections.observableArrayList();
    @FXML
    Button btnBacaDataImperceptibility, btnBacaDataRobustness,
            btnExportToExcelImperceptibility, btnExportToExcelRobustness,
            btnclearDataImperceptibility, btnClearDataRobustness, btnMainMenu;
    @FXML
    TableView<DataImperceptibility> tableViewDataImperceptibility;
    @FXML
    TableView<DataRobustness> tableViewDataRobustness;
    @FXML
    TableColumn<DataImperceptibility, Integer> noImperceptibilityTableCol;
    @FXML
    TableColumn<DataImperceptibility, String> oriImageNameImperceptibilityTableCol,
                                              stegoImageNameImperceptibilityTableCol;
    @FXML
    TableColumn<DataImperceptibility, Integer> txtLengthImperceptibilityTableCol;
    @FXML
    TableColumn<DataImperceptibility, Double> mseImperceptibilityTableCol, psnrImperceptibilityTableCol;

    @FXML void handleBacaDataImperceptibility (ActionEvent actionEvent) {
        ResultSet rs = null;
        try {
            this.btnExportToExcelImperceptibility.setDisable(true);
            if (!MainController.appControll.getInitializeStatus()) {
                MainController.appControll.init();
            }
            if (!MainController.appControll.sqLiteDB.getInitializeStatus()) {
                MainController.appControll.sqLiteDB.initialize(AppControll.DB_NAME);
            }

            String sql = "SELECT mseVal, psnrVal, oriImageName, stegoImageName, msgLength FROM "
                    + AppControll.TABLE_STEGANO_IMPERCEPTIBILITY + " JOIN "
                    + AppControll.TABLE_STEGANO_MASTER + " ON "
                    + AppControll.TABLE_STEGANO_IMPERCEPTIBILITY + ".sid = "
                    + AppControll.TABLE_STEGANO_MASTER + ".id;";
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

                counter += 1;
            }
            this.tableViewDataImperceptibility.setItems(this.dataImperceptibilities);
            this.tableViewDataImperceptibility.autosize();
            rs.close();
            MainController.appControll.sqLiteDB.closeConnection();
            if (counter > 1)
                this.btnExportToExcelImperceptibility.setDisable(false);
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
            String sql = "SELECT * FROM " + AppControll.TABLE_STEGANO_ROBUSTNESS;
            rs = MainController.appControll.sqLiteDB.SelectQuery(sql);
            this.dataRobustness.clear();

            Integer counter = 1;
            while (rs.next()) {
                ResultSet rss = MainController.appControll.sqLiteDB.SelectQuery(
                        "SELECT stegoImageName FROM " + AppControll.TABLE_STEGANO_MASTER + " WHERE "
                        + "id = " + rs.getInt("sid") + ";"
                );
                this.dataRobustness.add(new DataRobustness(
                        counter,
                        rss.getString("stegoImageName"),
                        rs.getDouble("noiseProb"),
                        rs.getDouble("percentage")
                ));

                counter += 1;
            }
            this.tableViewDataRobustness.setItems(this.dataRobustness);
            this.tableViewDataImperceptibility.autosize();
            rs.close();
            MainController.appControll.sqLiteDB.closeConnection();
            if (counter > 1)
                this.btnExportToExcelRobustness.setDisable(false);
        } catch (Exception e) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Database",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }
    }

    @FXML void handleExportToExcelImperceptibility (ActionEvent actionEvent) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Imperceptibility");
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("No.");
        header.createCell(1).setCellValue("Original Image");
        header.createCell(2).setCellValue("Stego Image");
        header.createCell(3).setCellValue("Msg Length");
        header.createCell(4).setCellValue("MSE");
        header.createCell(5).setCellValue("PSNR");

        int indeks = 1;
        ResultSet rs = null;
        try {
            if (!MainController.appControll.getInitializeStatus()) {
                MainController.appControll.init();
            }
            if (!MainController.appControll.sqLiteDB.getInitializeStatus()) {
                MainController.appControll.sqLiteDB.initialize(AppControll.DB_NAME);
            }

            //String sql = "SELECT * FROM " + AppControll.TABLE_STEGANO_NAME + " WHERE psnrVal != 0";

            String sql = "SELECT mseVal, psnrVal, oriImageName, stegoImageName, msgLength FROM "
                    + AppControll.TABLE_STEGANO_IMPERCEPTIBILITY + " JOIN "
                    + AppControll.TABLE_STEGANO_MASTER + " ON "
                    + AppControll.TABLE_STEGANO_IMPERCEPTIBILITY + ".sid = "
                    + AppControll.TABLE_STEGANO_MASTER + ".id;";

            rs = MainController.appControll.sqLiteDB.SelectQuery(sql);

            Integer counter = 1;
            while (rs.next()) {
                XSSFRow row = sheet.createRow(indeks);
                row.createCell(0).setCellValue(counter);
                row.createCell(1).setCellValue(rs.getString("oriImageName"));
                row.createCell(2).setCellValue(rs.getString("stegoImageName"));
                row.createCell(3).setCellValue(rs.getInt("msgLength"));
                row.createCell(4).setCellValue(rs.getDouble("mseVal"));
                row.createCell(5).setCellValue(rs.getDouble("psnrVal"));

                counter += 1;
                indeks += 1;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Simpan Berkas Excel (.xlsx)");
            fileChooser.setInitialFileName("Imperceptibility_");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Micorosoft Excel", "*.xlsx"));
            File file = fileChooser.showSaveDialog(Main.mainStage);

            if (file != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                workbook.write(fileOutputStream);
                fileOutputStream.close();
                workbook.close();
                AlertInfo.showAlertInfoMessage(
                        "Informasi Aplikasi",
                        "Export Data ke Excel",
                        "Proses export dan penyimpanan file excel berhasil...",
                        ButtonType.OK
                );
            }

        } catch (Exception e) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Read Database dan Export",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }
    }

    @FXML void handleExportToExcelRobustness (ActionEvent actionEvent) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Imperceptibility");
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("No.");
        header.createCell(1).setCellValue("Stego Image");
        header.createCell(2).setCellValue("Noise (%)");
        header.createCell(3).setCellValue("Similarity (%)");
        header.getCell(1).getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        int indeks = 1;
        ResultSet rs = null;
        try {
            if (!MainController.appControll.getInitializeStatus()) {
                MainController.appControll.init();
            }
            if (!MainController.appControll.sqLiteDB.getInitializeStatus()) {
                MainController.appControll.sqLiteDB.initialize(AppControll.DB_NAME);
            }
            String sql = "SELECT noiseProb, percentage, stegoImageName FROM "
                    + AppControll.TABLE_STEGANO_ROBUSTNESS + " JOIN "
                    + AppControll.TABLE_STEGANO_MASTER
                    + " ON " + AppControll.TABLE_STEGANO_ROBUSTNESS + ".sid = "
                    + AppControll.TABLE_STEGANO_MASTER + ".id;";
            rs = MainController.appControll.sqLiteDB.SelectQuery(sql);

            Integer counter = 1;
            while (rs.next()) {
                XSSFRow row = sheet.createRow(indeks);
                row.createCell(0).setCellValue(counter);
                row.createCell(1).setCellValue(rs.getString("stegoImageName"));
                row.createCell(2).setCellValue(rs.getDouble("noiseProb"));
                row.createCell(3).setCellValue(rs.getDouble("percentage"));

                counter += 1;
                indeks += 1;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Simpan Berkas Excel (.xlsx)");
            fileChooser.setInitialFileName("Robustness_");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Micorosoft Excel", "*.xlsx"));
            File file = fileChooser.showSaveDialog(Main.mainStage);

            if (file != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                workbook.write(fileOutputStream);
                fileOutputStream.close();
                workbook.close();
                AlertInfo.showAlertInfoMessage(
                        "Informasi Aplikasi",
                        "Export Data ke Excel",
                        "Proses export dan penyimpanan berkas excel berhasil...",
                        ButtonType.OK
                );
            }

        } catch (Exception e) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Baca Database dan Eksport",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }
        finally {
            this.btnExportToExcelRobustness.setDisable(true);
        }
    }

    @FXML void handleClearDataImperceptibility (ActionEvent actionEvent) {
        Optional<ButtonType> optional = AlertInfo.showConfirmMessage(
                "Informasi Aplikasi",
                "Penghapusan Data Tabel di Database",
                "Proses penghapusan data Imperceptibility merupakan penghapusan keseluruhan data "
                + "(Stegano, Imperceptibility, Robustness).\n"
                +"Apakah Anda yakin akan menghapus seluruh data yang tersimpan di database?"
        );
        if (optional.get().equals(MainController.buttonTypeYes)) {
            try {
                //MainController.appControll.sqLiteDB.deleteTableData(AppControll.TABLE_STEGANO_IMPERCEPTIBILITY);
                MainController.appControll.sqLiteDB.deleteTableData(AppControll.TABLE_STEGANO_MASTER);
                AlertInfo.showAlertInfoMessage(
                        "Informasi Aplikasi",
                        "Hasil Proses",
                        "Proses penghapusan data robustness pada tabel berhasil dilakukan.",
                        ButtonType.OK
                );
                this.btnBacaDataRobustness.fire();
            } catch (Exception e) {
                AlertInfo.showAlertErrorMessage(
                        "Informasi Aplikasi",
                        "Penghapusan Data Tabel",
                        "Terjadi kesalahan: " + e.getMessage(),
                        ButtonType.OK
                );
            }
        }
    }

    @FXML void handleClearDataRobustness (ActionEvent actionEvent) {
        Optional<ButtonType> optional = AlertInfo.showConfirmMessage(
                "Informasi Aplikasi",
                "Penghapusan Data Tabel di Database",
                "Apakah Anda yakin akan menghapus seluruh data Robustness yang tersimpan di database?"
        );
        if (optional.get().equals(MainController.buttonTypeYes)) {
            try {
                MainController.appControll.sqLiteDB.deleteTableData(AppControll.TABLE_STEGANO_ROBUSTNESS);
                AlertInfo.showAlertInfoMessage(
                        "Informasi Aplikasi",
                        "Hasil Proses",
                        "Proses penghapusan data robustness pada tabel berhasil dilakukan.",
                        ButtonType.OK
                );
                this.btnBacaDataRobustness.fire();
            } catch (Exception e) {
                AlertInfo.showAlertErrorMessage(
                        "Informasi Aplikasi",
                        "Penghapusan Data Tabel",
                        "Terjadi kesalahan: " + e.getMessage(),
                        ButtonType.OK
                );
            }
        }
    }

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
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Open Scene",
                    "Terjadi kesalahan (Error) : " + e.getMessage(),
                    ButtonType.OK
            );
        }
    }
}
