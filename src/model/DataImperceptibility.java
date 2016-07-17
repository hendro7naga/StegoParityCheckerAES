package model;

import javafx.beans.property.*;

/**
 * Created by hendro.sinaga on 18-Jul-16.
 */
public class DataImperceptibility {
    private SimpleIntegerProperty nomor, msgLength;
    private SimpleStringProperty oriImage, stegoImage;
    private SimpleDoubleProperty mse, psnr;

    public DataImperceptibility (Integer nomor,
                                 String oriImage,
                                 String stegoImage,
                                 Integer msgLength,
                                 Double mse,
                                 Double psnr) {
        this.nomor = new SimpleIntegerProperty(nomor);
        this.oriImage = new SimpleStringProperty(oriImage);
        this.stegoImage = new SimpleStringProperty(stegoImage);
        this.msgLength = new SimpleIntegerProperty(msgLength);
        this.mse = new SimpleDoubleProperty(mse);
        this.psnr = new SimpleDoubleProperty(psnr);
    }

    public int getNomor() {
        return  this.nomor.get();
    }

    public int getMsgLength() {
        return this.msgLength.get();
    }

    public double getMse() {
        return this.mse.get();
    }

    public double getPsnr() {
        return this.psnr.get();
    }

    public String getOriImage() {
        return this.oriImage.get();
    }

    public String getStegoImage() {
        return this.stegoImage.get();
    }
}
