package socialnetwork.ui.gui.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import socialnetwork.domain.User;
import socialnetwork.ui.gui.utils.enums.Scenes;

import java.io.IOException;
import java.util.HashMap;


public class SceneManager {

    private Scene mainScene;
    private final HashMap<Scenes, Parent> roots = new HashMap<>();
    private static final SceneManager sceneManager = new SceneManager();
    SceneInitializer sceneInitializer = SceneInitializer.getInstance();

    private SceneManager() {
        try {
            roots.putIfAbsent(Scenes.LOGIN, sceneInitializer.loadLogin());
            roots.putIfAbsent(Scenes.REGISTER, sceneInitializer.loadRegister());
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void setScene(Scene scene) {
        this.mainScene = scene;
    }

    public static SceneManager getInstance() {
        return sceneManager;
    }

    public void changeScene(Scenes name) {
        switch (name) {
            case LOGIN:
                mainScene.setRoot(roots.get(Scenes.LOGIN));
                return;
            case REGISTER:
                mainScene.setRoot(roots.get(Scenes.REGISTER));
        }
    }

    public void changeScene(Scenes name, User user) {
        try {
            if (name == Scenes.MAIN) {
                mainScene.setRoot(SceneInitializer.getInstance().loadMain(user));
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public Parent getRoot(Scenes scene) {
        return roots.get(scene);
    }
}
