package aes;

import com.sun.xml.internal.fastinfoset.util.CharArray;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by hendro.sinaga on 09-Jun-16.
 */
public class EncryptController implements Initializable {
    private Alert alert;
    @FXML
    Label lblInfoChiper;
    @FXML
    TextField itextPass;
    @FXML
    TextArea txtareaChiper;

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
        List<ArrayList<Integer>> listKunci = new ArrayList<>();
        int[][] arrPassInt = new int[8][4];
        int indeks = 0;
        String teks = "";

        char[] arrPassChar = pass.toCharArray();

        try {
            for (int x = 0; x < 8; x += 1) {
                ArrayList<Integer> sub = new ArrayList<>();
                for (int i = 0; i < 4; i += 1) {
                    if (indeks < arrPassChar.length) {
                        sub.add((int)arrPassChar[indeks]);
                        indeks += 1;
                    } else {
                        sub.add(0);
                    }
                }
                listKunci.add(sub);
            }

            for (int x = 0; x < listKunci.size(); x += 1) {
                for (int y = 0; y < listKunci.get(x).size(); y += 1) {
                    teks += "Ascii (" + (char)listKunci.get(x).get(y).intValue() + ") => ";
                    teks += listKunci.get(x).get(y) + " => " + Integer.toHexString(listKunci.get(x).get(y));
                    teks += "\t";
                }
                teks += " \n";
            }

            txtareaChiper.setText(teks);
            lblInfoChiper.setText("Panjang Kunci (Pass): " + pass.length() + " Length list: " + listKunci.size());
        }
        catch (Exception exceptonc) {
            alert = new Alert(Alert.AlertType.ERROR,
                    "Terjadi exception pada saat konversi string-char-integer",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi");
            alert.setHeaderText("Proses Konversi Char");
            alert.show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
