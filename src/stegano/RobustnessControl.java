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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import kelas.*;
import kripto.HAES256;
import main.AppControll;
import main.Main;
import main.MainController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by hendro.sinaga on 10-Jul-16.
 */
public class RobustnessControl implements Initializable {
    private HashMap<String, Integer> dataTable;
    List<ArrayList<Integer>> dataEncrypt = null;
    List<ArrayList<Integer>> dataDecrypt = null;
    BufferedImage bufferedImageStego, bufferedImageStegoNoise;
    BigInteger primeNumber;
    Double noiseProb, percentage;
    String stegoImagePathFile = "", chiperTextInBiner = "", stegoImageName = "";
    boolean kunciSama = false, stegoImageNameSama = false, hasData = false, skipOpenFile = false;
    int[] rgbDataOfImage;
    int nOfChiperLenOnByte = 0, nOfPixelForEmbedding = -1;
    int coverImgWidth = 0, coverImgHeight = 0, coverImgType = -1, xnm = -1, gemodNumber = -1, np;
    final int NK_ON_BYTE = 256;
    char[] arrCharTeksOri = null;
    @FXML
    AnchorPane anchorPaneExtraction;
    @FXML
    Button  btnBrowse, btnAddNoise, btnInitExtractMsg, btnExtractDecrypt,
            btnBrowseTxt, btnCompare, btnSaveRobustness, btnMainMenu;
    @FXML
    ImageView imgViewStego, imgViewStegoNoise;
    @FXML
    Label lblInfoImgStego;

    @FXML
    TextField txtfieldNoiseProbSaltPepper, txtfieldKunci, txtfieldCompareResult;
    @FXML
    TextArea txtareaTest, txtareaOriMsg;


    @FXML
    private void handleBrowseStegoImg (ActionEvent actionEvent) {
        this.dataTable = new HashMap<String, Integer>();
        this.percentage = 0D;
        this.primeNumber = new BigInteger("0", 10);
        this.kunciSama = false;
        this.skipOpenFile = false;
        this.chiperTextInBiner = "";
        this.rgbDataOfImage = null;
        this.dataEncrypt = null;
        this.dataDecrypt = null;
        this.bufferedImageStego = null;
        this.bufferedImageStegoNoise = null;
        this.btnAddNoise.setDisable(true);
        this.btnInitExtractMsg.setDisable(true);
        this.txtfieldKunci.clear();
        this.txtareaTest.clear();
        this.txtareaOriMsg.clear();
        this.txtfieldCompareResult.clear();
        this.anchorPaneExtraction.setDisable(true);
        /*this.txtfieldNoiseProbSaltPepper.clear();*/
        String sql = "";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Stego Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));

        File file = fileChooser.showOpenDialog(Main.mainStage);
        Image image = null;
        boolean dataTersedia = false;

        if (file != null) {
            try {
                this.stegoImagePathFile = file.toURI().toURL().toString();
                image = new Image(this.stegoImagePathFile);
                this.bufferedImageStego = SwingFXUtils.fromFXImage(image, null);
                this.imgViewStego.setImage(image);
                this.lblInfoImgStego.setText(
                        "Filename: " + file.getName() + "\n"
                        + "Width: " + this.bufferedImageStego.getWidth() + "\n"
                        + "Height: " + this.bufferedImageStego.getHeight() + "\n"
                        + "Size: " + (file.length() / 1024) + " KB\n"
                );
                this.stegoImageName = file.getName();
                sql = "SELECT * FROM " + AppControll.TABLE_STEGANO_MASTER + " WHERE "
                        + "stegoImageName = '" + this.stegoImageName + "';";
                ResultSet rs = MainController.appControll.sqLiteDB.SelectQuery(sql);
                while (rs.next()) {
                    if (this.stegoImageName.equalsIgnoreCase(rs.getString("stegoImageName"))) {
                        this.stegoImageNameSama = true;
                        this.dataTable.put(rs.getString("stegoImageName"), rs.getInt("id"));
                        break;
                    }
                }
                if (!this.stegoImageNameSama || this.dataTable.size() < 1) {
                    throw new Exception("Nama file stego image tidak tersedia di database");
                }
                else {
                    this.btnAddNoise.setDisable(false);
                }
                rs.close();
                MainController.appControll.sqLiteDB.closeConnection();
            }
            catch (MalformedURLException malformedURLException) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi",
                        "Kesalahan: Lokasi Berkas",
                        "tidak dapat mengakses image pada direktori",
                        ButtonType.OK
                );
            }
            catch (Exception e) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi",
                        "Database",
                        "Terjadi kesalahan: \n" + e.getMessage() ,
                        ButtonType.OK
                );
            }
        }

    }

    @FXML
    private void handleAddNoise (ActionEvent actionEvent) {
        boolean noiseValid = false;
        try {
            this.noiseProb = Double.parseDouble(this.txtfieldNoiseProbSaltPepper.getText());
            noiseValid = true;
        } catch (Exception e) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi: Testing - Robustness",
                    "Noise Probability Value",
                    "Invalid noise probability.\nNilai Noise 0 - 100",
                    ButtonType.OK
            );
        }
        if (noiseValid) {
            if (!(noiseProb >= 0 && noiseProb <= 100)) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi",
                        "Salt And Pepper Input",
                        "Nilai probabilitas tidak valid. Nilai probabilitas harus diantara 0 - 100 (%)",
                        ButtonType.OK
                );
            }
            else {
                this.bufferedImageStegoNoise = PengolahanCitra.addSaltAndPepperNoise(this.bufferedImageStego, this.noiseProb);
                //this.bufferedImageStegoNoise = this.bufferedImageStego;
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
                    //this.btnAddNoise.setDisable(true);
                    this.btnCompare.setDisable(true);
                    this.btnInitExtractMsg.setDisable(false);
                }
            }
        }
    }

    @FXML void handleInitExtractMsg (ActionEvent actionEvent) {
        boolean initStatus = false;

        this.np = this.bufferedImageStegoNoise.getWidth() * this.bufferedImageStegoNoise.getHeight();
        this.rgbDataOfImage = new int[this.bufferedImageStegoNoise.getWidth() * this.bufferedImageStegoNoise.getHeight()];
        String npOnBinary = Integer.toBinaryString(np);
        this.xnm = npOnBinary.length();
        //this.nOfChiperLenOnByte = textChiper.getText().length() * 8;
        this.nOfPixelForEmbedding = this.xnm + this.nOfChiperLenOnByte + NK_ON_BYTE;
        for (int i = ((np % 2 == 0) ? np - 1 : np); i > 0; i -= 2) {
            this.primeNumber = new BigInteger(i + "", 10);
            if (primeNumber.isProbablePrime(1)) {
                break;
            }
        }

        if (this.primeNumber.intValue() > this.nOfPixelForEmbedding) {
            try {
                int indeks = 0;
                for (int x = 0; x < this.bufferedImageStegoNoise.getWidth(); x += 1) {
                    for (int y = 0; y < this.bufferedImageStegoNoise.getHeight(); y += 1) {
                        this.rgbDataOfImage[indeks] = this.bufferedImageStegoNoise.getRGB(x, y);
                        indeks += 1;
                    }
                }
                initStatus = true;
            } catch (Exception except) {
                AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                        "Membaca Data RGB Gambar",
                        "Terjadi kesalahan ketika membaca data RGB Stego Image",
                        ButtonType.OK
                );
            }
        }
        if (initStatus) {
            boolean skipYes = false;
            if (this.skipOpenFile) {
                Optional<ButtonType> optional = AlertInfo.showConfirmMessage(
                        "Informasi Aplikasi",
                        "Inisialisasi Data Pengujian Robustness",
                        "Sistem mendeteksi bahwa stego image yang akan digunakan sama dengan sebelumnya"
                                + "\nJika pernyataan ini benar, klik Yes untuk skip proses input kunci dan berkas teks asli."
                                + "\nJika tidak, klik No"
                );
                if (optional.get().equals(MainController.buttonTypeYes)) {
                    skipYes = true;
                }
            }
            if (skipYes) {
                this.btnExtractDecrypt.setDisable(false);
                this.txtareaTest.clear();
            }
            else {
                this.skipOpenFile = false;
                this.txtfieldKunci.clear();
                this.txtareaTest.clear();
                this.btnExtractDecrypt.setDisable(true);
                this.txtareaOriMsg.clear();
                this.anchorPaneExtraction.setDisable(false);
                this.txtfieldKunci.setEditable(true);
            }
        }
    }

    @FXML
    private void handleInputPassword(KeyEvent handler) {
        if (txtfieldKunci.getText().length() > 4) {
            if (this.btnExtractDecrypt.isDisable()) {
                btnExtractDecrypt.setDisable(false);
            }
            if (txtfieldKunci.getText().length() > 32) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi: Embedding",
                        "Panjang Kunci",
                        "Panjang Kunci maksimal 32 karakter",
                        ButtonType.OK
                );
                txtfieldKunci.setText(txtfieldKunci.getText().substring(0, 32));
                txtfieldKunci.positionCaret(32);
            }
        } else {
            if (!(txtfieldKunci.getText().length() < 1)) {
                btnExtractDecrypt.setDisable(true);
            }
        }
    }

    @FXML void handleExtractDecrypt (ActionEvent actionEvent) {
        this.gemodNumber = Matematik.gemodFinder(this.primeNumber.intValue());
        /*this.txtareaTest.setText(
                "NP: " + this.np + "\n"
                + "xnm: " + this.xnm + "\n"
                + "PrimeNumber: " + this.primeNumber + "\n"
                + "Gemod: " + this.gemodNumber
        );*/
        try {
            int pixelIterator = 1;
            BigInteger gemodNums = new BigInteger(this.gemodNumber + "", 10);
            //byte siklusRGB = 1;
            String binerInfoPanjangPesan = "";
            for (int i = 0; i < this.xnm; i += 1) {
                BigInteger random = gemodNums.modPow(new BigInteger(pixelIterator + "", 10),
                        this.primeNumber);
                int a = (this.rgbDataOfImage[random.intValue()]>>24)&0x000000FF;
                int r = (this.rgbDataOfImage[random.intValue()]>>16)&0x000000FF;
                int g = (this.rgbDataOfImage[random.intValue()]>>8)&0x000000FF;
                int b = (this.rgbDataOfImage[random.intValue()])&0x000000FF;

                //jika parity genap
                if (ParityChecker.generateParity(r,g,b) == 0) {
                    binerInfoPanjangPesan += "0";
                } else {
                    binerInfoPanjangPesan += "1";
                }
                pixelIterator += 1;
            }

            int panjangPesan = Integer.parseInt(binerInfoPanjangPesan, 2);

            String binerKunci = "";
            for (int i = 0; i < this.NK_ON_BYTE; i += 1) {
                BigInteger randoms = gemodNums.modPow(new BigInteger(pixelIterator + "", 10),
                        this.primeNumber);
                int r = (this.rgbDataOfImage[randoms.intValue()]>>16)&0x000000FF;
                int g = (this.rgbDataOfImage[randoms.intValue()]>>8)&0x000000FF;
                int b = (this.rgbDataOfImage[randoms.intValue()])&0x000000FF;

                //jika parity genap
                if (ParityChecker.generateParity(r,g,b) == 0) {
                    binerKunci += "0";
                } else {
                    binerKunci += "1";
                }
                pixelIterator += 1;
            }

            //binerKunci.substring(beginIndex, endIndex)
            int bil = Integer.parseInt(binerKunci.substring(0, 8), 2);
            String huruf1 = Character.toString((char)bil);

            String strKunci = "";
            boolean potonglagi = true;
            int indeks = 0;

            while (potonglagi) {
                int tmpBil;
                if (indeks + 8 >= binerKunci.length()) {
                    tmpBil = Integer.parseInt(binerKunci.substring(indeks, binerKunci.length()), 2);
                    potonglagi = false;
                }
                else {
                    tmpBil = Integer.parseInt(binerKunci.substring(indeks, indeks + 8), 2);
                }
                if (tmpBil != 0) {
                    strKunci += Character.toString((char)tmpBil);
                    //this.textAreaOri.appendText(tmpBil + " ");
                }
                indeks += 8;
            }

            this.kunciSama = false;
            char[] arrKunciTersimpan = strKunci.toCharArray();
            char[] arrKunciInput = this.txtfieldKunci.getText().toCharArray();

            if (arrKunciTersimpan.length == arrKunciInput.length) {
                boolean tmpPersamaan = true;
                //this.textAreaOri.appendText("\nPanjangKunci input dan tersimpan sama\n");
                for (int i = 0; i < arrKunciInput.length; i += 1) {
                    if (arrKunciInput[i] != arrKunciTersimpan[i]) {
                        tmpPersamaan = false;
                        break;
                    }
                }
                this.kunciSama = tmpPersamaan;
            }
            if (this.kunciSama) {
                this.chiperTextInBiner = "";
                for (int i = 0; i < panjangPesan; i += 1) {
                    BigInteger randoms = gemodNums.modPow(new BigInteger(pixelIterator + "", 10),
                            this.primeNumber);
                    int r = (this.rgbDataOfImage[randoms.intValue()]>>16)&0x000000FF;
                    int g = (this.rgbDataOfImage[randoms.intValue()]>>8)&0x000000FF;
                    int b = (this.rgbDataOfImage[randoms.intValue()])&0x000000FF;

                    //jika parity genap
                    if (ParityChecker.generateParity(r,g,b) == 0) {
                        this.chiperTextInBiner += "0";
                    } else {
                        this.chiperTextInBiner += "1";
                    }
                    pixelIterator += 1;
                }

            }
            else {
                throw new Exception("Maaf!!!\\nKunci yang Anda berikan tidak sesuai.");
            }

            if (this.chiperTextInBiner.length() >= 16) {
                if (doExtractMessage()) {
                    if (doDecryptMessage()) {
                        if (!this.skipOpenFile) {
                            this.btnBrowseTxt.setDisable(false);
                        } else {
                            this.btnCompare.setDisable(false);
                        }
                        this.txtfieldCompareResult.clear();
                    }
                }
            }
        }
        catch (Exception e) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi: Robustness",
                    "Extraction - Decrypt Process",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }
    }

    @FXML void handleBrowseTxtOri (ActionEvent actionEvent) {
        boolean loadFileStatus = false;
        byte[] byteTeks = null;
        File file = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Original Text");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text File", "*.txt"));

        file = fileChooser.showOpenDialog(Main.mainStage);

        if (file != null) {
           try {
               byteTeks = Files.readAllBytes(Paths.get(file.getPath()));
               loadFileStatus = true;
           } catch (IOException ioException) {
               AlertInfo.showAlertErrorMessage(
                       "Informasi Aplikasi (Robustness)",
                       "Load File",
                       "Terjadi kesalahan ketika load file: " + ioException.getMessage(),
                       ButtonType.OK
               );
           }
            if (loadFileStatus) {
                this.txtareaOriMsg.setText(new String(byteTeks));
                this.btnBrowseTxt.setDisable(true);
                this.txtfieldCompareResult.clear();
                this.btnCompare.setDisable(false);
            }
        }
    }

    @FXML void handleBtnCompare (ActionEvent actionEvent) {
        char[] charArrExtraction = this.txtareaTest.getText().toCharArray();
        char[] charArrOri = this.txtareaOriMsg.getText().toCharArray();
        int counter = 0;

        try {
            for (int i = 0; i < charArrOri.length; i += 1) {
                if (charArrOri[i] == charArrExtraction[i]) {
                    counter += 1;
                }
                /*if (Character.valueOf(charArrOri[i]).compareTo(Character.valueOf(charArrExtraction[i])) == 0) {
                    counter += 1;
                }*/
                /*if ((int)charArrOri[i] == (int)charArrExtraction[i])
                    counter += 1;*/
            }
        }
        catch (Exception ex) {}
        finally {
            this.percentage = Matematik.roundUpDoubleWithTwoDecimalPlace(((double)counter / charArrOri.length) * 100);
            this.btnSaveRobustness.setDisable(false);
            this.hasData = true;
            this.txtfieldCompareResult.setText(this.percentage.toString());
            this.txtfieldNoiseProbSaltPepper.setEditable(true);
            this.btnAddNoise.setDisable(false);
        }

    }

    @FXML void handleSaveDataRobustness (ActionEvent actionEvent) {
        String sqlInsert = "INSERT INTO " + AppControll.TABLE_STEGANO_ROBUSTNESS
                + " VALUES("
                + null + ", "
                + this.dataTable.get(this.stegoImageName) + ", "
                + this.noiseProb + ", "
                + this.percentage
                + ");";
        ResultSet rs = null;
        boolean dataSudahAda = false;

        int res = 0;
        try {
            res = MainController.appControll.sqLiteDB.InsertUpdateDeleteQuery(sqlInsert);
            MainController.appControll.sqLiteDB.closeConnection();
        } catch (Exception e) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Database - Robustness Table",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }
        finally {
            if (res > 0) {
                AlertInfo.showAlertInfoMessage(
                        "Informasi Aplikasi",
                        "Database - Robustness Table",
                        "Data berhasil disimpan!!!",
                        ButtonType.OK
                );
                this.btnSaveRobustness.setDisable(true);
                this.skipOpenFile = true;
            }
        }

    }

    private boolean doExtractMessage() {
        boolean potonglagi = true, prosesKonversiBinerPesanEkstraksi = false;
        int indeks = 0, prosesPotong = 0;
        this.dataEncrypt = new ArrayList<>();
        try {
            while (potonglagi) {
                ArrayList<Integer> tmp = new ArrayList<>();
                for (int i = 0; i < 16; i += 1) {
                    //String str = "";
                    if (indeks + 8 >= this.chiperTextInBiner.length()) {
                        tmp.add(Integer.parseInt(
                                this.chiperTextInBiner.substring(indeks, this.chiperTextInBiner.length()),
                                2)
                        );
                        potonglagi = false;
                        i = 16;
                    } else {
                        tmp.add(Integer.parseInt(
                                this.chiperTextInBiner.substring(indeks, indeks + 8),
                                2)
                        );
                    }
                    indeks += 8;
                }
                this.dataEncrypt.add(tmp);

            }
            prosesKonversiBinerPesanEkstraksi = true;
        } catch (Exception exception) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi (Error): Robustness",
                    "Ekstraksi pesan - Konversi biner pesan",
                    "Terjadi kesalahan ketika akan mengkonversi biner pesan ekstraksi.\n"
                            + "Detail error: " + exception.getMessage(),
                    ButtonType.OK
            );
        }
        if (prosesKonversiBinerPesanEkstraksi) {
            String strDataEnkrip = "";
            for (ArrayList<Integer> val : this.dataEncrypt) {
                for (int i = 0; i < val.size(); i += 1) {
                    strDataEnkrip += Character.toString((char)val.get(i).intValue());
                }
            }
            this.txtareaTest.setText(strDataEnkrip);
            this.txtfieldKunci.setEditable(false);
            this.btnExtractDecrypt.setDisable(true);
        }
        return prosesKonversiBinerPesanEkstraksi;
    }

    boolean doDecryptMessage () {
        boolean dekripStatus = false;
        char[] arrCharKunci = txtfieldKunci.getText().toCharArray();
        int[] kunciDecrypt = new int[arrCharKunci.length];
        for (short i = 0; i < arrCharKunci.length; i += 1) {
            kunciDecrypt[i] = (int)arrCharKunci[i];
        }
        if (this.dataDecrypt != null) {
            this.dataDecrypt.clear();
        }
        this.dataDecrypt = new ArrayList<>();
        HAES256 haes256 = HAES256.getInstance();
        haes256.ReInitProps();
        for (int i = 0; i < this.dataEncrypt.size(); i += 1) {
            ArrayList<Integer> subData = new ArrayList<>();
            try {
                if (haes256.decryptDataInteger(
                        KonversiData.arraylist1DToArr1D(this.dataEncrypt.get(i)),
                        kunciDecrypt
                )) {
                    for (Integer val : KonversiData.arr2DToIntArr1D(haes256.getArrChiper())) {
                        subData.add(val);
                    }
                    this.dataDecrypt.add(subData);
                } else {
                    throw new Exception("Kesalahan proses dekripsi");
                }
                dekripStatus = true;
            } catch (Exception decriptException) {
                AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                        "Error in Proses Dekripsi AES-256",
                        "Terjadi kesalahan proses: \n" + decriptException.getMessage(),
                        ButtonType.OK);
            }
            if (!dekripStatus)
                break;
        }
        if (dekripStatus) {
            String hasilDekrip = "";
            for (ArrayList<Integer> val : this.dataDecrypt) {
                for (int i = 0; i < val.size(); i += 1) {
                    hasilDekrip += Character.toString((char)val.get(i).intValue());
                }
            }
            this.arrCharTeksOri = hasilDekrip.toCharArray();
            this.txtareaTest.clear();
            this.txtareaTest.setText(hasilDekrip);
        }
        return dekripStatus;
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
        try {
            btnBrowse.setGraphic(
                    new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "bmp-icon16.png"))
            );
            btnBrowseTxt.setGraphic(
                    new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "fltxt-icon16.png"))
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
        txtfieldNoiseProbSaltPepper.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtfieldNoiseProbSaltPepper.setText(newValue.replaceAll("[^\\d.]", ""));
                }
            }
        });
    }
}
