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
public class MainController implements Initializable {
    public static final ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    public static final ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

    public static File resouresDir;
    public static boolean dbStatus = false;
    public static AppControll appControll = null;
    @FXML
    private void handleExit(ActionEvent event) {
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
    private void handleShowExtractionMessage(ActionEvent actionEvent) {
        Parent p = null;
        boolean loadSukses = true;
        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("stegano/extractiondoc.fxml"));
        } catch (IOException ex) {
            loadSukses = false;
        }
        finally {
            if (!loadSukses || p == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Gagal membuka scene extraction fxml",
                        ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("File tidak ditemukan");
                alert.show();
            } else {
                Main.mainStage.setTitle("Aplikasi Steganografi: Extraction Message");
                Main.mainStage.setScene(new Scene(p, 870, 625));
                Main.mainStage.centerOnScreen();
            }
        }
    }

    @FXML
    private void handleShowImperceptibilityScene(ActionEvent actionEvent) {
        Parent p = null;
        boolean sceneLoaded = true;
        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("stegano/imperceptibilitydoc.fxml"));
        } catch (IOException ex) {
            sceneLoaded = false;
        }
        if (!sceneLoaded || p == null) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Load imperceptibility scene",
                    "Gagal membuka imperceptibility scene",
                    ButtonType.OK
            );
        }
        else {
            Main.mainStage.setTitle("Aplikasi Steganografi - Testing: Imperceptibility");
            Main.mainStage.setScene(new Scene(p, 775, 605));
            Main.mainStage.centerOnScreen();
        }
    }

    @FXML
    private void handleShowRobustnessScene(ActionEvent actionEvent) {
        Parent p = null;
        boolean sceneLoaded = true;
        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("stegano/robustnessdoc.fxml"));
        } catch (IOException ex) {
            sceneLoaded = false;
        }
        if (!sceneLoaded || p == null) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Load Robustness scene",
                    "Gagal membuka Robustness scene",
                    ButtonType.OK
            );
        }
        else {
            Main.mainStage.setTitle("Aplikasi Steganografi - Testing: Robustness");
            Main.mainStage.setScene(new Scene(p, 1064, 620));
            Main.mainStage.centerOnScreen();
        }
    }

    @FXML void handleShowTestResult (ActionEvent actionEvent) {
        Parent p = null;
        boolean sceneLoaded = true;
        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("stegano/testresultdoc.fxml"));
        } catch (IOException e) {
            sceneLoaded = false;
        }

        if (!sceneLoaded || p == null) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Load Scene",
                    "Gagal membuka TestResult scene",
                    ButtonType.OK
            );
        }
        else {
            Main.mainStage.setTitle("Aplikasi Steganografi - Testing: Result");
            Main.mainStage.setScene(new Scene(p, 995, 730));
            Main.mainStage.centerOnScreen();
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

    void initApp () {
        if (MainController.appControll == null) {
            MainController.appControll = AppControll.getInstance();
        }
        if (!MainController.appControll.getInitializeStatus()) {
            try {
                if (MainController.appControll.init() == 1) {
                    MainController.appControll.sqLiteDB.createConnection();
                    MainController.appControll.sqLiteDB.closeConnection();
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
        MainController.appControll = AppControll.getInstance();
        initApp();
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
