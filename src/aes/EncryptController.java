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
    private Alert alert;
    AESH aesInstance = AESH.getInstance();

    private final int MAX_CHAR_PASS = 32;
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
            /*for (int x = 0; x < 8; x += 1) {
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
            }*/
            //boolean statusEnkripsi = aesInstance.encrypt(pesan, pass);
            boolean statusEnkripsi = HAES256.encrypt(pesan, pass);
            if (statusEnkripsi) {
                //pertama
                /*listKunci = (AESH.getListKunci());
                listPesan = (AESH.getListPesan());
                //int counterIndeks = 0;
                for (int x = 0; x < listKunci.size(); x += 1) {
                    teks += x + ") : ";
                    for (int y = 0; y < listKunci.get(x).size(); y += 1) {
                        //teks += listKunci.get(x).get(y) + " ";
                        teks += Integer.toHexString(listKunci.get(x).get(y)) + " ";
                    }
                    teks += " \n";
                }

                teks += "\n \n ============== Pesan(Hexa) ===============\n";
                for (int x = 0; x < listPesan.size(); x += 1) {
                    teks += x + ") : ";
                    for (int y = 0; y < listPesan.get(x).size(); y += 1) {
                        teks += Integer.toHexString(listPesan.get(x).get(y)) + " ";
                    }
                    teks += " \n \n";
                }
                */
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
                for (int x = 0; x < arrPesan.length; x += 1) {
                    for (int y = 0; y < arrPesan[x].length; y += 1) {
                        teks += Integer.toHexString(arrPesan[x][y]) + "   ";
                    }
                    teks += "\n";
                }
            } else {
                listKunci = new ArrayList<>();
            }

            /*for (int x = 0; x < listKunci.size(); x += 1) {
                for (int y = 0; y < listKunci.get(x).size(); y += 1) {
                    teks += ">" + x + ": Ascii (" + (char)listKunci.get(x).get(y).intValue() + ") => ";
                    teks += listKunci.get(x).get(y) + "  hex => " + Integer.toHexString(listKunci.get(x).get(y));
                    teks += "      ";
                }
                teks += " \n";
            }*/

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
