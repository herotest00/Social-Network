package socialnetwork.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import socialnetwork.domain.Event;
import socialnetwork.domain.User;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.EventService;
import socialnetwork.ui.gui.utils.Notification;
import socialnetwork.ui.gui.utils.ServicesLoader;
import socialnetwork.ui.gui.utils.enums.NotificationType;
import socialnetwork.ui.gui.utils.enums.Tables;
import socialnetwork.ui.gui.utils.observer.Observer;
import socialnetwork.utils.DateConstants;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.NoSuchElementException;


public class EventsController extends Observer {

    public Pagination subscribedEventsPagination;
    public Pagination allEventsPagination;
    public Button paneRemoveEventButton;
    ObservableList<Event> subscribedEvents = FXCollections.observableArrayList();
    ObservableList<Event> allEvents = FXCollections.observableArrayList();
    ObservableList<String> hours =
            FXCollections.observableArrayList(
                    generateHours(15)
            );
    EventService eventService = ServicesLoader.getInstance().eventService;
    public Tab eventTab;
    private User user;
    private int pane;

    public AnchorPane eventAnchorPane;
    public Pane allEventsPane, newEventPane, detailsEventPane, subscribedEventsPane;
    public TableView<Event> allEventsTable, subscribedEventsTable;
    public TableColumn<Event, String> titleAllEvents, titleSubscribedEvents, startAllEvents, endAllEvents, startSubscribedEvents, endSubscribedEvents;
    public Button subscribeButton, detailsEventButton, newEventButton1, subscribedEventsButton, submitEvent, backNewEventButton, paneNotifyEventButton, paneUpdateEventButton, backEventButton, unsubscribeButton, detailsSubscribedButton, newEventButton, allEventsButton;
    public TextField newEventTitle, paneTitleOfEvent, searchBarSubscribedEvents;
    public DatePicker startDateEvent, endDateEvent;
    public TextArea newEventArea, paneDescriptionOfEvent;
    public ComboBox<String> startHourEvent, endHourEvent;
    public Label paneIdOfEvent, paneStartOfEvent, paneEndOfEvent, paneUsersParticipatingEvent, paneCreatedByOfEvent;


    @FXML
    void initialize() {
        subscribedEventsTable.setItems(subscribedEvents);
        titleSubscribedEvents.setCellValueFactory(new PropertyValueFactory<>("title"));
        startSubscribedEvents.setCellValueFactory(new PropertyValueFactory<>("start_timeString"));
        endSubscribedEvents.setCellValueFactory(new PropertyValueFactory<>("end_timeString"));
        subscribedEventsPagination.setPageFactory(pageIndex -> {
            loadSubscribedEvents(pageIndex);
            return subscribedEventsTable;
        });
        allEventsTable.setItems(allEvents);
        titleAllEvents.setCellValueFactory(new PropertyValueFactory<>("title"));
        startAllEvents.setCellValueFactory(new PropertyValueFactory<>("start_timeString"));
        endAllEvents.setCellValueFactory(new PropertyValueFactory<>("end_timeString"));
        allEventsPagination.setPageFactory(pageIndex -> {
            loadAllEvents(pageIndex);
            return allEventsTable;
        });
        paneDescriptionOfEvent.setWrapText(true);
        newEventArea.setWrapText(true);
    }

    public void setUser(User user, Tab eventTab) {
        this.user = user;
        this.eventTab = eventTab;
        startHourEvent.setItems(hours);
        startHourEvent.getSelectionModel().selectFirst();
        endHourEvent.setItems(hours);
        endHourEvent.getSelectionModel().selectFirst();
        startDateEvent.setValue(LocalDate.now().plusDays(1));
        endDateEvent.setValue(LocalDate.now().plusDays(1));
        if (user != null) {
            addEvents();
            notifyUser();
            subscribedEventsPagination.setPageCount(eventService.getNoPagesForUser(user.getId()));
            allEventsPagination.setPageCount(eventService.getNoPages());
            eventService.addObserver(this);
        }
    }

    private void loadSubscribedEvents(int page) {
        subscribedEvents.setAll(eventService.findAllForUser(user.getId(), page).values());
    }

    private void loadAllEvents(int page) {
        allEvents.setAll(eventService.findAll(page).values());
    }

    private void notifyUser() {
        String notification = eventService.findAllNotifiedForUser(user.getId(), Duration.ofMinutes(120)).stream()
                .map(x -> "The event: " + x.getTitle() + " will begin at: " + x.getStart_time().format(DateConstants.DATE_TIME_FORMATTER) + "\n")
                .reduce("", String::concat);
        if (!notification.isEmpty())
            Notification.showNotification(MainController.notificationLabel, NotificationType.CONFIRMATION, notification);
    }

    private void addEvents() {
        addEventDetailsEvent();
        addEventTextAreaEnter(paneDescriptionOfEvent);
        addEventTextAreaEnter(newEventArea);
    }

    public void submitNewEventClicked() {
        String title = newEventTitle.getText();
        if (title.isEmpty()) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Title can't be empty!");
            return;
        }
        String description = newEventArea.getText();
        LocalDate startDate = startDateEvent.getValue();
        if (startDate == null) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Select a starting date!");
            return;
        }
        LocalDate endDate = endDateEvent.getValue();
        if (endDate == null) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Select an ending date!");
            return;
        }
        String startHourAndMinute = startHourEvent.getValue();

        String endHourAndMinute = endHourEvent.getValue();
        LocalTime startTime = LocalTime.parse( startHourAndMinute ) ;
        LocalTime endTime = LocalTime.parse( endHourAndMinute ) ;
        LocalDateTime startLocalDateTime = LocalDateTime.parse(startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + startTime + ":00", DateConstants.DATE_TIME_FORMATTER);
        LocalDateTime endLocalDateTime = LocalDateTime.parse(endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + endTime + ":00", DateConstants.DATE_TIME_FORMATTER);
        if (startLocalDateTime.isBefore(LocalDateTime.now())) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Starting date can't be in the past!");
            return;
        }
        if (startLocalDateTime.isAfter(endLocalDateTime)) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Starting date is after ending date!");
            return;
        }

        try {
            eventService.addEvent(user, title, description, startLocalDateTime, endLocalDateTime);
            newEventPane.getChildren().remove(MainController.notificationLabel);
            eventAnchorPane.getChildren().add(MainController.notificationLabel);
            newEventArea.setText("");
            newEventTitle.setText("");
            startDateEvent.setValue(LocalDate.now().plusDays(1));
            endDateEvent.setValue(LocalDate.now().plusDays(1));
            startHourEvent.getSelectionModel().selectFirst();
            endHourEvent.getSelectionModel().selectFirst();
            eventTab.setContent(eventAnchorPane);
            pane = 0;
        } catch (NoSuchElementException | RepoException e){
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
        }
    }


    public void handlerUpdateButton() {
        String title = paneTitleOfEvent.getText();
        String description = paneDescriptionOfEvent.getText();
        long id = Long.parseLong(paneIdOfEvent.getText());
        if (!eventService.findOne(Long.parseLong(paneIdOfEvent.getText())).getUser().getId().equals(this.user.getId())) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "You are not allowed to update this event!");
            return;
        }
        try {
            eventService.updateEvent(id, title, description);
        } catch (NoSuchElementException | RepoException e) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
        }
    }

    public void handlerRemoveButton() {
        long id = Long.parseLong(paneIdOfEvent.getText());
        if (!eventService.findOne(id).getUser().getId().equals(this.user.getId())) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "You are not allowed to delete this event!");
            return;
        }
        try {
            eventService.deleteEvent(id);
            backEventButton.fire();
        } catch (RepoException | NoSuchElementException e){
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
        }

    }

    public void unsubscribeEventClicked() {
        Event event = subscribedEventsTable.getSelectionModel().getSelectedItem();
        if (event == null){
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Select an event!");
            return;
        }
        if (event.getUser().getId().equals(user.getId())) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Can't unsubscribe to your event!");
            return;
        }
        try {
            eventService.deleteUserFromEvent(user.getId(), event.getId());
        } catch (NoSuchElementException | RepoException e) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
        }
    }


    public void subscribeEventClicked() {
        Event event = allEventsTable.getSelectionModel().getSelectedItem();
        if (event == null) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Select an event!");
            return;
        }
        try {
            eventService.saveUserInEvent(user.getId(), event.getId());
        } catch (NoSuchElementException | RepoException e) {
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, e.getMessage());
        }
    }

    private ArrayList<String> generateHours(int interval){
        ArrayList<String> hours = new ArrayList<>();
        int number = 60 / interval;
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < number; j++) {
                int minute = j * interval;
                if (minute < 10)
                    hours.add("0" + i + ":0" + j * interval);
                else
                    hours.add("0" + i + ":" + j * interval);
            }
        for (int i = 10; i < 24; i++)
            for (int j = 0; j < number; j++) {
                int minute = j * interval;
                if (minute < 10)
                    hours.add(i + ":0" + j * interval);
                else
                    hours.add(i + ":" + j * interval);
            }
        return hours;
    }

    public int detailsEventClicked(TableView<Event> eventsTable) {
        Event event = eventsTable.getSelectionModel().getSelectedItem();
        if (event == null){
            Notification.showNotification(MainController.notificationLabel, NotificationType.ERROR, "Select an event!");
            return 1;
        }

        paneTitleOfEvent.setText(event.getTitle());
        paneDescriptionOfEvent.setText(event.getDescription());
        String startDate =  event.getStart_time().format(DateConstants.DATE_TIME_FORMATTER);
        String endDate = event.getEnd_time().format(DateConstants.DATE_TIME_FORMATTER);
        paneStartOfEvent.setText("Starts at " + startDate.split(" ")[0] + "\n\t\t" + startDate.split(" ")[1]);
        paneEndOfEvent.setText("Ends at  " + endDate.split(" ")[0] + "\n\t\t" + endDate.split(" ")[1]);
        paneIdOfEvent.setText(event.getId().toString());
        User owner = event.getUser();

//        detailsEventPane.getChildren().remove(backEventButton);
//        detailsEventPane.getChildren().add(backEventButton);

        detailsEventPane.getChildren().remove(paneUpdateEventButton);
        detailsEventPane.getChildren().remove(paneRemoveEventButton);
        paneDescriptionOfEvent.setEditable(false);
        paneTitleOfEvent.setEditable(false);
        paneTitleOfEvent.setFocusTraversable(false);
        paneDescriptionOfEvent.setFocusTraversable(false);
        
        if (owner.getId().equals(user.getId())) {
            detailsEventPane.getChildren().add(paneUpdateEventButton);
            detailsEventPane.getChildren().add(paneRemoveEventButton);
            paneDescriptionOfEvent.setEditable(true);
            paneTitleOfEvent.setEditable(true);
            paneTitleOfEvent.setFocusTraversable(true);
            paneDescriptionOfEvent.setFocusTraversable(true);
        }

        paneCreatedByOfEvent.setText("Owner: " + owner.getFirstName() + " " + owner.getLastName());
        paneUsersParticipatingEvent.setText(eventService.findAllForEvent(event.getId()).size() + " users participating");

        if (event.isNotify()) {
            paneNotifyEventButton.setText("Silent");
        }
        else {
            paneNotifyEventButton.setText("Notify");
        }
        handlerNotifyButton(event);


        return 0;
    }

    public void handlerNotifyButton(Event event) {
        paneNotifyEventButton.setOnAction(ev -> {
            if (paneNotifyEventButton.getText().equals("Notify")) {
                event.setNotify(true);
                paneNotifyEventButton.setText("Silent");
                eventService.updateNotifyForEvent(user.getId(), event.getId(), true);
            }
            else {
                event.setNotify(false);
                paneNotifyEventButton.setText("Notify");
                eventService.updateNotifyForEvent(user.getId(), event.getId(), false);
            }
        });
    }

    private void addEventDetailsEvent() {
        eventAnchorPane.getChildren().remove(detailsEventPane);
        eventAnchorPane.getChildren().remove(newEventPane);
        eventAnchorPane.getChildren().remove(allEventsPane);

        detailsEventPane.setOpacity(1);
        newEventPane.setOpacity(1);
        allEventsPane.setOpacity(1);
        subscribedEventsPane.setOpacity(1);

        eventTab.setContent(eventAnchorPane);

        //  all events pane
        detailsEventPane.getChildren().remove(paneUpdateEventButton);
        detailsEventButton.setOnAction(event -> {
            if (detailsEventClicked(allEventsTable) == 0) {
                eventAnchorPane.getChildren().remove(MainController.notificationLabel);
                detailsEventPane.getChildren().add(MainController.notificationLabel);
                eventTab.setContent(detailsEventPane);
                pane = 1;
            }
        });

        newEventButton1.setOnAction(event -> {
            eventAnchorPane.getChildren().remove(MainController.notificationLabel);
            newEventPane.getChildren().add(MainController.notificationLabel);
            eventTab.setContent(newEventPane);
            pane = 2;
        });

        subscribedEventsButton.setOnAction(event -> {
            eventAnchorPane.getChildren().remove(allEventsPane);
            eventAnchorPane.getChildren().add(subscribedEventsPane);
        });

        //  subscribed pane
        detailsSubscribedButton.setOnAction(event -> {
            if (detailsEventClicked(subscribedEventsTable) == 0) {
                subscribedEventsPane.getChildren().remove(MainController.notificationLabel);
                detailsEventPane.getChildren().add(MainController.notificationLabel);
                eventTab.setContent(detailsEventPane);
                pane = 1;
            }
        });

        newEventButton.setOnAction(event -> {
            eventAnchorPane.getChildren().remove(MainController.notificationLabel);
            newEventPane.getChildren().add(MainController.notificationLabel);
            eventTab.setContent(newEventPane);
            pane = 2;
        });

        allEventsButton.setOnAction(event -> {
            eventAnchorPane.getChildren().remove(subscribedEventsPane);
            eventAnchorPane.getChildren().add(allEventsPane);
        });

        //  new event pane
        backNewEventButton.setOnAction(event -> {
            newEventPane.getChildren().remove(MainController.notificationLabel);
            eventAnchorPane.getChildren().add(MainController.notificationLabel);
            eventTab.setContent(eventAnchorPane);
            pane = 0;
        });

        submitEvent.setOnAction(event -> submitNewEventClicked());

        // details pane
        paneUpdateEventButton.setOnAction(event -> {
            handlerUpdateButton();
            detailsEventPane.getChildren().remove(MainController.notificationLabel);
            eventAnchorPane.getChildren().add(MainController.notificationLabel);
            eventTab.setContent(eventAnchorPane);
            pane = 0;
        });

        backEventButton.setOnAction(event -> {
            detailsEventPane.getChildren().remove(MainController.notificationLabel);
            eventAnchorPane.getChildren().add(MainController.notificationLabel);
            eventTab.setContent(eventAnchorPane);
            pane = 0;
        });
    }

    private void addEventTextAreaEnter(TextArea textArea) {
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    KeyCode code = event.getCode();

                    // code for moving with tab from textarea
                    if (code == KeyCode.TAB && !event.isShiftDown() && !event.isControlDown()) {
                        event.consume();
                        paneNotifyEventButton.requestFocus();
                    }

                    //it executes the new line
                    if (code == KeyCode.ENTER) {
                        event.consume();
                        int caretPosition = textArea.getCaretPosition();
                        String firstText = textArea.getText(0, caretPosition);
                        String secondText = textArea.getText(caretPosition, textArea.getText().length());
                        textArea.setText(firstText + "\n" + secondText);
                        textArea.positionCaret(caretPosition + 1);
                    }
                }
        );
    }

    public int getPane() {
        return pane;
    }

    @Override
    public void update(Tables table) {
        switch (table) {
            case ALLEVENTS: allEventsPagination.setPageCount(eventService.getNoPages()); loadAllEvents(0); allEventsPagination.setCurrentPageIndex(0); break;
            case SUBSCRIBEDEVENTS: subscribedEventsPagination.setPageCount(eventService.getNoPagesForUser(user.getId())); loadSubscribedEvents(0); subscribedEventsPagination.setCurrentPageIndex(0); break;
        }
    }
}
