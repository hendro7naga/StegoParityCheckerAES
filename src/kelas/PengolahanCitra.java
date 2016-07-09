package kelas;

import javafx.scene.control.ButtonType;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by hendro.sinaga on 09-Jul-16.
 */
public class PengolahanCitra {
    public static BufferedImage addSaltAndPepperNoise(BufferedImage imageOri, int noiseProbabilitas) {
        BufferedImage temp = imageOri;
        Random rnd = new Random();
        int width = imageOri.getWidth();
        int height = imageOri.getHeight();
        int prob = (int) (width * height * noiseProbabilitas * 0.01);
        for (int i = 0; i < prob; i += 1) {
            int x1 = rnd.nextInt(imageOri.getWidth() - 1);
            int y1 = rnd.nextInt(imageOri.getHeight() - 1);
            int random = rnd.nextInt(10) + 1;
            if (random <= 10) {
                temp.setRGB(x1, y1, ((255 << 24) | (0 << 16) | (0 << 8) | 0));
            }
            else {
                temp.setRGB(x1, y1, ((255 << 24) | (255 << 16) | (255 << 8) | 255));
            }
        }
        return  temp;
    }

    public static double calculateMSE(BufferedImage ori, BufferedImage stego) {
        double temp = Double.MIN_VALUE;
        double mseR = 0, mseG = 0, mseB = 0;
        try {
            for (int x = 0; x < ori.getWidth(); x += 1) {
                for (int y = 0; y < ori.getHeight(); y += 1) {
                    int rgb = ori.getRGB(x, y);
                    int r = (rgb>>16)&0x000000FF;
                    int g = (rgb>>8)&0x000000FF;
                    int b = (rgb)&0x000000FF;
                    int rgbStego = stego.getRGB(x, y);
                    int rs = (rgbStego>>16)&0x000000FF;
                    int gs = (rgbStego>>8)&0x000000FF;
                    int bs = (rgbStego)&0x000000FF;
                    mseR += Math.pow((r - rs), 2);
                    mseG += Math.pow((g - gs), 2);
                    mseB += Math.pow((b - bs), 2);
                }
            }
            mseR = mseR / (ori.getWidth() * ori.getHeight());
            mseG = mseG / (ori.getWidth() * ori.getHeight());
            mseB = mseB / (ori.getWidth() * ori.getHeight());
            temp = (mseR + mseG + mseB) / 3;
        }
        catch (ArrayIndexOutOfBoundsException arrindexout) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "Array Index Out Of Bound",
                    "Array Index tidak valid",
                    ButtonType.OK
            );
            temp = Double.MIN_VALUE;
        }
        catch (Exception e) {
            AlertInfo.showAlertErrorMessage(
                    "Informasi Aplikasi",
                    "MSE - PSNR",
                    "Terjadi kesalahan ketika menghitung nilai MSE - PSNR",
                    ButtonType.OK
            );
            temp = Double.MIN_VALUE;
        }
        return temp;
    }

    public static double calculatePSNR(double mse) {
        double temp = 10 * Math.log10( Math.pow(255, 2) / mse );
        return temp;
    }

}
