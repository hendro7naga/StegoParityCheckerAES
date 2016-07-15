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
import kelas.PengolahanCitra;

import javax.imageio.ImageIO;
import javax.imageio.plugins.bmp.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by hendro-sinaga on 23/06/16.
 */
public class UjiGambar {
    Image image = null;
    BufferedImage bufferedImageNoise = null;
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

                this.bufferedImage = SwingFXUtils.fromFXImage(image, null);
                graphics = bufferedImage.getGraphics();
                int blue = graphics.getColor().getBlue();


                teks += "Nama File: " + fio.getName() + "\n"
                        + "Lebar: " + bufferedImage.getWidth() + "\n"
                        + "Tinggi: " + bufferedImage.getHeight() + "\n";
                teks += "\n===========================\n";
                teks += "Info Piksel:";
                teks += "\n===========================\n";
                //teks += "Pixel 0,0: " + r + "," + g + "," + b + "\n";
                teks += "Pixel Gambar: \n";
                int tambahNoise = 10, tmpp;
                double x0, x1, x2, xn;
//                for (int x = 0; x < 35; x += 1) {
//                    for (int y = 0, lines = 0; y < 35; y += 1) {
//                        int argb = bufferedImage.getRGB(x,y);
                        /*int r = (argb)&0xFF;
                        int g = (argb>>8)&0xFF;
                        int b = (argb>>16)&0xFF;
                        int a = (argb>>24)&0xFF;*/
//                        int a = (argb>>24)&0x000000FF;
//                        int r = (argb>>16)&0x000000FF;
//                        int g = (argb>>8)&0x000000FF;
//                        int b = (argb)&0x000000FF;
//                        teks += "[" + x + "," + y +"]: " + r + "," + g + "," + b + "  " + a + "\n";
//                        int argb2 = (a << 24) | (r << 16) | (g << 8) | b;
//                        int argban = ((a << 24) | (255 << 16) | (255 << 8) | 255);
//
//                        do {
//                            x1 = Math.random();
//                        } while (x1 == 0);
//                        x2 = Math.random();
//                        x0 = Math.sqrt(-2 * Math.log(x1) * Math.cos(2 * Math.PI * x2));
//                        xn = 1 + Math.sqrt(0.1) * x;
//                        tmpp = argban + (int)xn;
//
//                        if (tmpp < 0) {
//                            bufferedImage.setRGB(x, y, ((a << 24) | (0 << 16) | (0 << 8) | 0));
//                        }
//                        else if (tmpp > 255) {
//                            bufferedImage.setRGB(x, y, ((a << 24) | (255 << 16) | (255 << 8) | 255));
//                        }
//                        else {
//                            bufferedImage.setRGB(x, y, ((a << 24) | tmpp));
//                        }

                        /*if (tambahNoise > 0) {
                            bufferedImage.setRGB((y * 2) % 19, tambahNoise, argban);
                            tambahNoise -= 1;
                        }*/
                        /*int r2 = (argb2)&0xFF;
                        int g2 = (argb2>>8)&0xFF;
                        int b2 = (argb2>>16)&0xFF;
                        int a2 = (argb2>>24)&0xFF;*/
//                        int a2 = (argb2>>24)&0x000000FF;
//                        int r2 = (argb2>>16)&0x000000FF;
//                        int g2 = (argb2>>8)&0x000000FF;
//                        int b2 = (argb2)&0x000000FF;
//                        teks += "[" + x + "," + y +"]: " + r2 + "," + g2 + "," + b2 + "  " + a2 + "\n";
//                        if (lines == 5) {
//                            lines = 0;
//                            teks += "\n";
//                        } else {
//                            lines += 1;
//                        }
//                    }
//                    teks += "\n";
//                }

                /*if (bufferedImageOut == null) {
                    imgviewWrite.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
                    bufferedImageOut = bufferedImage;
                } else {
                    imgviewWrite.setImage(SwingFXUtils.toFXImage(bufferedImageOut, null));
                }*/
                //imgviewWrite.setImage(SwingFXUtils.toFXImage(bufferedImage, null));

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
            addGaussianNoise();
        }
    }

    private void addGaussianNoise() {
        Double presentaseNoise = new Double(50);
        Random gaussianDistribution = new Random();
        Double distribution = gaussianDistribution.nextGaussian();

        int acakX = (this.bufferedImage.getWidth() * 30) / 100;
        int acakY = (this.bufferedImage.getHeight() * 30) / 100;
        java.util.List<Integer> dataNoise = new ArrayList<>();
        try {
            /*for (int x = 0; x < this.bufferedImage.getWidth(); x += 1) {
                for (int y = 0; y < this.bufferedImage.getHeight(); y += 1) {
                }
            }*/
            this.textareaInfoImgOri.appendText("\n\nNilai noise: \n");
            int lines = 8;
//            for (int x = 0; x < this.bufferedImage.getWidth(); x += 1) {
//                for (int y = 0; y < this.bufferedImage.getHeight(); y += 1) {
//                    int rgb = this.bufferedImage.getRGB(x, y);
//                    int r = (rgb>>16)&0x000000FF;
//                    int g = (rgb>>8)&0x000000FF;
//                    int b = (rgb)&0x000000FF;
//                    int gray = (r + g + b) / 3;
//                    Double getNoise = presentaseNoise * distribution;
//                    int withNoise = Math.round((255 + r + g + b) + getNoise.floatValue());
//                    r = (r + Math.round(getNoise) > 255) ? 255 : (r + Math.round(getNoise) < 0) ? 0 : (int)(r + Math.round(getNoise));
//                    g = (g + Math.round(getNoise) > 255) ? 255 : (g + Math.round(getNoise) < 0) ? 0 : (int)(g + Math.round(getNoise));
//                    b = (b + Math.round(getNoise) > 255) ? 255 : (b + Math.round(getNoise) < 0) ? 0 : (int)(b + Math.round(getNoise));
//                    //int withNoise = Math.round(gray + getNoise.floatValue());
//                    this.bufferedImage.setRGB(
//                            x,
//                            y,
//                            ((255 << 24) | (r << 16) | (g << 8) | b)
//                    );
//                    /*this.bufferedImage.setRGB(x,
//                                              y,
//                                              withNoise
//                    );*/
//                    dataNoise.add(withNoise);
//                }
//            }
            /*for (int i = 0; i < dataNoise.size(); i += 1) {
                this.textareaInfoImgOri.appendText("" + dataNoise.get(i) + " ");
                lines -= 1;
                if (lines == 1) {
                    this.textareaInfoImgOri.appendText("\n");
                    lines = 8;
                }
            }*/
            //imgviewWrite.setImage(SwingFXUtils.toFXImage(this.bufferedImage, null));
            this.bufferedImageNoise = PengolahanCitra.addSaltAndPepperNoise(this.bufferedImage, 5.5);
            imgviewWrite.setImage(SwingFXUtils.toFXImage(this.bufferedImageNoise, null));
            AlertInfo.showAlertInfoMessage(
                    "Informasi Aplikasi",
                    "Penambahan Gaussian Noise",
                    "Penambahan noise berhasil dilakukan",
                    ButtonType.OK
            );
        } catch (Exception gaussianException) {
            alert = new Alert(Alert.AlertType.INFORMATION,
                    "Terjadi kesalahan Penambahan gaussian noise",
                    ButtonType.OK);
            alert.setTitle("Informasi Aplikasi");
            alert.setHeaderText("Terjadi Exception PATH File");
            alert.show();
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
                boolean res = ImageIO.write(this.bufferedImage, extension, fis);
                AlertInfo.showAlertErrorMessage("Informasi Kesalahan",
                        "Penyimpanan Gambar Berhasil",
                        "Berhasil menyimpan gambar ke direktori",
                        ButtonType.OK);
            } catch (Exception e) {
                AlertInfo.showAlertErrorMessage("Informasi Kesalahan",
                        "Penyimpanan Gambar Gagal",
                        "Gagal menyimpan gambar ke direktori",
                        ButtonType.OK);
            }
        }

    }

}
