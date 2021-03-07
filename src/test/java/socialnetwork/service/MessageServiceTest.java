/*
package socialnetwork.service;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.MessageDb;
import socialnetwork.repository.database.UserDb;
import socialnetwork.service.exceptions.ServiceException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;


class MessageServiceTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    Repository<Long, User> userDb = new UserDb(url, username, password, new UserValidator());
    Repository<Long, Message> messageDb = new MessageDb(url, username, password, new MessageValidator());
    MessageService messageService = new MessageService(userDb, messageDb);

    @BeforeAll
    static void beforeAll() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; DELETE FROM Messages;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Users; DELETE FROM Messages;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @Test
    void sendMessage() {
        for (long i = 1 ; i <= 12 ; i++) {
            User user = new User("Test", "Test");
            user.setId(i);
            userDb.save(user);
        }
        messageService.sendMessage(2L, Arrays.asList(4L, 6L, 8L, 12L), "test");
        messageService.sendMessage(4L, Arrays.asList(3L, 9L, 1L), "test1");
        Assertions.assertThrows(NoSuchElementException.class, () -> messageService.sendMessage(12L, Arrays.asList(4L, 1L, 2L, 24L, 5L), "test2"));
        Assertions.assertThrows(NoSuchElementException.class, () -> messageService.sendMessage(53L, Collections.singletonList(4L), "test2"));
        Assertions.assertEquals(messageDb.getSize(), 2);
    }

    @Test
    void replyToMessage() {
        for (long i = 1 ; i <= 12 ; i++) {
            User user = new User("Test", "Test");
            user.setId(i);
            userDb.save(user);
        }
        messageService.sendMessage(2L, Arrays.asList(4L, 6L, 8L, 12L), "test");
        messageService.sendMessage(4L, Arrays.asList(3L, 9L, 1L), "test1");
        Assertions.assertThrows(NoSuchElementException.class, () -> messageService.replyToMessage(42L, "test", 1L));
        Assertions.assertThrows(NoSuchElementException.class, () -> messageService.replyToMessage(2L, "test", 3L));
        Assertions.assertThrows(ServiceException.class, () -> messageService.replyToMessage(10L, "test", 2L));
        Assertions.assertThrows(ServiceException.class, () -> messageService.replyToMessage(4L, "test", 2L));
        messageService.replyToMessage(9L, "test", 2L);
        Assertions.assertEquals(messageDb.getSize(), 3);
    }

    @Test
    void filterMessages() {
        for (long i = 1 ; i <= 12 ; i++) {
            User user = new User("Test", "Test");
            user.setId(i);
            userDb.save(user);
        }
        messageService.sendMessage(2L, Collections.singletonList(4L), "sal");
        messageService.sendMessage(4L, Collections.singletonList(2L), "sal si tie");
        messageService.sendMessage(2L, Collections.singletonList(4L), "sal si tie si tie");
        messageService.sendMessage(4L, Collections.singletonList(2L), "sal si tie si tie si tie");
        Assertions.assertEquals(messageService.filterMessages(2L, 4L).size(), 4);
        Assertions.assertThrows(NoSuchElementException.class, () -> messageService.filterMessages(4L, 14L));
    }
}*/
