package socialnetwork.service;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Event;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.database.EventDb;
import socialnetwork.repository.database.UserDb;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.utils.DateConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
    EventDb eventDb = new EventDb(url, username, password);
    EventService eventService = new EventService(userDb, eventDb);

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
    void addEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        userDb.save(user);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.addEvent(new User(2L), "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2)));
    }

    @Test
    void findAllForUser() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        eventService.addEvent(user1, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        Assertions.assertEquals(eventService.findAllForUser(1).size(), 1);
        Assertions.assertEquals(eventService.findAllForUser(2).size(), 1);
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.findAllForUser(3));
    }

    @Test
    void saveUserInEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        eventService.saveUserInEvent(2L, 1L);
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.saveUserInEvent(5L, 3L));
    }

    @Test
    void findAll() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        userDb.save(user);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        Assertions.assertEquals(eventService.findAll().size(), 3);
    }

    @Test
    void findAllForEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        User user2 = new User(3L, "test2", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        userDb.save(user2);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        eventService.saveUserInEvent(2L, 1L);
        eventService.saveUserInEvent(3L, 1L);
        Assertions.assertEquals(eventService.findAllForEvent(1L).size(), 3);
        Assertions.assertEquals(eventService.findAllForEvent(1L).get(1L), user);
        Assertions.assertEquals(eventService.findAllForEvent(1L).get(2L), user1);
        Assertions.assertEquals(eventService.findAllForEvent(1L).get(3L), user2);
        Assertions.assertEquals(eventService.findAllForEvent(2L).size(), 1);
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.findAllForEvent(5L));
    }

    @Test
    void deleteUserFromEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        User user2 = new User(3L, "test2", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        userDb.save(user2);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        eventService.saveUserInEvent(2L, 1L);
        eventService.saveUserInEvent(3L, 1L);
        Assertions.assertEquals(eventService.findAllForEvent(1L).size(), 3);
        eventService.deleteUserFromEvent(2L, 1L);
        Assertions.assertEquals(eventService.findAllForEvent(1L).size(), 2);
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.deleteUserFromEvent(4L, 1L));
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.deleteUserFromEvent(2L, 3L));
    }

    @Test
    void updateNotifyForEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        eventService.saveUserInEvent(2L, 1L);
        Assertions.assertTrue(eventService.findAllForUser(2L).get(1L).isNotify());
        eventService.updateNotifyForEvent(2L, 1L, false);
        Assertions.assertFalse(eventService.findAllForUser(2L).get(1L).isNotify());
        Assertions.assertTrue(eventService.findAllForUser(1L).get(1L).isNotify());
        eventService.updateNotifyForEvent(2L, 1L, true);
        Assertions.assertTrue(eventService.findAllForUser(2L).get(1L).isNotify());
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.updateNotifyForEvent(4L, 1L, true));
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.updateNotifyForEvent(2L, 3L, false));
    }

    @Test
    void findAllNotifiedForUser() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test1", "test1", "Asd", "Asd");
        User user2 = new User(3L, "test2", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        userDb.save(user2);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plus(Duration.ofDays(1)));
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plus(Duration.ofDays(1)));
        eventService.saveUserInEvent(2L, 1L);
        eventService.saveUserInEvent(3L, 1L);
        eventService.saveUserInEvent(2L, 2L);
        Assertions.assertEquals(eventService.findAllNotifiedForUser(1L, Duration.ofMinutes(40)).size(), 2);
        Assertions.assertEquals(eventService.findAllNotifiedForUser(2L, Duration.ofMinutes(40)).size(), 2);
        Assertions.assertEquals(eventService.findAllNotifiedForUser(3L, Duration.ofMinutes(40)).size(), 1);
    }

    @Test
    void deleteEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        userDb.save(user);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        Assertions.assertEquals(eventService.findAllForUser(1L).size(), 1);
        eventService.deleteEvent(1L);
        Assertions.assertEquals(eventService.findAllForUser(1L).size(), 0);
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.deleteEvent(4L));
    }

    @Test
    void updateEvent() {
        User user = new User(1L, "test", "test1", "Asd", "Asd");
        userDb.save(user);
        eventService.addEvent(user, "TEST", "TEST DESCRIERE", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
        Assertions.assertEquals(eventService.findAll().get(1L).getTitle(), "TEST");
        Assertions.assertEquals(eventService.findAll().get(1L).getDescription(), "TEST DESCRIERE");
        eventService.updateEvent(1L, "TEST UPDATE", "TEST DESCRIERE UPDATE");
        Assertions.assertEquals(eventService.findAll().get(1L).getTitle(), "TEST UPDATE");
        Assertions.assertEquals(eventService.findAll().get(1L).getDescription(), "TEST DESCRIERE UPDATE");
        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.updateEvent(3L, "ASD", "ASD"));
    }
}