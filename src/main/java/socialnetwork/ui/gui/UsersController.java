package socialnetwork.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.FriendRequestService;
import socialnetwork.service.GroupService;
import socialnetwork.service.UserService;
import socialnetwork.ui.gui.utils.Notification;
import socialnetwork.ui.gui.utils.ServicesLoader;
import socialnetwork.ui.gui.utils.enums.NotificationType;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


public class UsersController {

    ObservableList<User> everyone = FXCollections.observableArrayList();
    private final UserService userService;
    private final FriendRequestService friendRequestService;
    private final GroupService groupService;
    private User user;

    public TextField searchBarUsers;
    public TableView<User> usersTable;
    public TableColumn<User, String> firstNameColumnUsers, lastNameColumnUsers;
    public Button sendFriendRequestButton, createChatUsers;


    public UsersController() {
        userService = ServicesLoader.getInstance().userService;
        friendRequestService = ServicesLoader.getInstance().friendRequestService;
        groupService = ServicesLoader.getInstance().groupService;
    }

    @FXML
    void initialize() {
        usersTable.setItems(everyone);
        firstNameColumnUsers.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumnUsers.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    void loadUsers() {
        everyone.setAll(userService.findAll().values());
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            loadUsers();

        }
    }

    public void searchUsers() {
        String[] s = searchBarUsers.getText().split(" ");
        if (s.length == 1){
            everyone.setAll(userService.findAll().values().stream()
                    .filter(user -> user.getFirstName().toLowerCase().contains(s[0].toLowerCase())).collect(Collectors.toList()));
        }
        else if (s.length == 2){
            everyone.setAll(userService.findAll().values().stream()
                    .filter(user -> user.getLastName().toLowerCase().contains(s[1].toLowerCase()) && user.getFirstName().toLowerCase().contains(s[0])).collect(Collectors.toList()));
        }
    }

    public void sendFriendRequestClicked() {
        User user = usersTable.getSelectionModel().getSelectedItem();
        if (user == null)
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "No user selected!");
        else{
            try{
                friendRequestService.sendFriendRequest(this.user.getId(), user.getId());
            } catch (NoSuchElementException | KeyAlreadyExistsException | ValidationException | RepoException e) {
                Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
            }
        }
    }

    public void createChatUsersClicked() {
        ObservableList<User> users = usersTable.getSelectionModel().getSelectedItems();
        if (users.size() == 0){
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "No user selected!");
        }
        else {
            List<User> listUsers = users
                    .stream()
                    .map(x -> new User(x.getId(), x.getFirstName(), x.getLastName()))
                    .collect(Collectors.toList());
            if (!listUsers.contains(user))
                listUsers.add(user);
            try {
                groupService.addGroup(listUsers);
            } catch (NoSuchElementException | RepoException e){
                Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
            }
        }
    }
}
