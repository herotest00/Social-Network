package socialnetwork.repository.database;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


class FriendRequestDbTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
    FriendRequestDb friendRequestDb = new FriendRequestDb(url, username, password, new FriendRequestValidator());

    @BeforeAll
    static void beforeAll() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; DELETE FROM FriendRequests; ALTER SEQUENCE Users_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; DELETE FROM FriendRequests; ALTER SEQUENCE Users_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @Test
    void findOne() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        FriendRequest friendRequest = new FriendRequest(1L, 2L);
        friendRequestDb.save(friendRequest);
        Assertions.assertEquals(friendRequestDb.findOne(new Tuple<>(1L, 2L)), friendRequest);
        Assertions.assertNull(friendRequestDb.findOne(new Tuple<>(2L, 1L)));
    }

    @Test
    void findAll() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        userDb.save(new User("test2", "test1", "Asd", "Asd"));
        userDb.save(new User("test3", "test1", "Asd", "Asd"));
        friendRequestDb.save(new FriendRequest(1L, 2L));
        friendRequestDb.save(new FriendRequest(4L, 3L));
        Assertions.assertTrue(friendRequestDb.findAll().containsKey(new Tuple<>(1L, 2L)));
        Assertions.assertTrue(friendRequestDb.findAll().containsKey(new Tuple<>(4L, 3L)));
        Assertions.assertEquals(friendRequestDb.findAll().size(), 2);
    }

    @Test
    void save() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        userDb.save(new User("test2", "test1", "Asd", "Asd"));
        userDb.save(new User("test3", "test1", "Asd", "Asd"));
        friendRequestDb.save(new FriendRequest(1L, 2L));
        friendRequestDb.save(new FriendRequest(4L, 3L));
        Assertions.assertThrows(RepoException.class, () -> friendRequestDb.save(new FriendRequest(1L, 2L)));
    }

    @Test
    void delete() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        friendRequestDb.save(new FriendRequest(1L, 2L));
        Assertions.assertEquals(friendRequestDb.getSize(), 1);
        friendRequestDb.delete(new Tuple<>(1L, 2L));
        Assertions.assertEquals(friendRequestDb.getSize(), 0);
    }

    @Test
    void getSize() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        userDb.save(new User("test2", "test1", "Asd", "Asd"));
        userDb.save(new User("test3", "test1", "Asd", "Asd"));
        friendRequestDb.save(new FriendRequest(1L, 2L));
        friendRequestDb.save(new FriendRequest(4L, 3L));
        Assertions.assertEquals(friendRequestDb.getSize(), 2);
    }
}
