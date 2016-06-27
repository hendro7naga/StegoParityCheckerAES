package stegano;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import main.Main;
import main.MainController;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;
import kripto.HAES256;
import kelas.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

/**
 * Created by hendro.sinaga on 08-Jun-16.
 */
public class EmbeddingController implements Initializable {
    BufferedImage stegoImage = null;
    Image coverImage;
    String imgPath = "", imgProperty = "", bitDepthImg = "";
    BigInteger primeNumber = new BigInteger("0", 10);
    char[] pesanArrChar;
    List<ArrayList<Integer>> dataPesan = null;
    List<ArrayList<Integer>> dataEncrypt = null;
    int nOfChiperLenOnByte = 0, nOfPixelForEmbedding = -1;
    int coverImgWidth = 0, coverImgHeight = 0, coverImgType = -1, xnm = -1, gemodNumber = -1;
    int[] rgbDataOfImage = null;
    final int NK = 32;
    final int NK_ON_BYTE = 256;
    boolean isBmp24Bit = false;
    boolean enkripProsesSukses = false;
    Alert alert;
    @FXML
    TextArea textInputMessage, textChiper;
    @FXML
    Text txtInfoMessage,textInfoStegoImg, textInfoCoverImg;
    @FXML
    PasswordField txtInputPass;
    @FXML
    Button btnEncrypt, btnBrowseCoverImg, btnEmbedMessage, btnMainMenu, btnSaveStegoImg, btnNext;
    @FXML
    ImageView imgViewCover, imgViewStego;

    @FXML
    private void handleBrowseFileText(ActionEvent event) {

        boolean sukses = true;
        byte[] byteTeks = null;
        String textFile = "";
        FileChooser fc = new FileChooser();
        fc.setTitle("Buka Berkas Teks");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Berkas Teks", "*.txt"));

        File fileTeks = fc.showOpenDialog(null);

        if (fileTeks != null) {
            try {
                byteTeks = Files.readAllBytes(Paths.get(fileTeks.getPath()));
                textInputMessage.setText(new String(byteTeks));
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
        int len = textInputMessage.getText().length();
        txtInfoMessage.setText("Text Length : " + len + " Byte");
    }

    @FXML
    private void handleInputPassword(KeyEvent handler) {
        if (txtInputPass.getText().length() > 4) {
            btnEncrypt.setDisable(false);
        } else {
            if (!(textInputMessage.getText().length() < 1)) {
                btnEncrypt.setDisable(true);
            }
        }
    }

    @FXML
    private void handleBtnEncrypt(ActionEvent event) {
        if (textInputMessage.getText().length() < 3 || txtInputPass.getText().length() < 4) {
            btnBrowseCoverImg.setDisable(true);
        } else {
            int n = textInputMessage.getText().length();
            nOfChiperLenOnByte = n + ((16 - (n % 16)) % 16);
            nOfChiperLenOnByte *= 8;
            if (doEncrypt()) {
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

        File fg = fc.showOpenDialog(null);
        if (fg != null) {
            try {
                imgPath = fg.toURI().toURL().toString();
                coverImage = new Image(imgPath);
                buffCoverImg = SwingFXUtils.fromFXImage(coverImage, null);
                imgViewCover.setImage(coverImage);
                textInfoCoverImg.setText(
                        "Nama File: " + fg.getName() + "\n"
                        + "Lebar: " + coverImage.getWidth() + "\n"
                        + "Tinggi: " + coverImage.getHeight()
                );
                initImage = true;

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
                            displayMetadata(node, this.imgProperty);
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
                                    "Bit Depth: " + this.bitDepthImg
                    );
                } catch (Exception exceptionReadProperty) {
                    AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                            "Pembacaan Properti Gambar",
                            "Terjadi exception ketika membaca properti gambar",
                            ButtonType.OK);
                }
            }

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
                }
                textChiper.appendText(
                        "\nJumlah Pixel yang dibutuhkan: " + this.nOfPixelForEmbedding + "\n"
                );
            } else {
                AlertInfo.showAlertInfoMessage("Informasi Aplikasi",
                        "Validasi Awal Stego Image",
                        "Stego Image tidak mendukung.\n" +
                        "Bit Depth Stego Image harus 24 dan berformat .bmp",
                        ButtonType.OK);
            }

        }

    }

    @FXML
    private void handleBtnEmbedMessage(ActionEvent event) {
        boolean konversiMsg = false, konversiKunci = false;
        char[] arrKunci = txtInputPass.getText().toCharArray();
        String msgInBinary = "";
        String kunciInBinary = "";
        String msgLengthInfoInBinary = KonversiData.paddingInLeftBinaryString(
                                        Integer.toBinaryString(this.nOfChiperLenOnByte),
                                        this.xnm
                                    );
        try {
            for (ArrayList<Integer> data: this.dataEncrypt) {
                for (int i = 0; i < data.size(); i += 1) {
                    msgInBinary += KonversiData.paddingInLeftBinaryString(Integer.toBinaryString(data.get(i)),8);
                }
            }
            for (int i = 0; i < arrKunci.length; i += 1) {
                kunciInBinary += KonversiData.paddingInLeftBinaryString(Integer.toBinaryString((int)arrKunci[i]), 8);
            }
            kunciInBinary = KonversiData.paddingInRightBinaryString(kunciInBinary, NK_ON_BYTE);
            this.textChiper.appendText(
                    "\n\nChipertext in binary: \n" + msgInBinary + "\n" +
                    "Length MsgInBinary: " + msgInBinary.length()
                    + "\nUkuran rgbData: " + this.rgbDataOfImage.length
            );
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
            this.textChiper.appendText(
                    "\nKunci in biner: " + kunciInBinary
                    + "\nUkuran kunci: " + kunciInBinary.length()
                    + "\nNumberOfMessage: " + this.nOfChiperLenOnByte
                    + "\nXNM: " + this.xnm
                    + "\nMsgLengthInfoInBinary: " + msgLengthInfoInBinary
                    + "\nGemodNumber: " + this.gemodNumber
            );
        }
    }

    @FXML
    private void handleBtnMainMenu(ActionEvent event) {
        Parent p = null;
        boolean loadSukses = true;

        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("main/maindoc.fxml"));
            if (p == null) {
                loadSukses = false;
            }
        } catch (IOException ex) {
            loadSukses = false;
        }

        if (!loadSukses) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Gagal membuka scene maindoc fxml",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi");
            alert.setHeaderText("File tidak ditemukan");
            alert.show();
        }
        else {
            Main.mainStage.setTitle("Aplikasi Steganografi");
            Main.mainStage.setScene(new Scene(p));
        }
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
                /*if (!this.enkripProsesSukses) {
                    btnBrowseCoverImg.setDisable(true);
                    break;
                }*/
            }

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
            textChiper.setText(hasilEnkrip);
            btnBrowseCoverImg.setDisable(false);
        }
        return this.enkripProsesSukses;

    }

    public void displayMetadata(Node root, String t) {
        displayMetadata(root, 0, t);
    }

    void indent(int level) {
        for (int i = 0; i < level; i++) {
            imgProperty += "  ";
        }
    }

    void displayMetadata(Node node, int level, String t) {
        indent(level); // emit open tag
        imgProperty += "<" + node.getNodeName();
        NamedNodeMap map = node.getAttributes();
        if (map != null) { // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                imgProperty += " " + attr.getNodeName() + "=" + attr.getNodeValue() + "";
            }
        }

        Node child = node.getFirstChild();
        if (child != null) {
            imgProperty += ">";
            while (child != null) { // emit child tags recursively
                displayMetadata(child, level + 1, imgProperty);
                child = child.getNextSibling();
            }
            indent(level); // emit close tag
            imgProperty += "</" + node.getNodeName() + ">";
        } else {
            imgProperty += "/>";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //imagePrev = new Image(getClass().getResourceAsStream("rnd_br_prev32.png"));
        //btnMainMenu.setGraphic(new ImageView(imagePrev));
        try {
            imgViewCover.setImage(new Image(MainController.resouresDir.toURI().toURL().toString() + "image-invalid.png"));
            imgViewStego.setImage(new Image(MainController.resouresDir.toURI().toURL().toString() + "image-invalid.png"));
        } catch (MalformedURLException e) {
            AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                    "Kesalahan Akeses File",
                    "File tidak terdeteksi",
                    ButtonType.OK);
        }


        Platform.isSupported(ConditionalFeature.INPUT_METHOD);
        textInputMessage.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() > 0) {
                    handleTextInputMessage();
                }
            }
        });
        textInputMessage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue) {
                    handleTextInputMessage();
                }
            }
        });
    }
}
