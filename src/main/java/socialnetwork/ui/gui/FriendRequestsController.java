package socialnetwork.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.FriendDTO;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.FriendRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.ui.gui.utils.Notification;
import socialnetwork.ui.gui.utils.ServicesLoader;
import socialnetwork.ui.gui.utils.enums.NotificationType;
import socialnetwork.ui.gui.utils.enums.Tables;
import socialnetwork.ui.gui.utils.observer.Observer;

import java.util.NoSuchElementException;


public class FriendRequestsController extends Observer {

    ObservableList<FriendDTO> receivedFriendRequests = FXCollections.observableArrayList(), sentFriendRequests = FXCollections.observableArrayList();
    private final FriendRequestService friendRequestService = ServicesLoader.getInstance().friendRequestService;
    private final FriendshipService friendshipService = ServicesLoader.getInstance().friendshipService;
    private User user;

    public TableView<FriendDTO> receivedFriendRequestsTable, sentFriendRequestsTable;
    public TableColumn<FriendDTO, String> firstNameFRPColumn, lastNameFRPColumn, dateFRPColumn, firstNameFRSColumn, lastNameFRSColumn, dateFRSColumn;
    public Button acceptButton, declineButton, cancelButton;


    @FXML
    void initialize() {
        receivedFriendRequestsTable.setItems(receivedFriendRequests);
        firstNameFRPColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameFRPColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dateFRPColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        sentFriendRequestsTable.setItems(sentFriendRequests);
        firstNameFRSColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameFRSColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dateFRSColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void loadSentFriendRequests() {
        sentFriendRequests.setAll(friendRequestService.sentFriendRequests(user.getId()));
    }

    private void loadReceivedFriendRequests() {
        receivedFriendRequests.setAll(friendRequestService.receivedFriendRequests(user.getId()));
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            loadSentFriendRequests();
            loadReceivedFriendRequests();
            friendRequestService.addObserver(this);
        }
    }

    public void acceptFriendRequest() {
        FriendDTO friendDTO = receivedFriendRequestsTable.getSelectionModel().getSelectedItem();
        if (friendDTO == null)
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "No friend request selected!");
        else{
            try{
                friendRequestService.deleteFriendRequest(friendDTO.getID(), user.getId());
                friendshipService.addFriendship(friendDTO.getID(), user.getId());
            } catch (NoSuchElementException | ValidationException | RepoException e) {
                Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
            }
        }
    }

    public void declineFriendRequest() {
        FriendDTO friendDTO = receivedFriendRequestsTable.getSelectionModel().getSelectedItem();
        if (friendDTO == null)
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "No friend request selected!");
        else{
            try{
                friendRequestService.deleteFriendRequest(friendDTO.getID(), user.getId());
            } catch (NoSuchElementException | ValidationException | RepoException e) {
                Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
            }
        }
    }

    public void cancelFriendRequest() {
        FriendDTO friendDTO = sentFriendRequestsTable.getSelectionModel().getSelectedItem();
        if (friendDTO == null)
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "No friend request selected!");
        else {
            try{
                friendRequestService.deleteFriendRequest(user.getId(), friendDTO.getID());
            } catch (NoSuchElementException | ValidationException | RepoException e) {
                Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
            }
        }
    }

    @Override
    public void update(Tables table) {
        switch (table) {
            case SENTREQUESTS: loadSentFriendRequests(); break;
            case RECEIVEDREQUESTS: loadReceivedFriendRequests(); break;
        }
    }
}
