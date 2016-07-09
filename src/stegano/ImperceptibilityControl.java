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
import main.Main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by hendro.sinaga on 07-Jul-16.
 */
public class ImperceptibilityControl {
    BufferedImage imageOri, imageStego;
    @FXML
    Button btnBrowseOriginalImg, btnBrowseStegoImg, btnCalculateMSEPSNR, btnReset, btnMainMenu;
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

        File file = fc.showOpenDialog(null);

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
                this.btnCalculateMSEPSNR.setDisable(false);
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

    @FXML void handleCalculateMSEPSNR(ActionEvent actionEvent) {
        boolean prosesCalculate = false;
        if (this.imageOri != null && this.imageStego != null) {
            if ((this.imageOri.getWidth() == this.imageStego.getWidth()) &&
                    (this.imageOri.getHeight() == this.imageStego.getHeight())) {
                prosesCalculate = true;
            }
        }
        if (prosesCalculate) {
            Double mse = PengolahanCitra.calculateMSE(this.imageOri, this.imageStego);
            if (mse > Double.MIN_VALUE) {
                Double psnr = PengolahanCitra.calculatePSNR(mse);
                this.txtfieldMSE.setText(mse + "");
                this.txtfieldPSNR.setText(psnr + "");
            }
        }


    }

    @FXML
    private void handleMainMenuScene(ActionEvent actionEvent) {
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
            Main.mainStage.setTitle("Aplikasi Steganografi");
            Main.mainStage.setScene(new Scene(p, 756, 485));
            Main.mainStage.centerOnScreen();
        }
    }

}
