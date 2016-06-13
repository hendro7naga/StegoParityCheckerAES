package aes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import kripto.AESH;
import kripto.HAES256;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by hendro.sinaga on 09-Jun-16.
 */
public class EncryptController implements Initializable {
    public static int[] encryptResult;
    public static String encryptResultInString = "";
    private Alert alert;
    AESH aesInstance = AESH.getInstance();

    private final int MAX_CHAR_PASS = 32;
    @FXML
    Label lblInfoChiper;
    @FXML
    TextField itextPass;
    @FXML
    TextArea txtareaChiper, teksChiper;

    @FXML
    private void handlebtnEncrypt(ActionEvent event)
    {

        if (itextPass.getText().length() < 2) {
            alert = new Alert(Alert.AlertType.WARNING,
                    "Password tidak boleh kosong.\nMinimum 3 karakter",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi");
            alert.setHeaderText("Kesalahan: Input Password");
            alert.show();
        } else {
            AESKeyExpansion(itextPass.getText());
        }

    }

    @FXML
    private void handleInputPass(KeyEvent key) {
        if (itextPass.getText().length() >= MAX_CHAR_PASS) {
            alert = new Alert(Alert.AlertType.ERROR,
                    "Maksimal pass 32 Karakter",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi");
            alert.setHeaderText("Password Input");
            alert.show();
            itextPass.setText(itextPass.getText().substring(0, MAX_CHAR_PASS));
            itextPass.positionCaret(MAX_CHAR_PASS);
        }

    }

    @FXML
    private void handleBtnMainMenu(ActionEvent event) {
        Parent p = null;
        boolean sukses = true;

        try {
            p = FXMLLoader.load(this.getClass().getClassLoader().getResource("main/maindoc.fxml"));
            if (p == null) {
                sukses = false;
            }
        } catch (IOException ex) {
            sukses = false;
        }

        if (!sukses) {
            alert = new Alert(Alert.AlertType.ERROR,
                    "Gagal membuka scene maindoc fxml",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi: ");
            alert.setHeaderText("File tidak ditemukan");
            alert.show();
        }
        else {
            Main.mainStage.setTitle("Aplikasi Steganografi");
            Main.mainStage.setScene(new Scene(p));
        }
    }

    private void AESKeyExpansion(String pass)
    {
        List<ArrayList<Integer>> listKunci;
        List<ArrayList<Integer>> listPesan;
        int[][] arrPassInt = new int[8][4];
        int indeks = 0;
        String teks = "\n";
        String pesan = "kriptografiaesha";

        char[] arrPassChar = pass.toCharArray();

        try {
            boolean statusEnkripsi = HAES256.encrypt(pesan, pass);
            if (statusEnkripsi) {
                //kedua
                int[][] arrKunci = HAES256.getArrKunci();
                teks += "\n Kunci \n";
                for (int x = 0; x < arrKunci.length; x += 1) {
                    for (int y = 0; y < arrKunci[x].length; y += 1) {
                        teks += Integer.toHexString(arrKunci[x][y]) + "    ";
                    }
                    teks += "\n";
                }
                teks += "\n \n ================Pesan============== \n";
                int[][] arrPesan = HAES256.getArrPesan();
                encryptResult = HAES256.getEncryptResult1D();
                for (int x = 0; x < arrPesan.length; x += 1) {
                    for (int y = 0; y < arrPesan[x].length; y += 1) {
                        teks += Integer.toHexString(arrPesan[x][y]) + "   ";
                    }
                    teks += "\n";
                }
                teks += "\n Result in 1 arr: \n";
                for (int i = 0; i < 4; i += 1) {
                    //teks += Integer.toHexString(encryptResult[i]);
                    for (int j = 0; j < 4; j += 1) {
                        teks += Integer.toHexString(arrPesan[j][i]);
                    }
                }
                teks += "\n \n Result in char: \n";
                for (int i = 0; i < 4; i += 1) {
                    //teks += Integer.toHexString(encryptResult[i]);
                    for (int j = 0; j < 4; j += 1) {
                        teks += Character.toString((char)arrPesan[j][i]);
                        encryptResultInString += Character.toString((char)arrPesan[j][i]);
                    }
                }
                teks += "\n\nResult dalam string: " + encryptResultInString;

                teks += "\n\n Result in int: \n";
                char[] pesanChiper = encryptResultInString.toCharArray();
                for (int i = 0; i < pesanChiper.length; i += 1) {
                    teks += Integer.toHexString((int)pesanChiper[i]) + " ";
                }

                boolean resdecrypt = HAES256.decrypt(encryptResultInString, pass);
                if (resdecrypt) {
                    String tmpChiper = "\nProses Chiper Decrypt: \n";
                    int[][] arrChipers = HAES256.getArrChiper();
                    int[] chiper1D = HAES256.getDecryptResult1D();
                    for (int x = 0; x < arrChipers.length; x += 1) {
                        for (int y = 0; y < arrChipers[x].length; y += 1) {
                            tmpChiper += Integer.toHexString(arrChipers[x][y]) + "   ";
                        }
                        tmpChiper += "\n";
                    }

                    tmpChiper += "\nPesan Ori decrypt: ";

                    for (int x = 0; x < arrChipers.length; x += 1) {
                        for (int y = 0; y < arrChipers[x].length; y += 1) {
                            tmpChiper += Character.toString((char)arrChipers[y][x]);
                        }
                    }

                    teksChiper.setText(tmpChiper);
                }

            } else {

            }

            txtareaChiper.setText(teks);
            lblInfoChiper.setText("Panjang Kunci (Pass): " + pass.length() + " Length list: ");
        }
        catch (Exception exceptonc) {
            alert = new Alert(Alert.AlertType.ERROR,
                    "Terjadi exception pada saat konversi string-char-integer",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi");
            alert.setHeaderText("EncryptControll.Java : Proses Konversi Char");
            alert.show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
