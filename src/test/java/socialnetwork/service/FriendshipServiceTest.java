package socialnetwork.service;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.database.FriendshipDb;
import socialnetwork.repository.database.UserDb;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.NoSuchElementException;


class FriendshipServiceTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
    FriendshipDb friendshipDb = new FriendshipDb(url, username, password, new FriendshipValidator());
    FriendshipService friendshipService = new FriendshipService(userDb, friendshipDb);

    @BeforeAll
    static void beforeAll() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; DELETE FROM Friendships; ALTER SEQUENCE Users_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; DELETE FROM Friendships; ALTER SEQUENCE Users_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @Test
    void filterFriendshipsByID() {
        for (long i = 1 ; i <= 10 ; i++) {
            User user = new User("test" + i, "test", "Asd", "Asd");
            userDb.save(user);
        }
        friendshipService.addFriendship(1L, 2L);
        friendshipService.addFriendship(2L, 3L);
        friendshipService.addFriendship(3L, 4L);
        friendshipService.addFriendship(4L, 9L);
        friendshipService.addFriendship(4L, 8L);
        friendshipService.addFriendship(4L, 5L);
        friendshipService.addFriendship(8L, 7L);
        friendshipService.addFriendship(5L, 6L);
        friendshipService.addFriendship(6L, 10L);
        Assertions.assertEquals(friendshipService.filterFriendshipsByID(4).size(), 4);
        Assertions.assertEquals(friendshipService.filterFriendshipsByID(6).size(), 2);
    }

    @Test
    void filterFriendshipsByMonth() {
        for (long i = 1 ; i <= 10 ; i++) {
            User user = new User("test" + i, "test", "Asd", "Asd");
            userDb.save(user);
        }
        friendshipService.addFriendship(1L, 2L);
        friendshipService.addFriendship(2L, 3L);
        friendshipService.addFriendship(3L, 4L);
        friendshipService.addFriendship(4L, 9L);
        friendshipService.addFriendship(4L, 8L);
        friendshipService.addFriendship(4L, 5L);
        friendshipService.addFriendship(8L, 7L);
        friendshipService.addFriendship(5L, 6L);
        friendshipService.addFriendship(6L, 10L);
        Assertions.assertEquals(friendshipService.filterFriendshipsByMonth(4, LocalDate.now().getMonthValue()).size(), 4);
        Assertions.assertEquals(friendshipService.filterFriendshipsByMonth(4, LocalDate.now().getMonthValue() - 1).size(), 0);
    }

    @Test
    void addFriendship() {
        userDb.save(new User("test", "test", "Test", "Test"));
        userDb.save(new User("test1", "test", "Test", "Test"));
        friendshipService.addFriendship(1L, 2L);
        Assertions.assertEquals(friendshipDb.getSize(), 1);
        Assertions.assertThrows(RepoException.class, () -> friendshipService.addFriendship(1L, 2L));
    }

    @Test
    void removeFriendship() {
        userDb.save(new User("test", "test", "Test", "Test"));
        userDb.save(new User("test1", "test", "Test", "Test"));
        friendshipService.addFriendship(1L, 2L);
        Assertions.assertEquals(friendshipDb.getSize(), 1);
        friendshipService.removeFriendship(1L, 2L);
        Assertions.assertEquals(friendshipDb.getSize(), 0);
        Assertions.assertThrows(NoSuchElementException.class, () -> friendshipService.addFriendship(2L, 4L));
    }

    @Test
    void numberOfCommunities() {
        for (long i = 1 ; i <= 15 ; i++) {
            User user = new User("test" + i, "test", "Asd", "Asd");
            userDb.save(user);
        }
        friendshipService.addFriendship(1L, 2L);
        friendshipService.addFriendship(2L, 3L);
        friendshipService.addFriendship(3L, 4L);
        friendshipService.addFriendship(4L, 9L);
        friendshipService.addFriendship(4L, 8L);
        friendshipService.addFriendship(4L, 5L);
        friendshipService.addFriendship(8L, 7L);
        friendshipService.addFriendship(5L, 6L);
        friendshipService.addFriendship(6L, 10L);
        friendshipService.addFriendship(11L, 14L);
        Assertions.assertEquals(friendshipService.numberOfCommunities(), 5);
    }

    @Test
    void mostActiveCommunity() {
        for (long i = 1 ; i <= 15 ; i++) {
            User user = new User("test" + i, "test", "Asd", "Asd");
            userDb.save(user);
        }
        friendshipService.addFriendship(1L, 2L);
        friendshipService.addFriendship(2L, 3L);
        friendshipService.addFriendship(3L, 4L);
        friendshipService.addFriendship(4L, 9L);
        friendshipService.addFriendship(4L, 8L);
        friendshipService.addFriendship(4L, 5L);
        friendshipService.addFriendship(8L, 7L);
        friendshipService.addFriendship(5L, 6L);
        friendshipService.addFriendship(6L, 10L);
        Assertions.assertEquals(friendshipService.mostActiveCommunity().getRight(), (Integer) 6);
    }
}
