package socialnetwork.ui.gui;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import socialnetwork.domain.User;

import java.util.HashMap;


public class MainController {

    HashMap<Tab, Pane> roots = new HashMap<>();
    User user;
    public static Label notificationLabel = new Label();


    @FXML private TabPane tabPane;
    @FXML private Tab homeTab, friendRequestsTab, usersTab, chatsTab, eventsTab, settingsTab;
    @FXML private AnchorPane home, friendRequests, users, chats, events, settings;
    @FXML private HomeController homeController;
    @FXML private FriendRequestsController friendRequestsController;
    @FXML private UsersController usersController;
    @FXML private ChatsController chatsController;
    @FXML private EventsController eventsController;
    @FXML private SettingsController settingsController;

    public void setUser(User user) {
        this.user = user;
        homeController.setUser(user);
        friendRequestsController.setUser(user);
        usersController.setUser(user);
        chatsController.setUser(user);

        setupEvents();
        //notifications
        setHashMap();
        home.getChildren().add(notificationLabel);
        notificationLabel.setMouseTransparent(true);

        eventsController.setUser(user, eventsTab);
    }

    private void setupEvents() {
        tabPane.addEventFilter(KeyEvent.KEY_PRESSED, action -> {
            if (action.getCode().equals(KeyCode.TAB) && action.isControlDown()) {
                int size = tabPane.getTabs().size();

                if (size > 0) {
                    TabPaneBehavior tabPaneBehavior = new TabPaneBehavior(tabPane);

                    int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();

                    if (!action.isShiftDown()) {
                        if (selectedIndex < size -1) {
                            tabPaneBehavior.selectNextTab();
                        } else {
                            tabPaneBehavior.selectTab(tabPane.getTabs().get(0));
                        }
                    } else {
                        if (selectedIndex > 0) {
                            tabPaneBehavior.selectPreviousTab();
                        } else {
                            tabPaneBehavior.selectTab(tabPane.getTabs().get(size - 1));
                        }
                    }

                    action.consume();
                }

            }
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            roots.get(oldTab).getChildren().remove(notificationLabel);
            if (newTab.getText().equals("Events") && eventsController.getPane() == 1) {
                eventsController.detailsEventPane.getChildren().add(notificationLabel);
                return;
            }
            else if (newTab.getText().equals("Events") && eventsController.getPane() == 2) {
                eventsController.newEventPane.getChildren().add(notificationLabel);
                return;
            }
            roots.get(newTab).getChildren().add(notificationLabel);
        });
    }

    private void setHashMap() {
        roots.put(homeTab, home);
        roots.put(friendRequestsTab, friendRequests);
        roots.put(usersTab, users);
        roots.put(chatsTab, chats);
        roots.put(eventsTab, events);
        roots.put(settingsTab, settings);
    }
}
