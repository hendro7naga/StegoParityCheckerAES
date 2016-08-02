package stegano;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import kelas.*;
import kripto.HAES256;
import main.Main;
import main.MainController;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by hendro.sinaga on 25-Jun-16.
 */
public class ExtractionController implements Initializable {
    BufferedImage stegoImage = null;
    Image coverImage;
    String imgPath = "", imgProperty = "", bitDepthImg = "";
    BigInteger primeNumber;
    String chiperTextInBiner = "";
    List<ArrayList<Integer>> dataEncrypt = null;
    List<ArrayList<Integer>> dataDecrypt = null;
    int[] rgbDataOfImage;
    int nOfChiperLenOnByte = 0, nOfPixelForEmbedding = -1;
    int coverImgWidth = 0, coverImgHeight = 0, coverImgType = -1, xnm = -1, gemodNumber = -1, np;
    final int NK_ON_BYTE = 256;
    char[] arrCharTeksOri;
    boolean isBmp24 = false, kunciSama = false;
    @FXML
    Label lblInfoStego;
    @FXML
    TextField txtInputPass;
    @FXML
    TextArea textAreaChiper, textAreaOri;
    @FXML
    Button btnBrowseStegoImg, btnExtract, btnDecrypt, btnSaveText, btnMainMenu;
    @FXML
    ImageView imgViewStegoImg;

    @FXML
    private void handleBtnBrowse(ActionEvent actionEvent) {
        this.primeNumber = new BigInteger("0", 10);
        this.kunciSama = false;
        this.isBmp24 = false;
        this.rgbDataOfImage = null;
        this.dataEncrypt = null;
        this.dataDecrypt = null;
        this.btnExtract.setDisable(true);
        this.btnDecrypt.setDisable(true);
        this.chiperTextInBiner = "";
        boolean loadImage = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browse Stego Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));
        File file = fileChooser.showOpenDialog(Main.mainStage);

        if (file != null) {
            try {
                this.imgPath = file.toURI().toURL().toString();
                this.coverImage = new Image(imgPath);
                this.imgViewStegoImg.setImage(coverImage);
                this.stegoImage = SwingFXUtils.fromFXImage(this.coverImage, null);
                lblInfoStego.setText("Nama File: " + file.getName());
                loadImage = true;
                this.txtInputPass.clear();
                this.txtInputPass.setEditable(true);
            } catch (MalformedURLException malformedURLException) {
                AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                        "File Path",
                        "Gagal mengakses lokasi gambar",
                        ButtonType.OK
                );
            }
        }

        if (loadImage) {
            lblInfoStego.setText(
                    lblInfoStego.getText() + "\n"
                            + "Width: " + this.stegoImage.getWidth() + "\n"
                            + "Height: " + this.stegoImage.getHeight() + "\n"
                            + "Ukuran: " + (file.length() / 1024) + " KB\n"
            );
            try {
                ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
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
                        this.imgProperty = PengolahanCitra.ImageInfo.displayMetadata(node, this.imgProperty);
                    }
                    if (this.imgProperty.contains("bitDepth=8")) {
                        this.bitDepthImg = "8";
                    }
                    else if (this.imgProperty.contains("BitsPerSample value=8 8 8")) {
                        this.bitDepthImg = "24";
                        this.isBmp24 = true;
                        this.coverImgWidth = this.stegoImage.getWidth();
                        this.coverImgHeight = this.stegoImage.getHeight();
                        this.coverImgType = this.stegoImage.getType();
                    }
                    else if (this.imgProperty.contains("BitsPerSample value=8 8 8") ||
                            this.imgProperty.contains("BitsPerSample value=8 88")) {
                        this.bitDepthImg = "32";
                    }
                }
                lblInfoStego.setText(
                        lblInfoStego.getText() +
                                "Bit Depth: " + this.bitDepthImg
                );
                if (!this.isBmp24) {
                    AlertInfo.showAlertInfoMessage("Informasi Aplikasi",
                            "Invalid Stego Image Type",
                            "Stego Image tidak valid. Sistem hanya mendukung bit depth 24 stego image.\n" +
                            "Jika tidak, sistem tidak akan memproses input stego image.",
                            ButtonType.OK
                    );
                } else {
                    np = this.stegoImage.getWidth() * this.stegoImage.getHeight();
                    this.rgbDataOfImage = new int[this.stegoImage.getWidth() * this.stegoImage.getHeight()];
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
                            for (int x = 0; x < this.stegoImage.getWidth(); x += 1) {
                                for (int y = 0; y < this.stegoImage.getHeight(); y += 1) {
                                    this.rgbDataOfImage[indeks] = this.stegoImage.getRGB(x, y);
                                    indeks += 1;
                                }
                            }
                        } catch (Exception except) {
                            AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                                    "Membaca Data RGB Gambar",
                                    "Terjadi kesalahan ketika membaca data RGB Stego Image",
                                    ButtonType.OK
                            );
                        }
                    }
                }
            } catch (Exception exceptionReadProperty) {
                AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                        "Pembacaan Properti Gambar",
                        "Terjadi exception ketika membaca properti gambar",
                        ButtonType.OK);
            }
        }
    }

    @FXML
    private void handleInputPassword(KeyEvent handler) {
        if (txtInputPass.getText().length() > 4) {
            if (btnExtract.isDisable()) {
                btnExtract.setDisable(false);
            }
            if (this.txtInputPass.getText().length() > 32) {
                AlertInfo.showAlertWarningMessage(
                        "Informasi Aplikasi: Extraction",
                        "Peringatan",
                        "Panjang Kunci maksimal adalah 32 Karakter.",
                        ButtonType.OK
                );
                this.txtInputPass.setText(this.txtInputPass.getText().substring(0, 32));
                this.txtInputPass.positionCaret(32);
            }
        } else {
            if (!this.isBmp24) {
                btnExtract.setDisable(true);
            }
        }
    }

    @FXML
    private void handleLengthInputPass(KeyEvent handler) {
        /*if (this.txtInputPass.getText().length() > 32) {
            AlertInfo.showAlertWarningMessage("Informasi Aplikasi",
                    "Peringatan",
                    "Panjang Kunci maksimal adalah 32 Karakter.",
                    ButtonType.OK
            );
            this.txtInputPass.setText(this.txtInputPass.getText().substring(0, 32));
            this.txtInputPass.positionCaret(32);
        }*/
    }

    @FXML
    private void handleBtnExtraction(ActionEvent actionEvent) {
        this.gemodNumber = Matematik.gemodFinder(this.primeNumber.intValue());
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

            String strKunci = "";
            boolean potonglagi = true;
            int indeks = 0;
            //this.textAreaOri.appendText("\nDeretan nilaiInteger kunci: \n");
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
            char[] arrKunciInput = this.txtInputPass.getText().toCharArray();
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
                //this.textAreaOri.appendText("\nKunci input dan tersimpan sama\n");
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
                AlertInfo.showAlertInfoMessage("Informasi Aplikasi",
                        "Kunci Invalid",
                        "Maaf!!!\nKunci yang Anda berikan tidak sesuai.",
                        ButtonType.OK
                );
            }

        } catch (Exception exception) {
            AlertInfo.showAlertErrorMessage("Error",
                    "Proses Extraksi",
                    "Terjadi kesalahan ketika proses ekstraksi pesan"
                    + "Detail Eror: " + exception.getMessage(),
                    ButtonType.OK
            );
        }
        if (this.chiperTextInBiner.length() >= 16) {
            doExtractMessage();
        }
    }

    private void doExtractMessage() {
        boolean potonglagi = true, prosesKonversiBinerPesanEkstraksi = false;
        int indeks = 0;
        this.dataEncrypt = new ArrayList<>();
        try {

            while (potonglagi) {
                ArrayList<Integer> tmp = new ArrayList<>();
                for (int i = 0; i < 16; i += 1) {

                    if (indeks + 8 >= this.chiperTextInBiner.length()) {
                        tmp.add(Integer.parseInt(
                                this.chiperTextInBiner.substring(indeks, this.chiperTextInBiner.length()),
                                2)
                        );
                        potonglagi = false;
                        i = 16;
                    } else {
                        tmp.add(
                                Integer.parseInt(this.chiperTextInBiner.substring(indeks, indeks + 8),2)
                        );
                    }
                    indeks += 8;
                }
                this.dataEncrypt.add(tmp);
            }
            prosesKonversiBinerPesanEkstraksi = true;
        } catch (Exception exception) {
            AlertInfo.showAlertErrorMessage("Error",
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
            this.textAreaChiper.setText(strDataEnkrip);
            this.btnDecrypt.setDisable(false);
            this.txtInputPass.setEditable(false);
            this.btnExtract.setDisable(true);
        }
    }

    @FXML
    private void handleBtnDecrypt(ActionEvent actionEvent) {
        boolean dekripStatus = false;
        char[] arrCharKunci = txtInputPass.getText().toCharArray();
        int[] kunciDecrypt = new int[arrCharKunci.length];
        for (short i = 0; i < arrCharKunci.length; i += 1) {
            kunciDecrypt[i] = (int)arrCharKunci[i];
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
            this.textAreaOri.clear();
            this.textAreaOri.setText(hasilDekrip);
            this.btnDecrypt.setDisable(true);
            this.btnSaveText.setDisable(false);
        }
    }

    @FXML
    private void handleBtnSaveText(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Teks File (.txt)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Berkas Teks", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            saveFile(String.valueOf(this.arrCharTeksOri), file);
        }

    }

    @FXML private void handleMainMenu(ActionEvent actionEvent) {
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

    private void saveFile(String teks, File file) {
        try {

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (int i = 0; i < this.arrCharTeksOri.length; i += 1) {
                if ((int)this.arrCharTeksOri[i] == 10) {
                    bufferedWriter.newLine();
                } else {
                    bufferedWriter.write(arrCharTeksOri[i]);
                }
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
            //printWriter.close();
            AlertInfo.showAlertInfoMessage("Informasi Aplikasi: Extraction",
                    "Penyimpanan Berkas .txt",
                    "Penyimpanan data teks .txt ke direktori berhasil dilakukan.",
                    ButtonType.OK
            );
            this.btnSaveText.setDisable(true);
        } catch (IOException ioexception) {
            AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                    "Penyimpanan Berkas .txt",
                    "Terjadi kesalahan ketika proses penyimpanan berkas .txt ke direktori",
                    ButtonType.OK
            );
        }
        finally {
            System.gc();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.isSupported(ConditionalFeature.INPUT_METHOD);
        try {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    btnSaveText.setGraphic(
                            new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "filtxt-icon16.png"))
                    );
                    btnBrowseStegoImg.setGraphic(
                            new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "bmp-icon16.png"))
                    );

                    imgViewStegoImg.setImage(new Image(MainController.resouresDir.toURI().toURL().toString() + "image-invalid.png"));
                    btnMainMenu.setGraphic(
                            new ImageView(new Image(MainController.resouresDir.toURI().toURL().toString() + "arrowleft16.png"))
                    );
                    return null;
                }
            };
            new Thread(task).start();
        } catch (Exception e) {
            AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                    "Kesalahan Akeses File",
                    "Error Detail: " + e.getMessage(),
                    ButtonType.OK);
        }
    }
}
