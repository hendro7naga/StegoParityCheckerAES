package main;

import interfaces.OpenScene;
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
import kelas.AlertInfo;

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
public class MainController implements Initializable, OpenScene {
    public static final ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    public static final ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

    public static File resouresDir;
    public static boolean dbStatus = false;
    public static AppControll appControll = null;
    @FXML
    private void handleExit(ActionEvent event) {
        Optional<ButtonType> result = AlertInfo.showConfirmMessage(
                "Informasi Aplikasi",
                "Tutup Aplikasi?",
                "Apakah Anda yakin ingin menutup aplikasi?"
        );
        if (result.get().equals(buttonTypeYes)) {
            Main.mainStage.close();
        } else {
            //alert.close();
        }
    }

    @FXML
    private void handleShowEmbeddingMessage(ActionEvent event) {
        try {
            open("embedding", 1152.6, 660.5);
        } catch (Exception ex) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Membuka Scene",
                    "Terjadi kesalahan: " + ex.getMessage(),
                    ButtonType.OK
            );
        }
    }

    @FXML
    private void handleShowExtractionMessage(ActionEvent actionEvent) {
        try {
            open("extraction", 896.5, 665.5);
        } catch (Exception ex) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Membuka Scene",
                    "Terjadi kesalahan: " + ex.getMessage(),
                    ButtonType.OK
            );
        }
    }

    @FXML
    private void handleShowImperceptibilityScene(ActionEvent actionEvent) {
        try {
            open("imperceptibility", 785.5, 645.6);
        } catch (Exception ex) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Membuka Scene",
                    "Terjadi kesalahan: " + ex.getMessage(),
                    ButtonType.OK
            );
        }
    }

    @FXML
    private void handleShowRobustnessScene(ActionEvent actionEvent) {
        try {
            open("robustness", 1068.5, 645.5);
        } catch (Exception ex) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Load Robustness scene",
                    "Gagal membuka Robustness scene",
                    ButtonType.OK
            );
        }
    }

    @FXML void handleShowTestResult (ActionEvent actionEvent) {
        boolean sceneLoaded = true;
        try {
            open("result", 998.5, 756.5);
        } catch (Exception e) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Load Scene",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
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
    public void handleShowViewImage(ActionEvent actionEvent) {
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

    @FXML void handleShowAbout (ActionEvent actionEvent) {
        try {
            open("about", 763, 627);
        } catch (Exception e) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Open Scene",
                    "Terjadi kesalahan (Error) : " + e.getMessage(),
                    ButtonType.OK
            );
        }
    }

    public static void initApp () {
        if (MainController.appControll == null) {
            MainController.appControll = AppControll.getInstance();
        }
        if (!MainController.appControll.getInitializeStatus()) {
            try {
                if (MainController.appControll.init() == 1) {
                    MainController.appControll.sqLiteDB.createConnection();
                    MainController.appControll.sqLiteDB.closeConnection();
                    //MainController.appControll.sqLiteDB.deleteTableData(AppControll.TABLE_STEGANO_NOISE_NAME);
                }
            } catch (Exception e) {
                AlertInfo.showAlertErrorMessage(
                        "Informasi Aplikasi",
                        "Init App",
                        "Gagal inisialisasi App (Error) : " + e.getMessage(),
                        ButtonType.OK
                );
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String info = "";
        if (Main.firstRun) {
            MainController.appControll = AppControll.getInstance();
            MainController.initApp();
            //File dir = new File("resources");
            resouresDir = new File("resources/");
            if (resouresDir.exists()) {
                info = "resources dikenal";
            } else {
                info = "resources tidak dikenal";
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        info,
                        ButtonType.OK);
                alert.setTitle("Info lokasi : ");
                alert.show();
            }
            Main.firstRun = false;
        }

    }
}
