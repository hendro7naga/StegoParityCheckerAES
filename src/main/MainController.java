package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by hendro.sinaga on 08-Jun-16.
 */
public class MainController {
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
}
