package socialnetwork.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import socialnetwork.domain.Group;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.GroupService;
import socialnetwork.service.MessageService;
import socialnetwork.ui.gui.utils.Notification;
import socialnetwork.ui.gui.utils.ServicesLoader;
import socialnetwork.ui.gui.utils.enums.NotificationType;
import socialnetwork.ui.gui.utils.enums.Tables;
import socialnetwork.ui.gui.utils.observer.Observer;

import java.util.NoSuchElementException;


public class ChatsController extends Observer {

    ObservableList<Group> groups = FXCollections.observableArrayList();
    ObservableList<Message> messages = FXCollections.observableArrayList();
    private final GroupService groupService = ServicesLoader.getInstance().groupService;
    private final MessageService messageService = ServicesLoader.getInstance().messageService;
    private User user;
    private long group_id;

    public ListView<Group> groupsList;
    public ListView<Message> messagesList;
    public TextArea messageArea;
    public Button sendButton;


    @FXML
    void initialize() {
        groupsList.setItems(groups);
        messagesList.setItems(messages);
        addEventMessageArea();
        groupsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection != oldSelection) {
                group_id = newSelection.getId();
                loadMessages(groupsList.getSelectionModel().getSelectedItem().getId());
                messagesList.scrollTo(messages.size());
            }
        });
        sendButton.setOnMouseClicked(mouseEvent -> sendMessageClicked());
    }

    void loadChats() {
        groups.setAll(groupService.findAllGroupsForUser(user.getId()).values());
    }

    private void loadMessages(Long id) {
        messages.setAll(messageService.findAllMessagesForGroup(id));
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            loadChats();
            messageService.addObserver(this);
            groupService.addObserver(this);
        }
    }

    public void sendMessageClicked() {
        String msg = messageArea.getText();
        if (msg.isEmpty()) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Message can't be empty!");
            return;
        }
        if (groupsList.getSelectionModel().getSelectedItem() == null) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Select a group!");
            return;
        }
        msg = msg.replaceAll(" +$", "");
        if (msg.isEmpty()) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Message can't be empty!");
            return;
        }
        Message reply = messagesList.getSelectionModel().getSelectedItem();
        Group group = groupsList.getSelectionModel().getSelectedItem();
        try {
            messageService.sendMessage(user.getId(), group.getId(), msg, reply);
            messagesList.scrollTo(messages.size());
            messageArea.setText("");
        } catch (ValidationException | NoSuchElementException | RepoException e) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
        }
    }

    private void addEventMessageArea() {
        messageArea.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    KeyCode code = event.getCode();

                    // code for moving with tab from textarea
                    if (code == KeyCode.TAB && !event.isShiftDown() && !event.isControlDown()) {
                        event.consume();
                        sendButton.requestFocus();
                    }

                    //it executes the new line
                    if (code == KeyCode.ENTER && event.isShiftDown()) {
                        event.consume();
                        TextArea localArea = messageArea;
                        int caretPosition = localArea.getCaretPosition();
                        String firstText = localArea.getText(0, caretPosition);
                        String secondText = localArea.getText(caretPosition, localArea.getText().length());
                        localArea.setText(firstText + "\n" + secondText);
                        localArea.positionCaret(caretPosition + 1);
                    }

                    //it executes the send
                    if (code == KeyCode.ENTER && !event.isShiftDown()) {
                        sendMessageClicked();
                        event.consume();
                    }
                }
        );
    }

    @Override
    public void update(Tables table) {
        if (table.equals(Tables.MESSAGES))
            loadMessages(group_id);
        if (table.equals(Tables.GROUPS))
            loadChats();
    }
}
