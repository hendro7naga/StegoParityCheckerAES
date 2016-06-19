package aes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kelas.AlertInfo;
import kelas.KonversiData;
import main.Main;
import kripto.HAES256;


/**
 * Created by hendro.sinaga on 18-Jun-16.
 */
public class KriptoController {
    public static char[] pesanArrChar;
    public static List<ArrayList<Integer>> dataPesan = new ArrayList<>();
    public static List<ArrayList<Integer>> dataEncrypt = null;
    public static List<ArrayList<Integer>> dataDecrypt = null;
    boolean enkripProsesSukses = false, dekripProsesSukses = false;
    Alert alert;
    @FXML
    TextArea txtareaPesan, txtareaChiper, txtareaHasilDecrypt;
    @FXML
    TextField txtInputKunci, txtInputKunciDecrypt;
    @FXML
    Button btnDecrypt;

    @FXML
    private void handleBtnEncrypt(ActionEvent event) {
        if (txtareaPesan.getText().length() < 6) {
            AlertInfo.showAlertInfoMessage("Informasi Aplikasi",
                    "Kesalahan Teks Original",
                    "Teks ori tidak boleh kosong. Minimal 6 karakter",
                    ButtonType.OK);
        }
        else if (txtInputKunci.getText().length() < 6) {
            AlertInfo.showAlertInfoMessage("Informasi Aplikasi",
                    "Kesalahan Kunci Encrypt",
                    "Kunci Encrypt tidak boleh kosong dan kurang dari 6 karakter",
                    ButtonType.OK);
        }
        else {
            KriptoController.dataPesan = new ArrayList<>();
            pesanArrChar = txtareaPesan.getText().toCharArray();
            char[] arrCharKunci = txtInputKunci.getText().toCharArray();
            int[] kunciEncrypt = new int[arrCharKunci.length];
            for (short i = 0; i < arrCharKunci.length; i += 1) {
                kunciEncrypt[i] = (int)arrCharKunci[i];
            }

            String potongTeks = "Teks dipotong";

            boolean prosesPotong = true;
            int indeksStart = 0;
            int panjangTeks = txtareaPesan.getText().length();
            while (prosesPotong) {
                ArrayList<Integer> tmpPesan = new ArrayList<>();
                String subs = "";
                if (indeksStart + 16 >= txtareaPesan.getText().length()) {
                    subs = txtareaPesan.getText(indeksStart, panjangTeks);
                    prosesPotong = false;
                }
                else {
                    subs = txtareaPesan.getText(indeksStart, indeksStart + 16);
                }
                char[] tmpChar = subs.toCharArray();
                for (int i = 0; i < tmpChar.length; i += 1) {
                    tmpPesan.add((int)tmpChar[i]);
                }
                dataPesan.add(tmpPesan);
                /*if (indeksStart < pesanArrChar.length) {
                    tmpPesan.add((int)pesanArrChar[indeksStart]);
                    indeksStart += 1;
                } else {
                    prosesPotong = false;
                }*/
                indeksStart += 16;
            }
            int len = 0;
            for (int i = 0; i < dataPesan.size(); i += 1) {
                len += dataPesan.get(i).size();
            }

            dataEncrypt = new ArrayList<>();
            HAES256.ReInitProps();
            for (int i = 0; i < dataPesan.size(); i += 1) {
                ArrayList<Integer> subData = new ArrayList<>();
                try {
                    if (HAES256.encryptDataInteger(
                            KonversiData.arraylist1DToArr1D(dataPesan.get(i)),
                            kunciEncrypt
                            )) {
                        /*subData.addAll(
                                subData.addAll(KonversiData.arr2DToIntArr1D(HAES256.getArrPesan()))
                        );*/
                        for (Integer val : KonversiData.arr2DToIntArr1D(HAES256.getArrPesan())) {
                            subData.add(val);
                        }
                        dataEncrypt.add(subData);
                    } else {
                        throw new Exception("Kesalahan proses enkripsi");
                    }
                    enkripProsesSukses = true;
                } catch (Exception ex) {
                    AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                            "Proses Enkripsi AES-256",
                            "Terjadi kesalahan proses: \n" + ex.getMessage(),
                            ButtonType.OK);
                }
                if (!enkripProsesSukses)
                    break;
            }

            if (!enkripProsesSukses) {
                btnDecrypt.setDisable(true);
            } else {
                String hasilEnkrip = "";
                for (ArrayList<Integer> val : dataEncrypt) {
                    for (int i = 0; i < val.size(); i += 1) {
                        hasilEnkrip += Character.toString((char)val.get(i).intValue());
                    }
                }
                txtareaChiper.setText(hasilEnkrip);
            }

            /*String isiTeksChiper = "";
            isiTeksChiper += "Panjang Teks Asli : " + panjangTeks + "\n";
            isiTeksChiper += "Size DataPesan : " + dataPesan.size() + "\n";
            isiTeksChiper += "Panjang DataPesan keseluruhan: " + len + "\n";

            String dataKonversi = "";
            for (ArrayList<Integer> data: dataPesan) {
                for (int i = 0; i < data.size(); i += 1) {
                    dataKonversi += Character.toString((char)data.get(i).intValue());
                }
            }

            isiTeksChiper += "\n\nIsi Pesan dari integer ke string: \n";
            isiTeksChiper += dataKonversi;*/

            //

            //txtareaChiper.setText(isiTeksChiper);

        }
    }

    @FXML
    private void handleBtnDecrypt(ActionEvent event) {
        if (txtInputKunciDecrypt.getText().length() < 6) {
            AlertInfo.showAlertWarningMessage("Informasi Aplikasi",
                    "Pasword Decrypt tidak valid",
                    "Kunci decrypt tidak boleh kosong dan kunci minimal 6 karakter.",
                    ButtonType.OK);
        } else {
            char[] arrCharKunci = txtInputKunciDecrypt.getText().toCharArray();
            int[] kunciDecrypt = new int[arrCharKunci.length];
            for (short i = 0; i < arrCharKunci.length; i += 1) {
                kunciDecrypt[i] = (int)arrCharKunci[i];
            }
            dataDecrypt = new ArrayList<>();
            HAES256.ReInitProps();
            for (int i = 0; i < dataEncrypt.size(); i += 1) {
                ArrayList<Integer> subData = new ArrayList<>();
                try {
                    if (HAES256.decryptDataInteger(
                            KonversiData.arraylist1DToArr1D(dataEncrypt.get(i)),
                            kunciDecrypt
                    )) {
                        for (Integer val : KonversiData.arr2DToIntArr1D(HAES256.getArrChiper())) {
                            subData.add(val);
                        }
                        dataDecrypt.add(subData);
                    } else {
                        throw new Exception("Kesalahan proses dekripsi");
                    }
                    dekripProsesSukses = true;
                } catch (Exception ex) {
                    AlertInfo.showAlertErrorMessage("Informasi Aplikasi",
                            "Proses Dekripsi AES-256",
                            "Terjadi kesalahan proses: \n" + ex.getMessage(),
                            ButtonType.OK);
                }
                if (!dekripProsesSukses)
                    break;
            }
            if (dekripProsesSukses) {
                String hasilDekrip = "";
                for (ArrayList<Integer> val : dataDecrypt) {
                    for (int i = 0; i < val.size(); i += 1) {
                        hasilDekrip += Character.toString((char)val.get(i).intValue());
                    }
                }
                //txtareaChiper.setText(hasilDekrip);
                txtareaHasilDecrypt.setText(hasilDekrip);
            }
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
            alert.setHeaderText("File Main tidak ditemukan");
            alert.show();
        }
        else {
            Main.mainStage.setTitle("Aplikasi Steganografi");
            Main.mainStage.setScene(new Scene(p));
        }
    }
}
