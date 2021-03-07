package socialnetwork.service;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.database.FriendRequestDb;
import socialnetwork.repository.database.FriendshipDb;
import socialnetwork.repository.database.UserDb;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;


class FriendRequestServiceTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
    FriendshipDb friendshipDb = new FriendshipDb(url, username, password, new FriendshipValidator());
    FriendRequestDb friendRequestDb = new FriendRequestDb(url, username, password, new FriendRequestValidator());
    FriendRequestService friendRequestService = new FriendRequestService(userDb, friendshipDb, friendRequestDb);

    @BeforeAll
    static void beforeAll() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ignored) { }
    }

    @BeforeEach
    void setUp() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; DELETE FROM Friendships; DELETE FROM Friendrequests; ALTER SEQUENCE Users_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; DELETE FROM Friendships; DELETE FROM Friendrequests; ALTER SEQUENCE Users_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @Test
    void sendFriendRequest() {
        for (long i = 1 ; i <= 10 ; i++) {
            User user = new User("test" + i, "test", "Asd", "Asd");
            userDb.save(user);
        }
        friendRequestService.sendFriendRequest(1L, 4L);
        Assertions.assertEquals(friendRequestDb.getSize(), 1);
        Assertions.assertThrows(NoSuchElementException.class, () -> friendRequestService.sendFriendRequest(1L, 24L));
        Assertions.assertThrows(KeyAlreadyExistsException.class, () -> friendRequestService.sendFriendRequest(4L, 1L));
        friendshipDb.save(new Friendship(5L, 9L));
        Assertions.assertThrows(KeyAlreadyExistsException.class, () -> friendRequestService.sendFriendRequest(5L, 9L));
    }

    @Test
    void approveFriendRequest() {
        for (long i = 1 ; i <= 10 ; i++) {
            User user = new User("test" + i, "test", "Asd", "Asd");
            userDb.save(user);
        }
        friendRequestService.sendFriendRequest(1L, 4L);
        Assertions.assertEquals(friendRequestDb.getSize(), 1);
    }

    @Test
    void rejectFriendRequest() {
        for (long i = 1 ; i <= 10 ; i++) {
            User user = new User("test" + i, "test", "Asd", "Asd");
            userDb.save(user);
        }
        friendRequestService.sendFriendRequest(1L, 4L);
        Assertions.assertEquals(friendRequestDb.getSize(), 1);
        friendRequestService.deleteFriendRequest(1L, 4L);
        Assertions.assertEquals(friendRequestDb.getSize(), 0);
        Assertions.assertThrows(NoSuchElementException.class, () -> friendRequestService.deleteFriendRequest(1L, 24L));
        Assertions.assertThrows(NoSuchElementException.class, () -> friendRequestService.deleteFriendRequest(1L, 7L));
    }

    @Test
    void receivedFriendRequests() {
    }

    @Test
    void sentFriendRequests() {
    }
}
