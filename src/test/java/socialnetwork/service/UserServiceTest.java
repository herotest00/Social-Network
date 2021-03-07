package socialnetwork.service;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.database.UserDb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


class UserServiceTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
    UserService userService = new UserService(userDb);

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
    void addUser() {
        userService.addUser("test", "test", "Test", "Test");
        Assertions.assertEquals(userDb.getSize(), 1);
    }

    @Test
    void removeUser() {
        userService.addUser("test", "test", "Test", "Test");
        Assertions.assertEquals(userDb.getSize(), 1);
        userService.removeUser(1);
        Assertions.assertEquals(userDb.getSize(), 0);
    }

    @Test
    void findAll() {
        Assertions.assertEquals(userDb.findAll().size(), 0);
        userService.addUser("test", "test", "Test", "Test");
        userService.addUser("test1", "test", "Test", "Test");
        userService.addUser("test2", "test", "Test", "Test");
        Assertions.assertTrue(userDb.findAll().containsKey(3L));
        Assertions.assertTrue(userDb.findAll().containsKey(1L));
        Assertions.assertTrue(userDb.findAll().containsKey(2L));
    }

    @Test
    void findOne() {
        userService.addUser("test", "test", "Test", "Test");
        Assertions.assertNull(userService.findOne(2L));
        Assertions.assertEquals(userService.findOne(1L).getId(), 1L);
    }
}