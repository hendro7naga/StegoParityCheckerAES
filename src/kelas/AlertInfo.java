package kelas;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Created by hendro.sinaga on 10-Jun-16.
 */
public class AlertInfo {
    private static Alert alert;

    public static void showAlertInfoMessage(String alertTitle,
                                    String alertHeader,
                                    String alertMessage,
                                    ButtonType buttonType) {
        alert = new Alert(Alert.AlertType.INFORMATION,
                alertMessage,
                buttonType);
        alert.setTitle(alertTitle);
        alert.setHeaderText(alertHeader);
        alert.show();
    }

    public static void showAlertWarningMessage(String alertTitle,
                                        String alertHeader,
                                        String alertMessage,
                                        ButtonType buttonType) {
        alert = new Alert(Alert.AlertType.WARNING,
                alertMessage,
                buttonType);
        alert.setTitle(alertTitle);
        alert.setHeaderText(alertHeader);
        alert.show();
    }

    public static void showAlertErrorMessage(String alertTitle,
                                        String alertHeader,
                                        String alertMessage,
                                        ButtonType buttonType) {
        alert = new Alert(Alert.AlertType.ERROR,
                alertMessage,
                buttonType);
        alert.setTitle(alertTitle);
        alert.setHeaderText(alertHeader);
        alert.show();
    }
}
