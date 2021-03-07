package socialnetwork.repository.database;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Event;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.utils.DateConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;


class EventDbTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
    EventDb eventDb = new EventDb(url, username, password);

    @BeforeAll
    static void beforeAll() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM UserEvent; DELETE FROM Events; DELETE FROM Users; ALTER SEQUENCE Users_id_seq RESTART WITH 1; ALTER SEQUENCE Events_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM UserEvent;  DELETE FROM Events; DELETE FROM Users; ALTER SEQUENCE Users_id_seq RESTART WITH 1; ALTER SEQUENCE Events_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @Test
    void findAllForUser() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        eventDb.save(new Event(user1, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        Assertions.assertEquals(eventDb.findAllForUser(1).size(), 2);
        Assertions.assertEquals(eventDb.findAllForUser(2).size(), 1);
        Assertions.assertEquals(eventDb.findAllForUser(3).size(), 0);
    }

    @Test
    void findAll() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        userDb.save(user);
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        Assertions.assertEquals(eventDb.findAll().size(), 3);
    }

    @Test
    void save() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        userDb.save(user);
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
    }

    @Test
    void saveUserInEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        eventDb.saveUserInEvent(2L, 1L);
    }

    @Test
    void findAllForEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        User user2 = new User(3L, "test2", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        userDb.save(user2);
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        eventDb.saveUserInEvent(2L, 1L);
        eventDb.saveUserInEvent(3L, 1L);
        Assertions.assertEquals(eventDb.findAllForEvent(1L).size(), 3);
        Assertions.assertEquals(eventDb.findAllForEvent(1L).get(1L), user);
        Assertions.assertEquals(eventDb.findAllForEvent(1L).get(2L), user1);
        Assertions.assertEquals(eventDb.findAllForEvent(1L).get(3L), user2);
        Assertions.assertEquals(eventDb.findAllForEvent(2L).size(), 1);
    }

    @Test
    void deleteUserFromEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        User user2 = new User(3L, "test2", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        userDb.save(user2);
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        eventDb.saveUserInEvent(2L, 1L);
        eventDb.saveUserInEvent(3L, 1L);
        Assertions.assertEquals(eventDb.findAllForEvent(1L).size(), 3);
        eventDb.deleteUserFromEvent(2L, 1L);
        Assertions.assertEquals(eventDb.findAllForEvent(1L).size(), 2);
    }

    @Test
    void updateNotifyForEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        eventDb.saveUserInEvent(2L, 1L);
        Assertions.assertTrue(eventDb.findAllForUser(2L).get(1L).isNotify());
        eventDb.updateNotifyForEvent(2L, 1L, false);
        Assertions.assertFalse(eventDb.findAllForUser(2L).get(1L).isNotify());
        Assertions.assertTrue(eventDb.findAllForUser(1L).get(1L).isNotify());
        eventDb.updateNotifyForEvent(2L, 1L, true);
        Assertions.assertTrue(eventDb.findAllForUser(2L).get(1L).isNotify());
    }

    @Test
    void delete() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        userDb.save(user);
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        Assertions.assertEquals(eventDb.findAllForUser(1L).size(), 1);
        eventDb.delete(1L);
        Assertions.assertEquals(eventDb.findAllForUser(1L).size(), 0);
    }

    @Test
    void update() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        userDb.save(user);
        eventDb.save(new Event(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        Assertions.assertEquals(eventDb.findAll().get(1L).getTitle(), "TEST");
        Assertions.assertEquals(eventDb.findAll().get(1L).getDescription(), "TEST DESCRIERE");
        eventDb.update(new Event(1L, new User(), "TEST UPDATE", "TEST DESCRIERE UPDATE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
        Assertions.assertEquals(eventDb.findAll().get(1L).getTitle(), "TEST UPDATE");
        Assertions.assertEquals(eventDb.findAll().get(1L).getDescription(), "TEST DESCRIERE UPDATE");
    }
}