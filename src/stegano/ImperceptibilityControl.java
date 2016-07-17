package stegano;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import kelas.AlertInfo;
import kelas.PengolahanCitra;
import main.AppControll;
import main.Main;
import main.MainController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by hendro.sinaga on 07-Jul-16.
 */
public class ImperceptibilityControl {
    BufferedImage imageOri, imageStego;
    String oriImageName = "", stegoImageName = "";
    HashMap<String, Integer> dataTable;
    Double mseVal = 0.0, psnrVal = 0.0;
    private boolean hasData;

    @FXML
    Button btnBrowseOriginalImg, btnBrowseStegoImg, btnCalculateMSEPSNR, btnSave, btnMainMenu;
    @FXML
    Label lblInfoOriginalImage, lblInfoStegoImage;
    @FXML
    ImageView imageViewOriginalImg, imageViewStegoImg;
    @FXML
    TextField txtfieldMSE, txtfieldPSNR;


    @FXML
    private void handleBrowseOriginalImg (ActionEvent actionEvent) {
        this.btnBrowseStegoImg.setDisable(true);
        this.btnCalculateMSEPSNR.setDisable(true);
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Original Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));
        Image image = null;
        String sql = "";

        File file = null;
        if (this.hasData) {
            Optional<ButtonType> b = AlertInfo.showConfirmMessage(
                    "Informasi Aplikasi",
                    "Konfirmasi",
                    "Sistem mendeteksi bahwa data sebelumnya belum disimpan.\n"
                    + "Data yang telah diproses tidak tersedia pada hasil testing.\n"
                    + "Anda yakin mengabaikan data yang telah diproses?"
            );
            if (b.equals(ButtonType.OK)) {
                this.hasData = false;
                file = fc.showOpenDialog(null);
            }
        } else {
            file = fc.showOpenDialog(null);
        }

        if (file != null) {
            String infoOriginal = "";
            try {
                image = new Image(file.toURI().toURL().toString());
                this.imageOri = SwingFXUtils.fromFXImage(image, null);
                this.imageViewOriginalImg.setImage(image);
                infoOriginal = "Filename: " + file.getName() + "\n" +
                        "Width: " + (int)image.getWidth() + "\n" +
                        "Height: " + (int)image.getHeight() + "\n" +
                        "Size: " + (int)(file.length() / 1024) + " KB";
                this.lblInfoOriginalImage.setText(infoOriginal);
                this.btnBrowseStegoImg.setDisable(false);

                this.oriImageName = file.getName();
            }
            catch (MalformedURLException malformedURLException) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi",
                        "Lokasi Berkas",
                        "Terjadi kesalahan ketika mengakses image",
                        ButtonType.OK
                );
            }
        }
    }

    @FXML
    private void handleBrowseStegoImg (ActionEvent actionEvent) {
        String sql = "";
        boolean namaOriImageSama = false;
        ResultSet rs = null;
        if (this.dataTable != null) {
            this.dataTable.clear();
        }
        this.dataTable = new HashMap<String, Integer>();
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Stego Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));
        Image image = null;

        File file = fc.showOpenDialog(null);

        if (file != null) {
            String infoStegoImage = "";
            try {
                image = new Image(file.toURI().toURL().toString());
                this.imageStego = SwingFXUtils.fromFXImage(image, null);
                this.imageViewStegoImg.setImage(image);
                infoStegoImage = "Filename: " + file.getName() + "\n" +
                        "Width: " + (int)image.getWidth() + "\n" +
                        "Height: " + (int)image.getHeight() + "\n" +
                        "Size: " + (int)(file.length() / 1024) + " KB";
                this.lblInfoStegoImage.setText(infoStegoImage);
                this.stegoImageName = file.getName();

                sql = "SELECT * FROM " + AppControll.TABLE_STEGANO_NAME + " WHERE "
                        + "stegoImageName = '" + this.stegoImageName + "';";
                rs = MainController.appControll.sqLiteDB.SelectQuery(sql);
                while (rs.next()) {
                    if (this.oriImageName.equalsIgnoreCase(rs.getString("oriImageName"))) {
                        namaOriImageSama = true;
                    }
                    this.dataTable.put(rs.getString("stegoImageName"), rs.getInt("id"));
                }
                if (!namaOriImageSama || this.dataTable.size() < 1) {
                    throw new Exception("Nama file original image dan stego image tidak tersedia di database");
                }
                else {
                    this.btnCalculateMSEPSNR.setDisable(false);
                }
                //this.btnCalculateMSEPSNR.setDisable(false);
            }
            catch (MalformedURLException malformedURLException) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi",
                        "Lokasi Berkas",
                        "Terjadi kesalahan ketika mengakses image",
                        ButtonType.OK
                );
            }
            catch (Exception e) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi",
                        "Database",
                        "Terjadi kesalahan ketika mengakses database: \n" + e.getMessage() ,
                        ButtonType.OK
                );
            }
        }
    }

    @FXML void handleCalculateMSEPSNR(ActionEvent actionEvent) {
        boolean prosesCalculate = false;
        if (this.imageOri != null && this.imageStego != null) {
            if ((this.imageOri.getWidth() == this.imageStego.getWidth()) &&
                    (this.imageOri.getHeight() == this.imageStego.getHeight())) {
                prosesCalculate = true;
            }
        }
        if (prosesCalculate) {
            this.mseVal = PengolahanCitra.calculateMSE(this.imageOri, this.imageStego);
            if (this.mseVal > Double.MIN_VALUE) {
                this.psnrVal = PengolahanCitra.calculatePSNR(this.mseVal);
                this.txtfieldMSE.setText(this.mseVal + "");
                this.txtfieldPSNR.setText(this.psnrVal + "");
                this.hasData = true;
                btnSave.setDisable(false);
            }
        }


    }

    @FXML void handleBtnSaveData(ActionEvent actionEvent) {
        String sql = "UPDATE " + AppControll.TABLE_STEGANO_NAME
                + " SET mseVal = " + this.mseVal
                + ", psnrVal = " + this.psnrVal
                + " WHERE id = " + this.dataTable.get(this.stegoImageName);
        int res = 0;
        try {
            res = MainController.appControll.sqLiteDB.InsertUpdateDeleteQuery(sql);
            this.hasData = false;
        } catch (Exception e) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Update Data",
                    "Gagal memperbaharui data pada database: \n" + e.getMessage(),
                    ButtonType.OK
            );
        }

        if (res > 0) {
            AlertInfo.showAlertInfoMessage(
                    "Informasi Aplikasi",
                    "Update Data",
                    "Data berhasil diperbaharui",
                    ButtonType.OK
            );
        }
    }

    @FXML
    private void handleMainMenuScene(ActionEvent actionEvent) {
        boolean prosesToMainMenu = false;
        if (this.hasData) {
            Optional<ButtonType> b = AlertInfo.showConfirmMessage(
                    "Informasi Aplikasi",
                    "Konfirmasi",
                    "Sistem mendeteksi bahwa data sebelumnya belum disimpan.\n"
                            + "Data yang telah diproses tidak tersedia pada hasil testing.\n"
                            + "Anda yakin mengabaikan data yang telah diproses?"
            );
            if (b.equals(ButtonType.OK)) {
                this.hasData = false;
                prosesToMainMenu = true;
            }
        } else {
            if (prosesToMainMenu) {
                Parent p = null;
                boolean sceneLoaded = true;

                try {
                    p = FXMLLoader.load(getClass().getClassLoader().getResource("main/maindoc.fxml"));
                } catch (IOException ex) {
                    sceneLoaded = false;
                }

                if (!sceneLoaded || p == null) {
                    AlertInfo.showAlertWarningMessage(
                            "Informasi Aplikasi",
                            "Load main scene",
                            "Gagal membuka main scene",
                            ButtonType.OK
                    );
                } else {
                    this.dataTable.clear();
                    Main.mainStage.setTitle("Aplikasi Steganografi");
                    Main.mainStage.setScene(new Scene(p, 756, 485));
                    Main.mainStage.centerOnScreen();
                }
            }
        }

    }

}
