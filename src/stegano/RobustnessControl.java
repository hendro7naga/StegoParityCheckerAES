package stegano;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import kelas.AlertInfo;
import kelas.PengolahanCitra;
import main.Main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by hendro.sinaga on 10-Jul-16.
 */
public class RobustnessControl implements Initializable {
    byte pilihanNoise = 0;
    String stegoImagePathFile = "";
    BufferedImage bufferedImageStego, bufferedImageStegoNoise;
    @FXML
    Button btnBrowse,btnAddNoise, btnExtractMsg, btnMainMenu;
    @FXML
    ImageView imgViewStego, imgViewStegoNoise;
    @FXML
    Label lblInfoImgStego;
    @FXML
    ToggleGroup noiseGroup;
    @FXML
    RadioButton radioButtonSaltPepper, radioButtonDiagonal;
    @FXML
    Rectangle rectangleSaltPepper, rectangleDiagonalNoise;
    @FXML
    TextField txtfieldNoiseProbSaltPepper, txtfieldNumberOfNoiseDiagonal, txtfieldLengthOfNoiseDiagonal;
    @FXML
    TextArea txtareaTest;

    @FXML
    private void handleBrowseStegoImg (ActionEvent actionEvent) {
        this.bufferedImageStego = null;
        this.bufferedImageStegoNoise = null;
        this.btnAddNoise.setDisable(true);
        this.btnExtractMsg.setDisable(true);
        this.radioButtonSaltPepper.setDisable(true);
        this.radioButtonDiagonal.setDisable(true);
        /*this.txtfieldNoiseProbSaltPepper.clear();
        this.txtfieldNumberOfNoiseDiagonal.clear();
        this.txtfieldLengthOfNoiseDiagonal.clear();*/

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Stego Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));

        File file = fileChooser.showOpenDialog(Main.mainStage);
        Image image = null;

        if (file != null) {
            try {
                this.stegoImagePathFile = file.toURI().toURL().toString();
                image = new Image(this.stegoImagePathFile);
                this.bufferedImageStego = SwingFXUtils.fromFXImage(image, null);
                this.imgViewStego.setImage(image);
                this.radioButtonSaltPepper.setDisable(false);
                this.radioButtonDiagonal.setDisable(false);
                this.lblInfoImgStego.setText(
                        "Filename: " + file.getName() + "\n"
                        + "Width: " + this.bufferedImageStego.getWidth() + "\n"
                        + "Height: " + this.bufferedImageStego.getHeight() + "\n"
                        + "Size: " + (file.length() / 1024) + " KB\n"
                );
            }
            catch (MalformedURLException malformedURLException) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi",
                        "Kesalahan: Lokasi Berkas",
                        "tidak dapat mengakses image pada direktori",
                        ButtonType.OK
                );
            }
        }

    }

    @FXML
    private void handleRadioButtonToggle(ActionEvent actionEvent) {
        RadioButton selected = (RadioButton)actionEvent.getTarget();
        if (selected.getId().equals("radioButtonSaltPepper")) {
            this.pilihanNoise = 0;
            if (this.btnAddNoise.isDisable() || this.btnAddNoise.isDisabled()) {
                this.btnAddNoise.setDisable(false);
            }
        } else {
            this.pilihanNoise = 1;
            if (this.btnAddNoise.isDisable() || this.btnAddNoise.isDisabled()) {
                this.btnAddNoise.setDisable(false);
            }
        }
    }

    @FXML
    private void handleAddNoise (ActionEvent actionEvent) {
        if (this.pilihanNoise == 0) {
            int prob = Integer.parseInt(this.txtfieldNoiseProbSaltPepper.getText(), 10);
            if (!(prob >= 0 && prob <= 100)) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi",
                        "Salt And Pepper Input",
                        "Nilai probabilitas tidak valid. Nilai probabilitas harus diantara 0 - 100 (%)",
                        ButtonType.OK
                );
            } else {
                this.txtareaTest.appendText("\nProb: " + prob);
                this.bufferedImageStegoNoise = PengolahanCitra.addSaltAndPepperNoise(this.bufferedImageStego, prob);
                if (bufferedImageStegoNoise == null) {
                    AlertInfo.showAlertErrorMessage(
                            "Informasi Aplikasi",
                            "Error: Salt And Pepper Noise",
                            "Terjadi kesalahan pada proses penambahan noise\n"
                            + "Salt and Pepper.",
                            ButtonType.OK
                    );
                } else {
                    this.imgViewStegoNoise.setImage(SwingFXUtils.toFXImage(this.bufferedImageStegoNoise, null));
                    this.btnAddNoise.setDisable(true);
                }
            }
        }
    }

    @FXML
    private void handleBtnMainMenu(ActionEvent actionEvent) {
        Parent parent = null;
        boolean loadMainScene = true;

        try {
            parent = FXMLLoader.load(getClass().getClassLoader().getResource("main/maindoc.fxml"));
            if (parent == null)
                loadMainScene = false;
        } catch (IOException ex) {
            loadMainScene = false;
        }

        if (!loadMainScene) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Gagal membuka scene MainDoc fxml",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi");
            alert.setHeaderText("File tidak ditemukan");
            alert.show();
        }
        else {
            Main.mainStage.setTitle("Aplikasi Steganografi");
            Main.mainStage.setScene(new Scene(parent, 790, 520));
            Main.mainStage.centerOnScreen();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtfieldNoiseProbSaltPepper.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtfieldNoiseProbSaltPepper.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
}
