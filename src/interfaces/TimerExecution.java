package interfaces;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Created by hendro.sinaga on 18-Aug-16.
 */
public interface TimerExecution {
    void startCountTime() throws Exception;

    void stopCountTime() throws Exception;
}
