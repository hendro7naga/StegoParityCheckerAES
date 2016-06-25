package main;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import kelas.AlertInfo;

import javax.imageio.ImageIO;
import javax.imageio.plugins.bmp.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by hendro-sinaga on 23/06/16.
 */
public class UjiGambar {
    BufferedImage bufferedImageOut = null;
    BufferedImage bufferedImage;
    Image imagePrev;
    String imgPath = "";
    Alert alert;
    @FXML
    Button btnBrowseImage, btnSaveImage;
    @FXML
    ImageView imgviewOri, imgviewWrite;
    @FXML
    TextArea textareaInfoImgOri;

    @FXML
    public void handleimgviewOri(ActionEvent event) {
        String teks = "";
        Image image = null;
        Graphics graphics;
        Color color;
        PixelReader pixelReader;
        FileChooser fc = new FileChooser();
        fc.setTitle("Buka Berkas Gambar");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));

        File fio = fc.showOpenDialog(null);
        if (fio != null) {
            try {
                imgPath = fio.toURI().toURL().toString();
                image = new Image(imgPath);

                imgviewOri.setImage(image);
                pixelReader = image.getPixelReader();
                color = pixelReader.getColor(0,0);

                bufferedImage = SwingFXUtils.fromFXImage(image, null);
                graphics = bufferedImage.getGraphics();
                int blue = graphics.getColor().getBlue();


                teks += "Nama File: " + fio.getName() + "\n"
                        + "Lebar: " + bufferedImage.getWidth() + "\n"
                        + "Tinggi: " + bufferedImage.getHeight() + "\n";
                teks += "\n===========================\n";
                teks += "Info Piksel:";
                teks += "\n===========================\n";
                //teks += "Pixel 0,0: " + r + "," + g + "," + b + "\n";

                for (int x = 0; x < 20; x += 1) {
                    for (int y = 0, lines = 0; y < 20; y += 1) {
                        int argb = bufferedImage.getRGB(x,y);
                        //int argb = pixelReader.getArgb(x,y);
                        int r = (argb)&0xFF;
                        int g = (argb>>8)&0xFF;
                        int b = (argb>>16)&0xFF;
                        //int a = (argb>>24)&0xFF;
                        teks += "[" + x + "," + y +"]: " + r + "," + g + "," + b + "  ";
                        if (lines == 5) {
                            lines = 0;
                            teks += "\n";
                        } else {
                            lines += 1;
                        }
                    }
                    teks += "\n";
                }

                if (bufferedImageOut == null) {
                    imgviewWrite.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
                    bufferedImageOut = bufferedImage;
                } else {
                    imgviewWrite.setImage(SwingFXUtils.toFXImage(bufferedImageOut, null));
                }

                textareaInfoImgOri.setText(teks);
            } catch (MalformedURLException mue) {
                alert = new Alert(Alert.AlertType.INFORMATION,
                        "Gagal mengupload gambar ke aplikasi",
                        ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("Terjadi Exception PATH File");
                alert.show();
            } catch (Exception exc) {
                alert = new Alert(Alert.AlertType.INFORMATION,
                        "Terjadi kesalahan set Image",
                        ButtonType.OK);
                alert.setTitle("Informasi Aplikasi");
                alert.setHeaderText("Terjadi Exception PATH File");
                alert.show();
            }
        }
    }

    @FXML
    public void handleBtnSaveImage(ActionEvent event) {
        //Image img = SwingFXUtils.toFXImage(bufferedImage, null);
        FileChooser fcs = new FileChooser();
        fcs.setTitle("Buka Berkas Gambar");
        fcs.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Image", "*.bmp"));

        String teks = "\n\nInfo Ukuran BufferedImage sekarang: " + bufferedImage.getWidth() + "," + bufferedImage.getHeight();
        teks += "\n";
        textareaInfoImgOri.appendText(teks);

        File fis = fcs.showSaveDialog(null);
        if (fis != null) {
            String selected_desc = fcs.getSelectedExtensionFilter().getDescription();
            String extension = "bmp";
            boolean sukses = true;
            if (fis.getName().endsWith(".bmp"))
                extension = "bmp";
            else if (fis.getName().endsWith(".jpg"))
                extension = "jpg";
            else
                extension = "bmp";
            try {
                boolean res = ImageIO.write(bufferedImage, extension, fis);
            } catch (Exception e) {
                AlertInfo.showAlertErrorMessage("Informasi Kesalahan",
                        "Penyimpanan Gambar Gagal",
                        "Gagal menyimpan gambar ke direktori",
                        ButtonType.OK);
            }
        }

    }

}
