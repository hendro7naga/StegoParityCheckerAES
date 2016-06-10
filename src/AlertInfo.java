import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Created by hendro.sinaga on 10-Jun-16.
 */
public class AlertInfo {
    private static AlertInfo ourInstance = new AlertInfo();
    private static Alert alert;

    public static AlertInfo getInstance() {
        return ourInstance;
    }

    private AlertInfo() {
    }

    public void showAlertInfoMessage(String alertTitle,
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

    public void showAlertWarningMessage(String alertTitle,
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

    public void showAlertErrorMessage(String alertTitle,
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
