package socialnetwork.ui.gui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import socialnetwork.domain.User;
import socialnetwork.ui.gui.MainController;

import java.io.IOException;


class SceneInitializer {

    private final static SceneInitializer sceneInitializer = new SceneInitializer();

    public static SceneInitializer getInstance() {
        return sceneInitializer;
    }

    public Pane loadLogin() throws IOException {
        FXMLLoader fxmlLoader =  new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
        AnchorPane root = fxmlLoader.load();
        ThemeHandler.addRoot(root);
        ThemeHandler.readTheme(root);
        return root;
    }

    public Pane loadRegister() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/registerView.fxml"));
        AnchorPane root = fxmlLoader.load();
        ThemeHandler.addRoot(root);
        ThemeHandler.readTheme(root);
        return root;
    }

    public Parent loadMain(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/mainView.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();
        mainController.setUser(user);
        ThemeHandler.addRoot(root);
        ThemeHandler.readTheme(root);
        return root;
    }
}
