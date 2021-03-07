package socialnetwork.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.UserService;
import socialnetwork.ui.gui.utils.Notification;
import socialnetwork.ui.gui.utils.SceneManager;
import socialnetwork.ui.gui.utils.ServicesLoader;
import socialnetwork.ui.gui.utils.enums.NotificationType;
import socialnetwork.ui.gui.utils.enums.Scenes;


public class LoginController {

    UserService userService = ServicesLoader.getInstance().userService;

    @FXML
    public AnchorPane loginPane;
    @FXML
    Button loginButton, registerButton;
    @FXML
    TextField username, password;
    @FXML
    Label notificationLabel;


    public void loginButtonClicked() {
        String username = this.username.getText();
        String password = this.password.getText();
        try {
            User user = userService.findAccount(username, password);
            Notification.stopAnimation(notificationLabel);
            SceneManager.getInstance().changeScene(Scenes.MAIN, user);
            clearFields();
        } catch (ValidationException | RepoException e) {
            Notification.showNotification(notificationLabel, NotificationType.ERROR, e.getMessage());
        }
    }

    public void registerButtonClicked() {
        Notification.stopAnimation(notificationLabel);
        SceneManager.getInstance().changeScene(Scenes.REGISTER);
    }

    private void clearFields() {
        username.setText("");
        password.setText("");
    }
}
