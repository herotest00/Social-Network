package socialnetwork.repository.database;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


class FriendshipDbTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
    FriendshipDb friendshipDb = new FriendshipDb(url, username, password, new FriendshipValidator());

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
    void findOne() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        Friendship friendship = new Friendship(1L, 2L);
        friendshipDb.save(friendship);
        Assertions.assertEquals(friendshipDb.findOne(new Tuple<>(1L, 2L)), friendship);
        Assertions.assertNull(friendshipDb.findOne(new Tuple<>(1L, 4L)));
    }

    @Test
    void findAll() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        friendshipDb.save(new Friendship(1L, 2L));
        Assertions.assertTrue(friendshipDb.findAll().containsKey(new Tuple<>(2L, 1L)));
    }

    @Test
    void save() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        friendshipDb.save(new Friendship(1L, 2L));
        Assertions.assertThrows(RepoException.class, () -> friendshipDb.save(new Friendship(1L, 2L)));
        Assertions.assertThrows(RepoException.class, () -> friendshipDb.save(new Friendship(2L, 1L)));
    }

    @Test
    void delete() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        friendshipDb.save(new Friendship(1L, 2L));
        Assertions.assertEquals(friendshipDb.getSize(), 1);
        friendshipDb.delete(new Tuple<>(1L, 2L));
        Assertions.assertEquals(friendshipDb.getSize(), 0);
    }

    @Test
    void getSize() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        friendshipDb.save(new Friendship(1L, 2L));
        Assertions.assertEquals(friendshipDb.getSize(), 1);
    }
}
