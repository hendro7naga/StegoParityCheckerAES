package main;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import kelas.AlertInfo;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by hendro.sinaga on 08-Jun-16.
 */
public class MainController implements Initializable {
    public static final ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    public static final ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

    public static File resouresDir;
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
        }
    }

    @FXML
    private void handleShowEmbeddingMessage(ActionEvent event) {
        try {
            //open("embedding", 1152.6, 660.5);
            Main.mainStage.setTitle("Embedding Messages");
            Main.mainStage.getScene().setRoot(
                    FXMLLoader.load(getClass().getClassLoader().getResource("stegano/embeddingdoc.fxml"))
            );
            Main.mainStage.setWidth(1052.6);
            Main.mainStage.setHeight(722.5);
            Main.mainStage.centerOnScreen();
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
            Main.mainStage.setTitle("Extraction Messages");
            Main.mainStage.getScene().setRoot(
                    FXMLLoader.load(getClass().getClassLoader().getResource("stegano/extractiondoc.fxml"))
            );
            Main.mainStage.setWidth(895.0);
            Main.mainStage.setHeight(668.0);
            Main.mainStage.centerOnScreen();
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
            System.gc();
            Main.mainStage.setTitle("Testing: Imperceptibility");
            Main.mainStage.getScene().setRoot(
                    FXMLLoader.load(getClass().getClassLoader().getResource("stegano/imperceptibilitydoc.fxml"))
            );
            Main.mainStage.setWidth(885.0);
            Main.mainStage.setHeight(635.0);
            Main.mainStage.centerOnScreen();
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
            System.gc();
            Main.mainStage.setTitle("Testing: Robustness");
            Main.mainStage.getScene().setRoot(
                    FXMLLoader.load(getClass().getClassLoader().getResource("stegano/robustnessdoc.fxml"))
            );
            Main.mainStage.setWidth(1142.0);
            Main.mainStage.setHeight(750.0);
            Main.mainStage.centerOnScreen();
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
        try {
            System.gc();
            Main.mainStage.setTitle("Result");
            Main.mainStage.getScene().setRoot(
                    FXMLLoader.load(getClass().getClassLoader().getResource("stegano/testresultdoc.fxml"))
            );
            Main.mainStage.setWidth(912.0);
            Main.mainStage.setHeight(746.0);
            Main.mainStage.centerOnScreen();
        } catch (Exception e) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Load Scene",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }
    }

    @FXML void handleShowAbout (ActionEvent actionEvent) {
        try {
            System.gc();
            Main.mainStage.setTitle("About");
            Main.mainStage.getScene().setRoot(
                    FXMLLoader.load(getClass().getClassLoader().getResource("about/aboutdoc.fxml"))
            );
            Main.mainStage.setWidth(780.0);
            Main.mainStage.setHeight(615.0);
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

    public static void initApp () {
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
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (Main.firstRun) {
                    MainController.appControll = AppControll.getInstance();
                    MainController.initApp();
                    resouresDir = new File("resources/");
                    if (!resouresDir.exists()) {
                        throw new Exception("Resources tidak dikenal");
                    }
                    Main.firstRun = false;
                }
                return null;
            }
        };
        try {
            new Thread(task).start();
        } catch (Exception e) {
            AlertInfo.showAlertWarningMessage(
                    "Informasi Aplikasi",
                    "Akses Resource",
                    "Terjadi kesalahan: " + e.getMessage(),
                    ButtonType.OK
            );
        }

    }
}
