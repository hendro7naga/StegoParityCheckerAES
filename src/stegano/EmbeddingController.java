package stegano;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * Created by hendro.sinaga on 08-Jun-16.
 */
public class EmbeddingController implements Initializable {
    Throwable ByteNull = new Throwable();

    @FXML
    TextArea textInputMessage;
    @FXML
    TextArea textChiper;
    @FXML
    Text txtInfoMessage;
    @FXML
    PasswordField txtInputPass;
    @FXML
    Button btnEncrypt;

    @FXML
    private void handleBrowseFileText(ActionEvent event) {
        Alert alert;
        boolean sukses = true;
        byte[] byteTeks = null;
        String textFile = "";
        FileChooser fc = new FileChooser();
        fc.setTitle("Buka Berkas Teks");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Berkas Teks", "*.txt"));

        File fileTeks = fc.showOpenDialog(null);

        if (fileTeks != null) {
            try {
                byteTeks = Files.readAllBytes(Paths.get(fileTeks.getPath()));
                textInputMessage.setText(new String(byteTeks));
            } catch (IOException ex) {
                alert = new Alert(Alert.AlertType.ERROR,
                                "Berkas tidak dapat diakses dan dibaca.\nDetail Error: " + ex.getMessage(),
                                ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("Berkas tidak ditemukan");
                alert.show();
            } catch (Exception e) {
               alert = new Alert(Alert.AlertType.ERROR,
                       "Terjadi kesalahan pembacaan file",
                       ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("Terjadi Exception");
                alert.show();
            }
        }

    }

    private void handleTextInputMessage() {
        int len = textInputMessage.getText().length();
        txtInfoMessage.setText("Text Length : " + len + " Byte");
    }

    @FXML
    private void handleInputPassword(KeyEvent handler) {
        if (txtInputPass.getText().length() > 5) {
            btnEncrypt.setDisable(false);
        } else {
            btnEncrypt.setDisable(true);
        }
        textChiper.setText(txtInputPass.getText());
    }

    @FXML
    private void handleBtnMainMenu(ActionEvent event) {
        Parent p = null;
        boolean loadSukses = true;

        try {
            p = FXMLLoader.load(getClass().getClassLoader().getResource("main/maindoc.fxml"));
            if (p == null) {
                loadSukses = false;
            }
        } catch (IOException ex) {
            loadSukses = false;
        }

        if (!loadSukses) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Gagal membuka scene maindoc fxml",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi");
            alert.setHeaderText("File tidak ditemukan");
            alert.show();
        }
        else {
            Main.mainStage.setTitle("Aplikasi Steganografi");
            Main.mainStage.setScene(new Scene(p));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.isSupported(ConditionalFeature.INPUT_METHOD);
        textInputMessage.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() > 0) {
                    handleTextInputMessage();
                }
            }
        });
        textInputMessage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue) {
                    handleTextInputMessage();
                }
            }
        });
    }
}
