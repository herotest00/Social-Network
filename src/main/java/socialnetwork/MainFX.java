package socialnetwork;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import socialnetwork.ui.gui.utils.Notification;
import socialnetwork.ui.gui.utils.SceneManager;
import socialnetwork.ui.gui.utils.enums.Scenes;

public class MainFX extends Application {

    @Override
    public void start(Stage loginStage) {

        Scene mainScene = new Scene(SceneManager.getInstance().getRoot(Scenes.LOGIN));

        SceneManager.getInstance().setScene(mainScene);

        Notification.setupAnimations();

        loginStage.setResizable(false);
        loginStage.setScene(mainScene);
        loginStage.setTitle("Social network");
        loginStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
