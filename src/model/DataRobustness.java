package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by hendro.sinaga on 18-Jul-16.
 */
public class DataRobustness {
    private SimpleIntegerProperty nomor;
    private SimpleStringProperty stegoImage;
    private SimpleDoubleProperty noiseProb, similarity;

    public DataRobustness (Integer nomor, String stegoImage, Double noiseProb, Double similarity) {
        this.nomor = new SimpleIntegerProperty(nomor);
        this.stegoImage = new SimpleStringProperty(stegoImage);
        this.noiseProb = new SimpleDoubleProperty(noiseProb);
        this.similarity = new SimpleDoubleProperty(similarity);
    }

    public int getNomor() {
        return nomor.get();
    }

    public String getStegoImage() {
        return stegoImage.get();
    }

    public double getNoiseProb() {
        return noiseProb.get();
    }

    public double getSimilarity() {
        return similarity.get();
    }

}
