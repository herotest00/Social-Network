package socialnetwork.repository.database;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Group;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;


class MessageDbTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
    MessageDb messageDb = new MessageDb(url, username, password, new MessageValidator());
    GroupDb groupDb = new GroupDb(url, username, password);

    @BeforeAll
    static void beforeAll() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM UserGroupTo; DELETE FROM Messages; DELETE FROM Groups; DELETE FROM Users; ALTER SEQUENCE Users_id_seq RESTART WITH 1; ALTER SEQUENCE Groups_id_seq RESTART WITH 1; ALTER SEQUENCE Messages_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM UserGroupTo; DELETE FROM Messages; DELETE FROM Groups; DELETE FROM Users; ALTER SEQUENCE Users_id_seq RESTART WITH 1; ALTER SEQUENCE Groups_id_seq RESTART WITH 1; ALTER SEQUENCE Messages_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @Test
    void findOne() {
        User user = new User(1L, "test1", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test2", "test1", "Asd", "Asd");
        User user2 = new User(3L, "test3", "test1", "Asd", "Asd");
        User user3 = new User(4L, "test4", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        userDb.save(user2);
        userDb.save(user3);
        Group group = new Group(1L, "test");
        group.getUsers().addAll(Arrays.asList(user, user1, user2, user3));
        groupDb.save(group);
        messageDb.save(new Message(1L, user, group, "test"));
        Assertions.assertEquals(messageDb.findOne(1L).getTo(), group);
        Assertions.assertNull(messageDb.findOne(2L));
    }

    @Test
    void save() {
        User user = new User(1L, "test1", "test1", "Asd", "Asd");
        userDb.save(user);
        Group group = new Group(1L, "test");
        group.getUsers().add(user);
        groupDb.save(group);
        Message message = new Message(1L, user, group, "test");
        messageDb.save(message);
        Assertions.assertEquals(messageDb.getSize(), 1);
    }

    @Test
    void getSize() {
        User user = new User(1L, "test1", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test2", "test1", "Asd", "Asd");
        User user2 = new User(3L, "test3", "test1", "Asd", "Asd");
        User user3 = new User(4L, "test4", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        userDb.save(user2);
        userDb.save(user3);
        Group group = new Group(1L, "test");
        group.getUsers().addAll(Arrays.asList(user, user1, user2, user3));
        groupDb.save(group);
        Message message = new Message(1L, user, group, "test");
        Message message1 = new Message(2L, user2, group, "test1");
        messageDb.save(message);
        messageDb.save(message1);
        Assertions.assertEquals(messageDb.getSize(), 2);
    }

    @Test
    void findAllMessageForGroup() {
        User user = new User(1L, "test1", "test1", "Asd", "Asd");
        User user1 = new User(2L, "test2", "test1", "Asd", "Asd");
        User user2 = new User(3L, "test3", "test1", "Asd", "Asd");
        User user3 = new User(4L, "test4", "test1", "Asd", "Asd");
        userDb.save(user);
        userDb.save(user1);
        userDb.save(user2);
        userDb.save(user3);
        Group group = new Group(1L, "test");
        group.getUsers().add(user2);
        Group group1 = new Group(2L, "test");
        group1.getUsers().add(user1);
        groupDb.save(group);
        groupDb.save(group1);
        messageDb.save(new Message(1L, user, group, "test"));
        messageDb.save(new Message(2L, user, group, "test1"));
        messageDb.save(new Message(3L, user, group1, "test2"));
        Assertions.assertEquals(messageDb.findAllMessagesForGroup(1L).size(), 2);
        Assertions.assertEquals(messageDb.findAllMessagesForGroup(1L).get(1L), new Message(1L));
        Assertions.assertEquals(messageDb.findAllMessagesForGroup(1L).get(2L), new Message(2L));
        Assertions.assertEquals(messageDb.findAllMessagesForGroup(1L).get(1L).getMessage(), "test");
    }
}
