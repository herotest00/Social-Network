package socialnetwork.ui.gui.utils;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;
import socialnetwork.ui.gui.utils.enums.NotificationType;


public class Notification {

    private final static double initialOpacity = 0.0, finalOpacity = 0.82;
    private final static Duration showDuration = new Duration(4000), fadeDuration = new Duration(850);
    private final static FadeTransition fadeIn = new FadeTransition(fadeDuration), fadeOut = new FadeTransition(fadeDuration);
    private final static PauseTransition pause = new PauseTransition(showDuration);


    public static void setupAnimations() {
        fadeIn.setFromValue(initialOpacity);
        fadeIn.setToValue(finalOpacity);
        fadeOut.setFromValue(finalOpacity);
        fadeOut.setToValue(initialOpacity);
        pause.setOnFinished(actionEvent -> fadeOut.play());
        fadeIn.setOnFinished(actionEvent -> pause.play());
    }

    public static void showNotification(Label label, NotificationType type, String text) {
        stopAnimation(label);
        fadeIn.setNode(label);
        fadeOut.setNode(label);
        switch (type) {
            case ERROR:
                label.getStyleClass().remove(0);
                label.setText(text);
                label.getStyleClass().add("notification-label-error");
                fadeIn.play();
                return;
            case CONFIRMATION:
                label.getStyleClass().remove(0);
                label.setText(text);
                label.getStyleClass().add("notification-label-confirmation");
                fadeIn.play();
        }
    }

    public static void stopAnimation(Label label) {
        fadeIn.stop();
        pause.stop();
        fadeOut.stop();
        label.setOpacity(0);
    }
}
