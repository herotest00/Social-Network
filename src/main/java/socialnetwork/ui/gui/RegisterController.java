package socialnetwork.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.UserService;
import socialnetwork.ui.gui.utils.Notification;
import socialnetwork.ui.gui.utils.SceneManager;
import socialnetwork.ui.gui.utils.ServicesLoader;
import socialnetwork.ui.gui.utils.enums.NotificationType;
import socialnetwork.ui.gui.utils.enums.Scenes;


public class RegisterController {

    UserService userService = ServicesLoader.getInstance().userService;

    @FXML
    Tooltip firstnameTooltip, lastnameTooltip;
    @FXML
    TextField username, password, passwordConfirm, firstName, lastName;
    @FXML
    Button registerButton, backButton;
    @FXML
    Label notificationLabel;


    @FXML
    public void initialize() {
        firstnameTooltip.setShowDelay(Duration.millis(300));
        lastnameTooltip.setShowDelay(Duration.millis(300));
    }

    public void registerButtonClicked() {
        String username = this.username.getText();
        if (username.equals("")) {
            Notification.showNotification(notificationLabel, NotificationType.ERROR, "Invalid username!");
            return;
        }
        String password = this.password.getText();
        if (password.equals("")) {
            Notification.showNotification(notificationLabel, NotificationType.ERROR, "Invalid password!");
            return;
        }
        String passwordConfirmation = this.passwordConfirm.getText();
        if (!password.equals(passwordConfirmation)) {
            Notification.showNotification(notificationLabel, NotificationType.ERROR, "Passwords do not match!");
            return;
        }
        String firstName = this.firstName.getText();
        if (!firstName.matches("^[A-Z][a-z]{2,}$")) {
            Notification.showNotification(notificationLabel, NotificationType.ERROR, "Invalid first name!");
            return;
        }
        String lastName = this.lastName.getText();
        if (!lastName.matches("^[A-Z][a-z]{2,}$")) {
            Notification.showNotification(notificationLabel, NotificationType.ERROR, "Invalid last name!");
            return;
        }
        try {
            userService.addUser(username, password, firstName, lastName);
            Notification.showNotification(notificationLabel, NotificationType.CONFIRMATION, "Successfully registered!");
            backButtonClicked();
        } catch (ValidationException | RepoException e) {
            Notification.showNotification(notificationLabel, NotificationType.ERROR, e.getMessage());
        }
    }

    public void backButtonClicked() {
        Notification.stopAnimation(notificationLabel);
        SceneManager.getInstance().changeScene(Scenes.LOGIN);
        clearFields();
    }

    private void clearFields() {
        username.setText("");
        password.setText("");
        password.setText("");
        passwordConfirm.setText("");
        firstName.setText("");
        lastName.setText("");
    }
}
