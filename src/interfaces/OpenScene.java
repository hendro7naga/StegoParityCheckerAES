package interfaces;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import main.Main;

/**
 * Created by hendro.sinaga on 24-Jul-16.
 */
public interface OpenScene {
    default void open (String doc, double width, double height) throws Exception {
        Parent parent = null;
        String docName = "", title = "";

        switch (doc) {
            case "about":
                docName = "about/aboutdoc.fxml";
                title = "About";
                break;
            case "embedding":
                docName = "stegano/embeddingdoc.fxml";
                title = "Embedding";
                break;
            case "extraction":
                docName = "stegano/extractiondoc.fxml";
                title = "Extraction";
                break;
            case "imperceptibility":
                docName = "stegano/imperceptibilitydoc.fxml";
                title = "Imperceptibility";
                break;
            case "robustness":
                docName = "stegano/robustnessdoc.fxml";
                title = "Robustness";
                break;
            case "result":
                docName = "stegano/testresultdoc.fxml";
                title = "Result";
                break;
            default:
                docName = "main/maindoc.fxml";
                title = "Aplikasi Steganografi";
                break;
        }

        parent = FXMLLoader.load(getClass().getClassLoader().getResource(docName));

        if (parent == null) {
            throw new Exception("Gagal membuka scene " + doc);
        }
        else {
            System.gc();
            Main.mainStage.setTitle(title);
            //Main.mainStage.setScene(new Scene(parent, width, height));
            Main.mainStage.setWidth(width);
            Main.mainStage.setHeight(height);
            Main.mainStage.getScene().setRoot(parent);
            Main.mainStage.centerOnScreen();
        }
    }
}
