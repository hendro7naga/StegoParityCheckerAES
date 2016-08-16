package stegano;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import kelas.*;
import kripto.HAES256;
import main.AppControll;
import main.Main;
import main.MainController;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by hendro.sinaga on 08-Jun-16.
 */
public class EmbeddingController implements Initializable {
    BufferedImage stegoImage = null;
    Image coverImage;
    String imgPath = "", imgOriName = "", imgStegoName = "", imgProperty = "", bitDepthImg = "";
    BigInteger primeNumber = new BigInteger("0", 10);
    char[] pesanArrChar;
    List<ArrayList<Integer>> dataPesan = null;
    List<ArrayList<Integer>> dataEncrypt = null;
    int nOfChiperLenOnByte = 0, nOfPixelForEmbedding = -1, textLength = 0;
    int coverImgWidth = 0, coverImgHeight = 0, coverImgType = -1, xnm = -1, gemodNumber = -1;
    int[] rgbDataOfImage = null;
    final int NK = 32;
    final int NK_ON_BYTE = 256;
    boolean isBmp24Bit = false, hasData = false;
    boolean enkripProsesSukses = false, processTimer;
    Thread threadTimer;
    Task taskTimer;

    Alert alert;
    @FXML ProgressBar progressBar;
    @FXML
    TextArea textInputMessage, textChiper;
    @FXML
    Text txtInfoMessage, txtInfoChipertext, textInfoCoverImg;
    @FXML
    TextField txtInputPass, txtfieldDetik, txtfieldMs;
    @FXML
    Button btnBrowseText, btnEncrypt, btnBrowseCoverImg, btnEmbedMessage, btnMainMenu, btnSaveStegoImg, btnNext;
    @FXML ImageView imgViewCover, imgViewStego;

    @FXML
    private void handleBrowseFileText(ActionEvent event) {
        this.btnBrowseCoverImg.setDisable(true);
        this.btnEmbedMessage.setDisable(true);
        this.btnEncrypt.setDisable(true);
        hasData = false;
        boolean sukses = true;
        byte[] byteTeks = null;
        String textFile = "";
        FileChooser fc = new FileChooser();
        fc.setTitle("Buka Berkas Teks");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Berkas Teks", "*.txt"));

        File fileTeks = fc.showOpenDialog(Main.mainStage);

        if (fileTeks != null) {
            try {
                byteTeks = Files.readAllBytes(Paths.get(fileTeks.getPath()));
                textInputMessage.setText(new String(byteTeks));
                this.textLength = textInputMessage.getLength();
                this.textInputMessage.setEditable(false);
                this.txtInputPass.setEditable(true);
            } catch (IOException ex) {
                alert = new Alert(Alert.AlertType.ERROR,
                                "Berkas tidak dapat diakses dan dibaca.\nDetail Error: " + ex.getMessage(),
                                ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("Berkas tidak ditemukan");
                alert.show();
            } catch (Exception e) {
               alert = new Alert(Alert.AlertType.ERROR,
                       "Terjadi kesalahan pembacaan file",
                       ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("Terjadi Exception");
                alert.show();
            }
        }

    }

    private void handleTextInputMessage() {
        textInputMessage.commitValue();
        int len = textInputMessage.getText().length();
        txtInfoMessage.setText("Text Length : " + len + " Byte");
        this.textLength = len;
        if (len > 3) {
            this.txtInputPass.setEditable(true);
        } else {
            this.txtInputPass.setEditable(false);
        }
    }

    @FXML
    private void handleInputPassword(KeyEvent handler) {
        if (txtInputPass.getText().length() > 4) {
            if (btnEncrypt.isDisable()) {
                btnEncrypt.setDisable(false);
            }
            if (txtInputPass.getText().length() > 32) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi: Embedding",
                        "Panjang Kunci",
                        "Panjang Kunci maksimal 32 karakter",
                        ButtonType.OK
                );
                txtInputPass.setText(txtInputPass.getText().substring(0, 32));
                txtInputPass.positionCaret(32);
            }
        } else {
            if (!(textInputMessage.getText().length() < 1)) {
                btnEncrypt.setDisable(true);
            }
        }
    }

    private void updateProgressBar(double newVal) throws Exception {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (!progressBar.isVisible()) {
                    progressBar.setProgress(0.00);
                    progressBar.setVisible(true);
                }
                progressBar.setProgress(newVal);
                if (progressBar.getProgress() >= 99.29) {
                    progressBar.setVisible(false);
                }
                progressBar.notify();
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void handleBtnEncrypt(ActionEvent event) {
        if (textInputMessage.getText().length() < 3 || txtInputPass.getText().length() < 4) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Informasi Enkripsi",
                    "Pastikan Anda sudah mengisi data teks pesan dan kunci,\n" +
                            "sebelum melakukan enkripsi.",
                    ButtonType.OK
            );
            btnBrowseCoverImg.setDisable(true);
        } else {
            int n = textInputMessage.getText().length();
            this.textLength = n;
            this.nOfChiperLenOnByte = n + ((16 - (n % 16)) % 16);
            this.nOfChiperLenOnByte *= 8;
            if (doEncrypt()) {
                this.txtInfoChipertext.setText("Chipertext Length: " + (this.dataEncrypt.size() * 16) + " byte");
                btnEncrypt.setDisable(true);
                btnBrowseCoverImg.setDisable(false);
            }
        }
    }

    @FXML
    private void handlebtnBrowseCoverImg(ActionEvent event) {
        //Image coverImg;
        primeNumber = new BigInteger("0", 10);
        this.btnSaveStegoImg.setDisable(true);
        this.btnEmbedMessage.setDisable(true);
        this.isBmp24Bit = false;
        this.rgbDataOfImage = null;
        BufferedImage buffCoverImg = null;
        boolean initImage = false;
        FileChooser fc = new FileChooser();
        fc.setTitle("Buka Berkas Gambar");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));

        File fg = fc.showOpenDialog(Main.mainStage);
        if (fg != null) {
            try {
                imgPath = fg.toURI().toURL().toString();
                coverImage = new Image(imgPath);
                buffCoverImg = SwingFXUtils.fromFXImage(coverImage, null);
                imgViewCover.setImage(coverImage);
                imgViewCover.setFitWidth(292);
                imgViewCover.setFitHeight(220);
                textInfoCoverImg.setText(
                        "Nama File: " + fg.getName() + "\n"
                        + "Lebar: " + coverImage.getWidth() + "\n"
                        + "Tinggi: " + coverImage.getHeight() + "\n"
                        + "Ukuran: " + (fg.length() / 1024) + " KB\n"
                );
                initImage = true;
                this.imgOriName = fg.getName();
            } catch (MalformedURLException mue) {
                alert = new Alert(Alert.AlertType.INFORMATION,
                        "Gagal mengupload gambar ke aplikasi",
                        ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("Terjadi Exception ketika mengakses File");
                alert.show();
            } catch (Exception exc) {
                alert = new Alert(Alert.AlertType.INFORMATION,
                        "Terjadi kesalahan : " + exc.getMessage(),
                        ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("Terjadi Exception PATH File");
                alert.show();
            }

            if (initImage) {
                try {
                    ImageInputStream imageInputStream = ImageIO.createImageInputStream(fg);
                    Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
                    if (readers.hasNext()) {
                        imageInputStream.flush();
                        ImageReader imageReader = readers.next();
                        imageReader.setInput(imageInputStream, true);
                        IIOMetadata metadata = imageReader.getImageMetadata(0);

                        String[] names = metadata.getMetadataFormatNames();
                        int lennames = names.length;
                        this.imgProperty = "";
                        for (int i = 0; i < lennames; i += 1) {
                            Node node = metadata.getAsTree(names[i]);
                            //displayMetadata(node, this.imgProperty);
                            this.imgProperty = PengolahanCitra.ImageInfo.displayMetadata(node, this.imgProperty);
                        }
                        if (this.imgProperty.contains("bitDepth=8")) {
                            this.bitDepthImg = "8";
                        }
                        else if (this.imgProperty.contains("BitsPerSample value=8 8 8")) {
                            this.bitDepthImg = "24";
                            this.isBmp24Bit = true;
                            this.coverImgWidth = buffCoverImg.getWidth();
                            this.coverImgHeight = buffCoverImg.getHeight();
                            this.coverImgType = buffCoverImg.getType();
                        }
                        else if (this.imgProperty.contains("BitsPerSample value=8 8 8") ||
                                this.imgProperty.contains("BitsPerSample value=8 88")) {
                            this.bitDepthImg = "32";
                        }
                    }
                    textInfoCoverImg.setText(
                            textInfoCoverImg.getText() + "\n" +
                                    "Bit Depth: " + this.bitDepthImg + "\n"
                    );
                } catch (Exception exceptionReadProperty) {
                    AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                            "Pembacaan Properti Gambar",
                            "Terjadi exception ketika membaca properti gambar",
                            ButtonType.OK);
                }
            }

            if (checkStegoData()) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi",
                        "Perhatian",
                        "Pesan atau Citra yang Anda berikan sudah tersedia di database..\n"
                                + "Harap berikan data yang berbeda.",
                        ButtonType.OK
                );
            }
            else {
                if (this.isBmp24Bit) {
                    int np = this.coverImgWidth * this.coverImgHeight;
                    this.rgbDataOfImage = new int[buffCoverImg.getWidth() * buffCoverImg.getHeight()];
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
                            for (int x = 0; x < buffCoverImg.getWidth(); x += 1) {
                                for (int y = 0; y < buffCoverImg.getHeight(); y += 1) {
                                    this.rgbDataOfImage[indeks] = buffCoverImg.getRGB(x, y);
                                    indeks += 1;
                                }
                            }
                            this.btnEmbedMessage.setDisable(false);
                        } catch (Exception except) {
                            AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                                    "Membaca Data RGB Gambar",
                                    "Terjadi kesalahan ketika membaca data RGB Stego Image",
                                    ButtonType.OK
                            );
                        }
                    } else {
                        AlertInfo.showAlertWarningMessage("Informasi Aplikasi",
                                "Perhatian",
                                "Image yang Anda input untuk disisipi pesan tidak sanggup menampung pesan Anda.\n"
                                        + "Harap upload ulang image yang lebih besar.",
                                ButtonType.OK
                        );
                    }

                } else {
                    AlertInfo.showAlertWarningMessage("Informasi Aplikasi",
                            "Validasi Awal Stego Image",
                            "Stego Image tidak mendukung.\n" +
                            "Bit Depth Stego Image harus 24 dan berformat .bmp",
                            ButtonType.OK);
                }
            }

        }
    }

    @FXML
    private void handleBtnEmbedMessage(ActionEvent event) {
        boolean konversiMsg = false, konversiKunci = false, penyisipan = false;
        char[] arrKunci = txtInputPass.getText().toCharArray();
        String msgInBinary = "";
        String kunciInBinary = "";
        String msgLengthInfoInBinary = KonversiData.paddingInLeftBinaryString(
                                        Integer.toBinaryString(this.nOfChiperLenOnByte),
                                        this.xnm
                                    );
        try {
            progressBar.setProgress(0.09d);
            for (ArrayList<Integer> data: this.dataEncrypt) {
                for (int i = 0; i < data.size(); i += 1) {
                    msgInBinary += KonversiData.paddingInLeftBinaryString(Integer.toBinaryString(data.get(i)),8);
                }
            }
            for (int i = 0; i < arrKunci.length; i += 1) {
                kunciInBinary += KonversiData.paddingInLeftBinaryString(Integer.toBinaryString((int)arrKunci[i]), 8);
            }
            kunciInBinary = KonversiData.paddingInRightBinaryString(kunciInBinary, NK_ON_BYTE);
            updateProgressBar(0.12d);
            Thread.sleep(250);
            konversiMsg = true;
            konversiKunci = true;
        } catch (Exception except) {
            AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                    "Konversi Msg to binary",
                    "Terjadi kesalahan ketika proses konversi chipertext ke dalam notasi binary",
                    ButtonType.OK
            );
        }

        if (konversiMsg && konversiKunci) {
            this.gemodNumber = Matematik.gemodFinder(this.primeNumber.intValue());

            try {
                //lakukan counter
                startCountTime();
                int pixelIterator = 1;
                BigInteger gemodNums = new BigInteger(this.gemodNumber + "", 10);
                byte siklusRGB = 1;

                updateProgressBar(0.22d);

                //for untuk sisip informasi panjang pesan
                for (int i = 0; i < msgLengthInfoInBinary.length(); i += 1) {
                    int bitPesan = Integer.parseInt(msgLengthInfoInBinary.charAt(i) + "", 10);
                    BigInteger random = gemodNums.modPow(new BigInteger(pixelIterator + "", 10),
                                                         this.primeNumber);
                    //int argb = this.rgbDataOfImage[random.intValue()];
                    int a = (this.rgbDataOfImage[random.intValue()]>>24)&0x000000FF;
                    int r = (this.rgbDataOfImage[random.intValue()]>>16)&0x000000FF;
                    int g = (this.rgbDataOfImage[random.intValue()]>>8)&0x000000FF;
                    int b = (this.rgbDataOfImage[random.intValue()])&0x000000FF;

                    if (siklusRGB > 3) {
                        siklusRGB = 1;
                    }

                    //jika hasil parity even
                    if (ParityChecker.generateParity(r,g,b) == 0) {
                        if (bitPesan == 1) { //lakukan Reverse
                            if (siklusRGB == 1) { //ubah R
                                r = ParityChecker.takeChannelRGBtoReverse(r);
                            }
                            else if (siklusRGB == 2) { //ubah G
                                g = ParityChecker.takeChannelRGBtoReverse(g);
                            }
                            else if (siklusRGB == 3) { //ubah B
                                b = ParityChecker.takeChannelRGBtoReverse(b);
                            }
                            siklusRGB += 1;
                        } else { // no change
                        }
                        this.rgbDataOfImage[random.intValue()] = ((a << 24) | (r << 16) | (g << 8) | b);
                    }
                    else { //hasil parity odd
                        if (bitPesan == 0) { //lakukan Reverse
                            if (siklusRGB == 1) { //ubah R
                                r = ParityChecker.takeChannelRGBtoReverse(r);
                            }
                            else if (siklusRGB == 2) { //ubah G
                                g = ParityChecker.takeChannelRGBtoReverse(g);
                            }
                            else if (siklusRGB == 3) { //ubah B
                                b = ParityChecker.takeChannelRGBtoReverse(b);
                            }
                            siklusRGB += 1;
                        } else { //no change
                        }
                        this.rgbDataOfImage[random.intValue()] = ((a << 24) | (r << 16) | (g << 8) | b);
                    }
                    pixelIterator += 1;
                } //end for untuk sisip informasi panjang pesan
                updateProgressBar(0.32d);
                Thread.sleep(250);

                //for untuk sisi kunci
                for (int i = 0; i < kunciInBinary.length(); i += 1) {
                    int bitPesan = Integer.parseInt(kunciInBinary.charAt(i) + "", 10);
                    BigInteger random = gemodNums.modPow(new BigInteger(pixelIterator + "", 10),
                                                         this.primeNumber);
                    //int argb = this.rgbDataOfImage[random.intValue()];
                    int a = (this.rgbDataOfImage[random.intValue()]>>24)&0x000000FF;
                    int r = (this.rgbDataOfImage[random.intValue()]>>16)&0x000000FF;
                    int g = (this.rgbDataOfImage[random.intValue()]>>8)&0x000000FF;
                    int b = (this.rgbDataOfImage[random.intValue()])&0x000000FF;

                    if (siklusRGB > 3) {
                        siklusRGB = 1;
                    }
                    //jika hasil parity even
                    if (ParityChecker.generateParity(r,g,b) == 0) {
                        if (bitPesan == 1) { //lakukan Reverse
                            if (siklusRGB == 1) { //ubah R
                                r = ParityChecker.takeChannelRGBtoReverse(r);
                            }
                            else if (siklusRGB == 2) { //ubah G
                                g = ParityChecker.takeChannelRGBtoReverse(g);
                            }
                            else if (siklusRGB == 3) { //ubah B
                                b = ParityChecker.takeChannelRGBtoReverse(b);
                            }
                            siklusRGB += 1;
                        } else { // no change
                        }
                        this.rgbDataOfImage[random.intValue()] = ((a << 24) | (r << 16) | (g << 8) | b);
                    }
                    else { //hasil parity odd
                        if (bitPesan == 0) { //lakukan Reverse
                            if (siklusRGB == 1) { //ubah R
                                r = ParityChecker.takeChannelRGBtoReverse(r);
                            }
                            else if (siklusRGB == 2) { //ubah G
                                g = ParityChecker.takeChannelRGBtoReverse(g);
                            }
                            else if (siklusRGB == 3) { //ubah B
                                b = ParityChecker.takeChannelRGBtoReverse(b);
                            }
                            siklusRGB += 1;
                        } else { //no change
                        }
                        this.rgbDataOfImage[random.intValue()] = ((a << 24) | (r << 16) | (g << 8) | b);
                    }
                    pixelIterator += 1;
                } //END FOR SISI KUNCI
                updateProgressBar(0.56d);
                Thread.sleep(150);
                //for untuk sisip pesan chiper
                for (int i = 0; i < msgInBinary.length(); i += 1) {
                    int bitPesan = Integer.parseInt(msgInBinary.charAt(i) + "", 10);
                    BigInteger random = gemodNums.modPow(new BigInteger(pixelIterator + "", 10),
                                                         this.primeNumber);
                    int a = (this.rgbDataOfImage[random.intValue()]>>24)&0x000000FF;
                    int r = (this.rgbDataOfImage[random.intValue()]>>16)&0x000000FF;
                    int g = (this.rgbDataOfImage[random.intValue()]>>8)&0x000000FF;
                    int b = (this.rgbDataOfImage[random.intValue()])&0x000000FF;

                    if (siklusRGB > 3) {
                        siklusRGB = 1;
                    }
                    //jika hasil parity even
                    if (ParityChecker.generateParity(r,g,b) == 0) {
                        if (bitPesan == 1) { //lakukan Reverse
                            if (siklusRGB == 1) { //ubah R
                                r = ParityChecker.takeChannelRGBtoReverse(r);
                            }
                            else if (siklusRGB == 2) { //ubah G
                                g = ParityChecker.takeChannelRGBtoReverse(g);
                            }
                            else if (siklusRGB == 3) { //ubah B
                                b = ParityChecker.takeChannelRGBtoReverse(b);
                            }
                            siklusRGB += 1;
                        } else { // no change
                        }
                        this.rgbDataOfImage[random.intValue()] = ((a << 24) | (r << 16) | (g << 8) | b);
                    }
                    else { //hasil parity odd
                        if (bitPesan == 0) { //lakukan Reverse
                            if (siklusRGB == 1) { //ubah R
                                r = ParityChecker.takeChannelRGBtoReverse(r);
                            }
                            else if (siklusRGB == 2) { //ubah G
                                g = ParityChecker.takeChannelRGBtoReverse(g);
                            }
                            else if (siklusRGB == 3) { //ubah B
                                b = ParityChecker.takeChannelRGBtoReverse(b);
                            }
                            siklusRGB += 1;
                        } else { //no change
                        }
                        this.rgbDataOfImage[random.intValue()] = ((a << 24) | (r << 16) | (g << 8) | b);
                    }
                    pixelIterator += 1;
                }
                updateProgressBar(0.95d);
                penyisipan = true;
                stopCountTime();
            } catch (Exception exceptionEmbedding) {
                penyisipan = false;
                AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                        "Embedding Proses",
                        "Terjadi kesalahan saat proses penyisipan pesan" +
                        "Detail error: " + exceptionEmbedding.getMessage(),
                        ButtonType.OK
                );
            }

            if (penyisipan && createStegoImage()) {
                imgViewStego.setImage(SwingFXUtils.toFXImage(this.stegoImage, null));
                imgViewStego.setFitWidth(292);
                this.btnEmbedMessage.setDisable(true);
                btnSaveStegoImg.setDisable(false);
                this.hasData = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(150);
                            progressBar.setProgress(1.00);
                            Thread.sleep(250);
                            progressBar.setVisible(false);
                        } catch (Exception e) {}
                    }
                });
            }
        }
    }

    @FXML
    private void handleBtnSaveStegoImg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buka Berkas Gambar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));
        boolean res = false;
        fileChooser.setInitialFileName(this.imgOriName.substring(0, this.imgOriName.lastIndexOf('.'))
                                        + "_" + this.textLength + "_stego");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            //String selected_desc = fileChooser.getSelectedExtensionFilter().getDescription();
            String extension = "bmp";
            boolean sukses = true;
            if (file.getName().endsWith(".bmp"))
                extension = "bmp";
            else
                extension = "bmp";
            try {
                res = ImageIO.write(this.stegoImage, extension, file);
                this.imgStegoName = file.getName();
            } catch (Exception e) {
                AlertInfo.showAlertErrorMessage("Informasi Kesalahan",
                        "Penyimpanan Gambar Gagal",
                        "Gagal menyimpan gambar ke direktori",
                        ButtonType.OK);
            }
        }
        if (res) {
            //simpan data ke database
            boolean dbState = false;
            int val = -1;
            try {
                int n = 0;
                n = MainController.appControll.sqLiteDB.getJumlahBaris(AppControll.TABLE_STEGANO_MASTER);
                n += 1;
                String sql = "INSERT INTO " + AppControll.TABLE_STEGANO_MASTER + " VALUES("
                        + n + ", '"
                        + this.imgOriName + "', '"
                        + this.imgStegoName + "', "
                        + this.textLength
                        + ");";
                val = MainController.appControll.sqLiteDB.InsertUpdateDeleteQuery(sql);
                MainController.appControll.sqLiteDB.closeConnection();
                if (val > 0) {
                    dbState = true;
                }
            } catch (Exception e) {
                AlertInfo.showAlertInfoMessage("Informasi Aplikasi",
                        "Database",
                        "Terjadi kesalahan: " + e.getMessage(),
                        ButtonType.OK
                );
            }
            String teksStatus = "";
            if (dbState) {
                teksStatus = "\nPenyimpanan data ke database berhasil dilakukan.";
            } else {
                teksStatus = "\nGagal menyimpan data ke database. Data ini tidak akan valid di sesi ekstraksi.";
            }
            AlertInfo.showAlertInfoMessage("Informasi Aplikasi",
                    "Penyimpanan Stego Image",
                    "Proses menyimpan stego image ke direktori berhasil dilakukan" + teksStatus,
                    ButtonType.OK
            );
            this.btnSaveStegoImg.setDisable(true);
            this.txtInputPass.setEditable(false);
        }
    }

    @FXML
    private void handleBtnMainMenu(ActionEvent event) {
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

    private void startCountTime() throws Exception {
        processTimer = true;
        taskTimer = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int counter = 0;
                short detik = 0;
                while (processTimer) {
                    if (counter > 59) {

                        txtfieldDetik.setText(
                                (detik < 10) ? ("0" + detik) : ("" + detik)
                        );
                        counter = 0;
                    }
                    counter += 1;
                    if (counter < 10) {
                        txtfieldMs.setText("0" + counter);
                    } else {
                        txtfieldMs.setText(counter + "");
                    }
                }
                notify();
                return null;
            }
        };

        threadTimer = new Thread(taskTimer);
        threadTimer.start();
    }

    private void stopCountTime() throws Exception {
        if (threadTimer.isAlive()) {
            threadTimer.interrupt();
            threadTimer = null;
            processTimer = false;
        }
    }

    @FXML void handleBtnNextExtraction (ActionEvent actionEvent) {
        Parent p = null;
        boolean loadSukses = true;
        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("stegano/extractiondoc.fxml"));
            if (p == null) {
                loadSukses = false;
            }
        } catch (IOException ex) {
            loadSukses = false;
        }

        if (!loadSukses) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Open Scene",
                    "Gagal membuka scene Extraction",
                    ButtonType.OK
            );
        } else {
            Main.mainStage.setTitle("Aplikasi Steganografi: Extraction");
            Main.mainStage.setScene(new Scene(p));
            Main.mainStage.centerOnScreen();
        }
    }

    private boolean createStegoImage() {
        boolean proses = false;
        this.stegoImage = new BufferedImage(this.coverImgWidth, this.coverImgHeight, this.coverImgType);
        int indeks = 0;
        try {
            for (int x = 0; x < this.coverImgWidth; x += 1) {
                for (int y = 0; y < this.coverImgHeight; y += 1) {
                    this.stegoImage.setRGB(x, y, this.rgbDataOfImage[indeks]);
                    indeks += 1;
                }
            }
            proses = true;
        } catch (Exception e) { this.stegoImage = null; }
        return proses;
    }

    private boolean doEncrypt() {
        this.dataPesan = new ArrayList<>();
        this.pesanArrChar = textInputMessage.getText().toCharArray();
        char[] kunciArrChar = txtInputPass.getText().toCharArray();
        int[] kunciEncrypt = new int[kunciArrChar.length];
        boolean prosesPotong = true;
        this.enkripProsesSukses = false;
        int indeksStart = 0;
        int panjangTeks = this.textInputMessage.getText().length();
        HAES256 haes256 = HAES256.getInstance();

        try {
            updateProgressBar(progressBar.getProgress() + 0.02);
            for (short i = 0; i < kunciArrChar.length; i += 1) {
                kunciEncrypt[i] = (int)kunciArrChar[i];
            }

            while (prosesPotong) {
                ArrayList<Integer> tmpPesan = new ArrayList<>();
                String subs = "";
                if (indeksStart + 16 >= this.textInputMessage.getText().length()) {
                    subs = this.textInputMessage.getText(indeksStart, panjangTeks);
                    prosesPotong = false;
                }
                else {
                    subs = this.textInputMessage.getText(indeksStart, indeksStart + 16);
                }
                char[] tmpChar = subs.toCharArray();
                for (int i = 0; i < tmpChar.length; i += 1) {
                    tmpPesan.add((int)tmpChar[i]);
                }
                dataPesan.add(tmpPesan);
                indeksStart += 16;
            }
            this.dataEncrypt = new ArrayList<>();
            updateProgressBar(progressBar.getProgress() + 0.02);

            for (int i = 0; i < this.dataPesan.size(); i += 1) {
                ArrayList<Integer> subData = new ArrayList<>();
                try {
                    if (haes256.encryptDataInteger(
                            KonversiData.arraylist1DToArr1D(this.dataPesan.get(i)),
                            kunciEncrypt
                    )) {
                        for (Integer val : KonversiData.arr2DToIntArr1D(haes256.getArrPesan())) {
                            subData.add(val);
                        }
                        this.dataEncrypt.add(subData);
                        if (progressBar.getProgress() < 91) {
                            updateProgressBar(progressBar.getProgress() + 0.04);
                        }
                    } else {
                        throw new Exception("Kesalahan proses enkripsi");
                    }
                    this.enkripProsesSukses = true;
                } catch (Exception ex) {
                    AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                            "Proses Enkripsi AES-256",
                            "Terjadi kesalahan proses: \n" + ex.getMessage(),
                            ButtonType.OK);
                    this.enkripProsesSukses = false;
                }

            }
            updateProgressBar(94.22);
        } catch (Exception except) {
            AlertInfo.showAlertErrorMessage("Informasi Aplikasi: Embedding",
                    "Proses Enkripsi",
                    "Terjadi kesalahan pada proses enkrispi.\n" +
                    "Error detail: " + except.getMessage(),
                    ButtonType.OK
            );
        }

        if (this.enkripProsesSukses) {
            String hasilEnkrip = "";
            for (ArrayList<Integer> val : dataEncrypt) {
                for (int i = 0; i < val.size(); i += 1) {
                    hasilEnkrip += Character.toString((char)val.get(i).intValue());
                }
            }
            try {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i += 1) {
                            progressBar.setProgress(progressBar.getProgress() + 0.02);
                        }
                        progressBar.setProgress(progressBar.getProgress() + 0.02);
                    }
                });
                updateProgressBar(99.89);
                textChiper.setText(hasilEnkrip);
                btnBrowseCoverImg.setDisable(false);
            } catch (Exception e) {}

        }
        //progressBar.setVisible(false);
        return this.enkripProsesSukses;
    }

    private boolean checkStegoData() {
        boolean sameData = false;

        String q = "SELECT oriImageName, stegoImageName, msgLength FROM " + AppControll.TABLE_STEGANO_MASTER;
        MainController.initApp();
        int initAppControl;
        ResultSet resultSet;

        try {
            initAppControl = MainController.appControll.init();
            if (initAppControl == 1) {
                resultSet = MainController.appControll.sqLiteDB.SelectQuery(q);
                while (resultSet.next()) {
                    if (this.imgOriName.equalsIgnoreCase(resultSet.getString("stegoImageName"))) {
                        sameData = true;
                        break;
                    }
                    else if (this.imgOriName.equalsIgnoreCase(resultSet.getString("oriImageName")) &&
                        this.textLength == resultSet.getInt("msgLength")) {
                        sameData = true;
                        break;
                    }
                }
                resultSet.close();
                MainController.appControll.sqLiteDB.closeConnection();
            }
        } catch (Exception e) {
            sameData = false;
            AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                    "Inisialisasi Aplikasi",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK);
        }
        return sameData;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                btnBrowseText.setGraphic(
                        new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "filtxt-icon16.png"))
                );
                btnBrowseCoverImg.setGraphic(
                        new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "bmp-icon16.png"))
                );
                btnSaveStegoImg.setGraphic(
                        new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "bmp-icon16.png"))
                );
                btnMainMenu.setGraphic(
                        new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "arrowleft16.png"))
                );
                btnNext.setGraphic(
                        new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "arrowright16.png"))
                );
                imgViewCover.setImage(new Image(MainController.resouresDir.toURI().toURL().toString() + "image-invalid.png"));
                imgViewStego.setImage(new Image(MainController.resouresDir.toURI().toURL().toString() + "image-invalid.png"));
                return null;
            }
        };
        try {
            new Thread(task).start();
        } catch (Exception e) {
            AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                    "Inisialisasi Gambar",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK);
        }

        Platform.isSupported(ConditionalFeature.INPUT_METHOD);
        textInputMessage.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                handleTextInputMessage();
            }
        });
        textInputMessage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                handleTextInputMessage();
            }
        });
    }
}
