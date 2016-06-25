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
import kelas.KonversiData;
/**
 * Created by hendro.sinaga on 09-Jun-16.
 */
public class EncryptController implements Initializable {
    public static int[] encryptResult;
    public static String encryptResultInString = "";
    public static  int arrPesan[], arrKeys[];
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
            //kedua
            EncryptController.arrPesan = KonversiData.stringToIntArr1D(pesan);
            EncryptController.arrKeys = KonversiData.stringToIntArr1D(pass);
            teks += "Pesan Asli : \n";
            for (int i = 0; i < arrPesan.length; i += 1) {
                teks += "" + Integer.toHexString(arrPesan[i]) + " ";
            }
            HAES256 haes256 = HAES256.getInstance();
            haes256.ReInitProps();
            if (haes256.encryptDataInteger(EncryptController.arrPesan, EncryptController.arrKeys)) {
                int[][] arrKunciRes = haes256.getArrKunci();
                teks += "\n Kunci \n";
                for (int x = 0; x < arrKunciRes.length; x += 1) {
                    for (int y = 0; y < arrKunciRes[x].length; y += 1) {
                        teks += Integer.toHexString(arrKunciRes[x][y]) + "    ";
                    }
                    teks += "\n";
                }
                int[][] arrHasilEnkrip = haes256.getArrPesan();
                teks += "\n\nPesan: \n";
                for (int x = 0; x < arrHasilEnkrip.length; x += 1) {
                    for (int y = 0; y < arrHasilEnkrip[x].length; y += 1) {
                        teks += Integer.toHexString(arrHasilEnkrip[x][y]) + "    ";
                    }
                    teks += "\n";
                }
                int[] hasilEnkrip1D = KonversiData.arr2DToIntArr1D(haes256.getArrPesan());
                teks += "\n\nPesan 1 Dimensi: \n";
                for (int i = 0; i < hasilEnkrip1D.length; i += 1) {
                    teks += Integer.toHexString(hasilEnkrip1D[i]) + "  ";
                }
                haes256.ReInitProps();
                boolean dekripStatus = haes256.decryptDataInteger(hasilEnkrip1D, EncryptController.arrKeys);

                if (dekripStatus) {
                    String teksDekrip = "Dekrip:\n";
                    int[] hasilDekrip1D = KonversiData.arr2DToIntArr1D(haes256.getArrChiper());
                    for (int i = 0; i < hasilDekrip1D.length; i += 1) {
                        teksDekrip += Character.toString((char)hasilDekrip1D[i]);
                    }
                    teksChiper.setText(teksDekrip);
                } else {
                    teksChiper.setText("Dekripsi Gagal...");
                }

            } else {
                teks += "Enkripsi Gagal....";
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
