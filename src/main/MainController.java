package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by hendro.sinaga on 08-Jun-16.
 */
public class MainController implements Initializable {
    public static File resouresDir;
    @FXML
    private void handleExit(ActionEvent event) {
        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        //ButtonType<>
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Apakah Anda yakin akan menutup aplikasi?",
                buttonTypeYes,
                buttonTypeNo);
        alert.setTitle("Informasi Aplikasi");
        alert.setHeaderText("Tutup Aplikasi?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().equals(buttonTypeYes)) {
            Main.mainStage.close();
        } else {
            alert.close();
        }
    }

    @FXML
    private void handleShowEmbeddingMessage(ActionEvent event) {
        Parent p = null;
        boolean loadSukses = true;
        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("stegano/embeddingdoc.fxml"));
        } catch (IOException ex) {
            loadSukses = false;
        }
        finally {
            if (!loadSukses || p == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Gagal membuka scene embedding fxml",
                        ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("File tidak ditemukan");
                alert.show();
            } else {
                Main.mainStage.setTitle("Aplikasi Steganografi: Embedding Message");
                Main.mainStage.setScene(new Scene(p, 1145, 640));
                Main.mainStage.centerOnScreen();
            }
        }

    }

    @FXML
    private void handleShowAESEncrypt(ActionEvent event) {
        Parent p = null;
        boolean loadSukses = true;
        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("aes/encryptdoc.fxml"));
        } catch (IOException ex) {
            loadSukses = false;
        }
        finally {
            if (!loadSukses || p == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Gagal membuka scene encryption fxml",
                        ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("File tidak ditemukan");
                alert.show();
            } else {
                Main.mainStage.setTitle("Aplikasi Steganografi: AES256 Encryption");
                Main.mainStage.setScene(new Scene(p, 738, 595));
                Main.mainStage.centerOnScreen();
            }
        }
    }

    @FXML
    private void handleShowKriptoEncryptDecrypt(ActionEvent event) {
        Parent p = null;
        boolean loadSukses = true;
        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("aes/kriptodoc.fxml"));
        } catch (IOException ex) {
            loadSukses = false;
        }
        finally {
            if (!loadSukses || p == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Gagal membuka scene kriptodoc fxml",
                        ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("File tidak ditemukan");
                alert.show();
            } else {
                Main.mainStage.setTitle("Aplikasi Steganografi: KriptoAES256 Encrypt-Decrypt");
                Main.mainStage.setScene(new Scene(p, 1150, 669));
                Main.mainStage.centerOnScreen();
            }
        }
    }

    @FXML
    public void handleShowViewImage(ActionEvent event) {
        Parent p = null;
        boolean loadSukses = true;
        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("main/ujigambardoc.fxml"));
        } catch (IOException ex) {
            loadSukses = false;
        }
        finally {
            if (!loadSukses || p == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Gagal membuka scene ujigambardoc fxml",
                        ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("File tidak ditemukan");
                alert.show();
            } else {
                Main.mainStage.setTitle("Aplikasi Steganografi: UjiGambar");
                Main.mainStage.setScene(new Scene(p, 795, 615));
                Main.mainStage.centerOnScreen();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String info = "";
        //File dir = new File("resources");
        resouresDir = new File("resources/");
        if (resouresDir.exists()) {
            info = "resources dikenal";
        } else {
            info = "resources tidak dikenal";
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    info,
                    ButtonType.OK);
            alert.setTitle("INfo lokasi : ");
            alert.show();
        }

    }
}
