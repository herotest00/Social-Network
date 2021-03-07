package socialnetwork.ui.gui.utils;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.database.*;
import socialnetwork.service.*;

public class ServicesLoader {

    private final static ServicesLoader servicesLoader = new ServicesLoader();

    private final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
    private final String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    private final String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    public UserDb userDb = new UserDb(url, username, password, new UserValidator());
    public FriendRequestDb friendRequestDb = new FriendRequestDb(url, username, password, new FriendRequestValidator());
    public FriendshipDb friendshipDb = new FriendshipDb(url, username, password, new FriendshipValidator());
    public MessageDb messageDb = new MessageDb(url, username, password, new MessageValidator());
    public GroupDb groupDb = new GroupDb(url, username, password);
    public EventDb eventDb = new EventDb(url, username, password);
    public UserService userService;
    public FriendRequestService friendRequestService;
    public FriendshipService friendshipService;
    public GroupService groupService;
    public MessageService messageService;
    public EventService eventService;

    private ServicesLoader() {
        userService = new UserService(userDb);
        friendshipService = new FriendshipService(userDb, friendshipDb);
        friendRequestService = new FriendRequestService(userDb, friendshipDb, friendRequestDb);
        groupService = new GroupService(userDb, groupDb);
        messageService = new MessageService(userDb, messageDb, groupDb);
        eventService = new EventService(userDb, eventDb);
    }

    public static ServicesLoader getInstance() {
        return servicesLoader;
    }
}
