package stegano;

import interfaces.OpenScene;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.*;
import kelas.AlertInfo;
import kelas.KonversiData;
import kelas.Matematik;
import kelas.PengolahanCitra;
import main.AppControll;
import main.Main;
import main.MainController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by hendro.sinaga on 07-Jul-16.
 */

public class ImperceptibilityControl implements Initializable {
    BufferedImage imageOri, imageStego;
    String oriImageName = "", stegoImageName = "";
    HashMap<String, Integer> dataTable;
    Double mseVal = 0.0, psnrVal = 0.0;
    private boolean hasData;

    @FXML
    Button btnBrowseOriginalImg, btnBrowseStegoImg, btnCalculateMSEPSNR, btnSave, btnShowHistogram, btnMainMenu;
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
        this.btnShowHistogram.setDisable(true);
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Original Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));
        Image image = null;
        String sql = "";
        boolean continueProcess = true;
        File file = null;

        if (this.hasData) {
            Optional<ButtonType> b = AlertInfo.showConfirmMessage(
                    "Informasi Aplikasi",
                    "Konfirmasi",
                    "Sistem mendeteksi bahwa data sebelumnya belum disimpan.\n"
                            + "Data yang telah diproses tidak tersedia pada hasil testing.\n"
                            + "Anda yakin mengabaikan data yang telah diproses?"
            );
            if (b.get().equals(MainController.buttonTypeYes)) {
                this.hasData = false;
                file = fc.showOpenDialog(Main.mainStage);
            }
        } else {
            file = fc.showOpenDialog(Main.mainStage);
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
        this.btnShowHistogram.setDisable(true);
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

                sql = "SELECT * FROM " + AppControll.TABLE_STEGANO_MASTER + " WHERE "
                        + "stegoImageName = '" + this.stegoImageName + "';";
                rs = MainController.appControll.sqLiteDB.SelectQuery(sql);

                while (rs.next()) {
                    if (this.oriImageName.equalsIgnoreCase(rs.getString("oriImageName"))) {
                        namaOriImageSama = true;
                    }
                    this.dataTable.put(rs.getString("stegoImageName"), rs.getInt("id"));
                }
                if (!namaOriImageSama || this.dataTable.size() < 1) {
                    throw new Exception("Nama file original image dan stego image tidak valid\n"
                            + "dengan data yang tersedia di database");
                }
                else {
                    this.btnCalculateMSEPSNR.setDisable(false);
                }
                //this.btnCalculateMSEPSNR.setDisable(false);
                this.btnShowHistogram.setDisable(false);
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
                        "Pencocokan Data",
                        "Terjadi kesalahan : \n" + e.getMessage() ,
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
            //this.mseVal = Matematik.roundUpDoubleWithTwoDecimalPlace(this.mseVal);
            if (this.mseVal > Double.MIN_VALUE) {
                this.psnrVal = PengolahanCitra.calculatePSNR(this.mseVal);
                this.mseVal = Matematik.roundUpDoubleWithTwoDecimalPlace(this.mseVal);
                //this.mseVal = Double.parseDouble(this.mseVal + "");
                this.psnrVal = Matematik.roundUpDoubleWithTwoDecimalPlace(this.psnrVal);
                this.txtfieldMSE.setText(this.mseVal + "");
                this.txtfieldPSNR.setText(this.psnrVal + "");
                this.hasData = true;
                btnSave.setDisable(false);
            }
        }


    }

    @FXML void handleBtnSaveData(ActionEvent actionEvent) {
        int res = 0;
        boolean dataSudahAda = false, prosesCekBerhasil = false;
        try {
            ResultSet rs = MainController.appControll.sqLiteDB.SelectQuery(
                    "SELECT * FROM " + AppControll.TABLE_STEGANO_IMPERCEPTIBILITY + " WHERE "
                            + "sid = " + this.dataTable.get(this.stegoImageName) + ";"
            );
            if (rs.next()) {
                dataSudahAda = true;
            }
            prosesCekBerhasil = true;
        } catch (Exception e) {
            prosesCekBerhasil = false;
        }

        if (prosesCekBerhasil) {
            if (dataSudahAda) {
                AlertInfo.showAlertInfoMessage(
                        "Informasi Aplikasi",
                        "Pengecekan Data",
                        "Data imperceptibility sudah tersedia pada database\n"
                        + "Silahkan lakukan proses pengujian imperceptibility yang berbeda.",
                        ButtonType.OK
                );
            } else {
                try {
                    int n = 0;
                    n = MainController.appControll.sqLiteDB.getJumlahBaris(AppControll.TABLE_STEGANO_IMPERCEPTIBILITY);
                    n += 1;
                    String sql = "INSERT INTO " + AppControll.TABLE_STEGANO_IMPERCEPTIBILITY + " VALUES("
                            + n + ", "
                            + this.dataTable.get(this.stegoImageName) + ", "
                            + this.mseVal + ", "
                            + this.psnrVal
                            + ");";
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
                            "Data mse dan psnr berhasil disimpan",
                            ButtonType.OK
                    );
                }
            }
        } else {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Periksa Data",
                    "Gagal Memeriksa data pada database",
                    ButtonType.OK
            );
        }

    }

    @FXML void handleShowHistogram (ActionEvent actionEvent) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Main.mainStage);

        int[] grayscaleOriImage = null, grayscaleOriImageDistinct = null, niOriImage = null;
        int[] grayscaleStegoImage = null, grayscaleStegoImageDistinct = null, niStegoImage = null;
        double[] histogramOriImage = null, histogramStegoImage = null;

        //final NumberAxis xAxis = new NumberAxis(1, 31, 1);
        final NumberAxis xAxis = new NumberAxis(0, 255, 1);
        final NumberAxis xAxisStego = new NumberAxis(0, 255, 1);
        final NumberAxis yAxis = new NumberAxis();
        final NumberAxis yAxisStego = new NumberAxis();
        final AreaChart<Number,Number> acOri =
                new AreaChart<>(xAxis,yAxis);
        final AreaChart<Number,Number> acStego =
                new AreaChart<>(xAxisStego,yAxisStego);
        acOri.setTitle("Histogram Original Image");
        //acOri.setPrefWidth(446.6);
        acOri.setLayoutX(10.2);
        acOri.setLayoutY(122.2);

        acStego.setTitle("Histogram Stego Image");
        //acStego.setPrefWidth(446.8);
        acStego.setLayoutX(552.8);
        acStego.setLayoutY(122.2);

        ScrollPane scrollPane = new ScrollPane();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(850, 650);
        scrollPane.setPrefSize(anchorPane.getPrefWidth() + 100, anchorPane.getPrefHeight() + 10);

        Button button = new Button("Testing");
        button.setLayoutX(10.2);
        button.setLayoutY(4.2);

        try {
            grayscaleOriImage = PengolahanCitra.getGrayscaleDataRGB(this.imageOri);
            grayscaleOriImageDistinct = KonversiData.getDistinctArray(grayscaleOriImage);
            Arrays.sort(grayscaleOriImageDistinct);

            grayscaleStegoImage = PengolahanCitra.getGrayscaleDataRGB(this.imageStego);
            grayscaleStegoImageDistinct = KonversiData.getDistinctArray(grayscaleStegoImage);
            Arrays.sort(grayscaleStegoImageDistinct);

            niOriImage = new int[grayscaleOriImageDistinct.length];
            niStegoImage = new int[grayscaleStegoImageDistinct.length];

            for (int i = 0; i < grayscaleOriImageDistinct.length; i += 1) {
                niOriImage[i] = 0;
            }
            for (int i = 0; i < grayscaleStegoImageDistinct.length; i += 1) {
                niStegoImage[i] = 0;
            }

            for (int i = 0; i < grayscaleOriImageDistinct.length; i += 1) {
                for (int j = 0; j < grayscaleOriImage.length; j += 1) {
                    if (grayscaleOriImageDistinct[i] == grayscaleOriImage[j]) {
                        niOriImage[i] = niOriImage[i] + 1;
                    }
                }
            }

            for (int i = 0; i < grayscaleStegoImageDistinct.length; i += 1) {
                for (int j = 0; j < grayscaleStegoImage.length; j += 1) {
                    if (grayscaleStegoImageDistinct[i] == grayscaleStegoImage[j]) {
                        niStegoImage[i] = niStegoImage[i] + 1;
                    }
                }
            }

            histogramOriImage = new double[niOriImage.length];
            for (int i = 0; i < niOriImage.length; i += 1) {
                histogramOriImage[i] = (double)niOriImage[i] / (this.imageOri.getWidth() * this.imageOri.getHeight());
            }

            histogramStegoImage = new double[niStegoImage.length];
            for (int i = 0; i < niStegoImage.length; i += 1) {
                histogramStegoImage[i] = (double)niStegoImage[i] / (this.imageStego.getWidth() * this.imageStego.getHeight());
            }

            XYChart.Series seriesOri = new XYChart.Series();
            seriesOri.setName("Original Image");
            for (int i = 0; i < grayscaleOriImageDistinct.length; i += 1) {
                seriesOri.getData().add(new XYChart.Data(grayscaleOriImageDistinct[i], histogramOriImage[i]));
            }

            XYChart.Series seriesStego = new XYChart.Series();
            seriesStego.setName("Stego Image");

            for (int i = 0; i < grayscaleStegoImageDistinct.length; i += 1) {
                seriesStego.getData().add(new XYChart.Data(grayscaleStegoImageDistinct[i], histogramStegoImage[i]));
            }

            acOri.getStylesheets().add(getClass().getClassLoader().getResource("css/Chart.css").toString());

            acOri.getData().addAll(seriesOri);
            acStego.getData().addAll(seriesStego);
            anchorPane.getChildren().addAll(acOri, acStego);
            scrollPane.setContent(anchorPane);
            Scene dialogScene = new Scene(scrollPane, 850, 650);
            dialog.setTitle("Histrogram : Grayscale");
            dialog.setScene(dialogScene);
            dialog.showAndWait();
        } catch (Exception e) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Init Histogram Scene",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }
        System.gc();
    }

    @FXML
    private void handleMainMenuScene(ActionEvent actionEvent) {
        boolean prosesToMainMenu = true;
        if (this.hasData) {
            Optional<ButtonType> b = AlertInfo.showConfirmMessage(
                    "Informasi Aplikasi",
                    "Konfirmasi",
                    "Sistem mendeteksi bahwa data sebelumnya belum disimpan.\n"
                            + "Data yang telah diproses tidak tersedia pada hasil testing.\n"
                            + "Anda yakin mengabaikan data yang telah diproses?"
            );
            if (b.get().equals(MainController.buttonTypeYes)) {
                this.hasData = false;
                prosesToMainMenu = true;
            } else {
                prosesToMainMenu = false;
            }
        }

        if (prosesToMainMenu) {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            btnBrowseOriginalImg.setGraphic(
                    new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "bmp-icon16.png"))
            );
            btnBrowseStegoImg.setGraphic(
                    new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "bmp-icon16.png"))
            );
            btnMainMenu.setGraphic(
                    new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "arrowleft16.png"))
            );
        } catch (MalformedURLException e) {
            AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                    "Kesalahan Akeses File",
                    "File tidak terdeteksi",
                    ButtonType.OK);
        }
    }
}
