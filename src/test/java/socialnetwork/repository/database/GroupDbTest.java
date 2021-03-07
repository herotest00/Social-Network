package socialnetwork.repository.database;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Group;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GroupDbTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
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
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM UserGroupTo; DELETE FROM Users; DELETE FROM Groups; ALTER SEQUENCE Users_id_seq RESTART WITH 1; ALTER SEQUENCE groups_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM UserGroupTo; DELETE FROM Users; DELETE FROM Groups; ALTER SEQUENCE users_id_seq RESTART WITH 1; ALTER SEQUENCE groups_id_seq RESTART WITH 1;") ) {
            deleteStatement.executeUpdate();
        } catch (SQLException ignore) { }
    }

    @Test
    void save() {
        userDb.save(new User("test", "test", "Test", "Test"));
        userDb.save(new User("test1", "test", "Test", "Test"));
        userDb.save(new User("test2", "test", "Test", "Test"));
        Group group = new Group("test");
        group.getUsers().addAll(Arrays.asList(new User(1L), new User(2L), new User(3L)));
        groupDb.save(group);
    }

    @Test
    void update() {
        userDb.save(new User("test", "test", "Test", "Test"));
        userDb.save(new User("test1", "test", "Test", "Test"));
        userDb.save(new User("test2", "test", "Test", "Test"));
        userDb.save(new User("testceva", "testceva", "Test", "Test"));
        Group group = new Group("test");
        group.getUsers().addAll(Arrays.asList(new User(1L), new User(2L), new User(3L)));
        groupDb.save(group);
        Group group1 = new Group(1L, "test1");
        groupDb.update(group1);
        Assertions.assertEquals(groupDb.findOne(1L).getName(), "test1");
        group1.getUsers().add(new User(4L, "testceva", "testceva", "Test", "Test"));
        groupDb.update(group1);
        Assertions.assertTrue(groupDb.findOne(1L).getUsers().containsAll(Arrays.asList(new User(4L, "testceva", "testceva", "Test", "Test"))));
    }

    @Test
    void findOne() {
        userDb.save(new User("test", "test", "Test", "Test"));
        userDb.save(new User("test1", "test", "Test", "Test"));
        userDb.save(new User("test2", "test", "Test", "Test"));
        Group group = new Group("test");
        group.getUsers().addAll(Arrays.asList(new User(1L), new User(2L), new User(3L)));
        groupDb.save(group);
        Assertions.assertEquals(groupDb.findOne(1L).getName(), "test");
        Assertions.assertTrue(groupDb.findOne(1L).getUsers().containsAll(Arrays.asList(new User(1L), new User(2L), new User(3L))));
        Assertions.assertNull(groupDb.findOne(4L));
    }

    @Test
    void findAllGroupsForUser() {
        userDb.save(new User("test", "test", "Test", "Test"));
        userDb.save(new User("test1", "test", "Test", "Test"));
        userDb.save(new User("test2", "test", "Test", "Test"));
        Group group = new Group("test");
        group.getUsers().addAll(Arrays.asList(new User(1L), new User(2L), new User(3L)));
        groupDb.save(group);
        Group group1 = new Group("test1");
        group1.getUsers().add(new User(2L));
        groupDb.save(group1);
        Assertions.assertEquals(groupDb.findAllGroupsForUser(1L).size(), 1);
        Assertions.assertEquals(groupDb.findAllGroupsForUser(2L).size(), 2);
        Assertions.assertEquals(groupDb.findAllGroupsForUser(2L).get(1L).getName(), "test");
        Assertions.assertEquals(groupDb.findAllGroupsForUser(2L).get(1L).getUsers().size(), 3);
        Assertions.assertEquals(groupDb.findAllGroupsForUser(2L).get(2L).getUsers().size(), 1);
    }
}