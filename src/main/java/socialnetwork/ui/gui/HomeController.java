package socialnetwork.ui.gui;

import com.itextpdf.text.DocumentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.GroupService;
import socialnetwork.service.MessageService;
import socialnetwork.ui.gui.utils.Notification;
import socialnetwork.ui.gui.utils.SceneManager;
import socialnetwork.ui.gui.utils.ServicesLoader;
import socialnetwork.ui.gui.utils.enums.NotificationType;
import socialnetwork.ui.gui.utils.enums.Scenes;
import socialnetwork.ui.gui.utils.enums.Tables;
import socialnetwork.ui.gui.utils.observer.Observer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


public class HomeController extends Observer {

    ObservableList<FriendDTO> friends = FXCollections.observableArrayList();
    private User user;
    private final FriendshipService friendshipService = ServicesLoader.getInstance().friendshipService;
    private final GroupService groupService = ServicesLoader.getInstance().groupService;
    private final MessageService messageService = ServicesLoader.getInstance().messageService;

    @FXML private DatePicker startDate, endDate;
    @FXML private TextField searchBarFriends;
    @FXML private TableView<FriendDTO> friendsTable;
    @FXML private TableColumn<FriendDTO, String> lastNameColumn, firstNameColumn;
    @FXML public Button logoutButton, removeFriendButton, createChatFriendsButton;
    @FXML private Label userNameLabel;


    @FXML
    public void initialize() {
        friendsTable.setItems(friends);
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void loadFriendsTable() {
        friends.setAll(friendshipService.filterFriendshipsByID(user.getId()));
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            userNameLabel.setText(this.user.getUsername());
            loadFriendsTable();
            friendshipService.addObserver(this);
        }
    }

    public void createReportMessages() {
        LocalDate startAt = startDate.getValue();
        LocalDate endAt = endDate.getValue();
        if (startAt == null) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Choose starting date");
            return;
        }
        if (endAt == null) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Choose ending date!");
            return;
        }
        if (startAt.isAfter(endAt)) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Ending date is before starting date!");
            return;
        }
        try {
            FriendDTO user2 = friendsTable.getSelectionModel().getSelectedItem();
            if (user2 == null) {
                Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Choose friend!");
                return;
            }
            List<Group> commonGroups = groupService.findAllGroupForTwoUsers(user.getId(), user2.getID());
            List<Message> allMessages = new ArrayList<>();
            commonGroups.forEach(x -> allMessages.addAll(messageService.findAllMessagesForGroupFromPeriod(x.getId(), startAt.atStartOfDay(), endAt.atTime(23, 59, 59))));
            new PDF(allMessages, user, user2);

        } catch (RepoException | FileNotFoundException | DocumentException e){
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
        }
    }


    public void createReportActivity() {
        LocalDate startAt = startDate.getValue();
        LocalDate endAt = endDate.getValue();
        if (startAt == null)
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Choose starting date!");
        else if (endAt == null)
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Choose ending date!");
        else if (startAt.isAfter(endAt))
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Ending date is before starting date!");
        else {
            List<FriendDTO> userFriends = friendshipService.filterFriendshipsByPeriod(user.getId(), startAt, endAt);
            HashMap<Long, Group> allGroupsForUser = groupService.findAllGroupsForUser(user.getId());
            List<Message> allMessagesForUser = new ArrayList<>();
            allGroupsForUser.forEach((k, v) -> allMessagesForUser.addAll(messageService.findAllMessagesForGroupFromPeriod(v.getId(), startAt.atStartOfDay(), endAt.atTime(23, 59, 59))));
            try {
                new PDF(userFriends, allMessagesForUser);
            } catch (DocumentException | IOException e){
                Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
            }
        }
    }

    public void logoutButtonClicked() {
        SceneManager.getInstance().changeScene(Scenes.LOGIN);
        Notification.stopAnimation(MainController.notificationLabel);
    }

    public void searchFriends() {
        String[] s = searchBarFriends.getText().split(" ");
        if (s.length == 1){
            friends.setAll(friendshipService.filterFriendshipsByID(this.user.getId()).stream()
                    .filter(friendDTO -> friendDTO.getFirstName().toLowerCase().contains(s[0].toLowerCase())).collect(Collectors.toList()));
        }
        else if (s.length == 2){
            friends.setAll(friendshipService.filterFriendshipsByID(this.user.getId()).stream()
                    .filter(friendDTO -> friendDTO.getLastName().toLowerCase().contains(s[1].toLowerCase()) && friendDTO.getFirstName().toLowerCase().contains(s[0])).collect(Collectors.toList()));
        }
    }

    public void removeFriendClicked() {
        FriendDTO friendDTO = friendsTable.getSelectionModel().getSelectedItem();
        if (friendDTO == null)
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "No friend selected!");
        else{
            try{
                friendshipService.removeFriendship(user.getId(), friendDTO.getID());
                loadFriendsTable();
            } catch (NoSuchElementException | ValidationException | RepoException e) {
                Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
            }
        }
    }

    public void createChatFriendsClicked() {
        ObservableList<FriendDTO> users = friendsTable.getSelectionModel().getSelectedItems();
        if (users.size() == 0){
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "No friend selected!");
        }
        else {
            List<User> listUsers = users
                    .stream()
                    .map(x -> new User(x.getID(), x.getFirstName(), x.getLastName()))
                    .collect(Collectors.toList());
            listUsers.add(user);
            try {
                groupService.addGroup(listUsers);
            } catch (NoSuchElementException | RepoException e){
                Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
            }
        }
    }

    @Override
    public void update(Tables table) {
        if (table.equals(Tables.FRIENDS))
            loadFriendsTable();
    }
}
