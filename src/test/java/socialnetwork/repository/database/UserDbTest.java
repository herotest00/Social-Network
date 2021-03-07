package socialnetwork.repository.database;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


class UserDbTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());

    @BeforeAll
    static void beforeAll() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; ALTER SEQUENCE Users_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; ALTER SEQUENCE Users_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @Test
    void findOne() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDb.findOne(null));
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        Assertions.assertEquals(userDb.findOne(1L).getId(), 1L);
        Assertions.assertNull(userDb.findOne(5L));
        Assertions.assertEquals(userDb.findOne(1L).getFriends().size(), 0);
    }

    @Test
    void findAll() {
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        userDb.save(new User("test2", "test1", "Asd", "Asd"));
        userDb.save(new User("test3", "test1", "Asd", "Asd"));
        Assertions.assertEquals(userDb.findAll().get(1L).getId(), 1L);
        Assertions.assertEquals(userDb.findAll().get(2L).getId(), 2L);
        Assertions.assertEquals(userDb.findAll().get(3L).getId(), 3L);
        Assertions.assertEquals(userDb.findAll().size(), 3);
    }

    @Test
    void save() {
        User user = new User("test", "test1", "Asd", "Asd");
        userDb.save(user);
        Assertions.assertThrows(RepoException.class, () -> userDb.save(user));
    }

    @Test
    void delete() {
        User user = new User("test", "test1", "Asd", "Asd");
        userDb.save(user);
        Assertions.assertEquals(userDb.getSize(), 1);
        userDb.delete(1L);
        Assertions.assertEquals(userDb.getSize(), 0);
    }

    @Test
    void getSize() {
        userDb.save(new User("test1", "test1", "Asd", "Asd"));
        userDb.save(new User("test2", "test1", "Asd", "Asd"));
        userDb.save(new User("test3", "test1", "Asd", "Asd"));
        Assertions.assertEquals(userDb.getSize(), 3);
    }

    @Test
    void findOneAccount() {
        userDb.save(new User("test", "test1", "Asd", "Asd"));
        Assertions.assertThrows(RepoException.class, () -> userDb.findAccount("asd", "test1"));
        Assertions.assertEquals(userDb.findAccount("test", "test1").getFirstName(), "Asd");
    }
}