package socialnetwork.service;

import org.junit.jupiter.api.*;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Group;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.database.GroupDb;
import socialnetwork.repository.database.UserDb;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;


class GroupServiceTest {

    static String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.urlTEST");
    static String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    static String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    static Connection connection;
    UserDb userDb = new UserDb(url, username, password, new UserValidator());
    GroupDb groupDb = new GroupDb(url, username, password);
    GroupService groupService = new GroupService(userDb, groupDb);

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
    void addGroup() {
        userDb.save(new User("test", "test", "Test", "Test"));
        userDb.save(new User("test1", "test", "Test", "Test"));
        userDb.save(new User("test2", "test", "Test", "Test"));
        groupService.addGroup(Arrays.asList(new User(1L), new User(2L), new User(3L)));
        Assertions.assertThrows(NoSuchElementException.class, () -> groupService.addGroup(Collections.singletonList(new User(5L))));
    }

    @Test
    void updateGroup() {
        userDb.save(new User("test", "test", "Test", "Test"));
        userDb.save(new User("test1", "test", "Test", "Test"));
        userDb.save(new User("test2", "test", "Test", "Test"));
        userDb.save(new User("testceva", "testceva", "Test", "Test"));
        Group group = new Group("test");
        group.getUsers().addAll(Arrays.asList(new User(1L), new User(2L), new User(3L)));
        groupDb.save(group);
        groupService.updateGroup(1L, "test1", Collections.emptyList());
        Assertions.assertEquals(groupDb.findOne(1L).getName(), "test1");
        groupService.updateGroup(1L, "test1", Collections.singletonList(new User(4L, "testceva", "testceva", "Test", "Test")));
        Assertions.assertTrue(groupDb.findOne(1L).getUsers().containsAll(Arrays.asList(new User(4L, "testceva", "testceva", "Test", "Test"))));
        Assertions.assertThrows(NoSuchElementException.class, () -> groupService.updateGroup(1L, "test1", Collections.singletonList(new User(12L, "testceva", "testceva", "Test", "Test"))));
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
        Assertions.assertEquals(groupService.findAllGroupsForUser(1L).size(), 1);
        Assertions.assertEquals(groupService.findAllGroupsForUser(2L).size(), 2);
        Assertions.assertEquals(groupService.findAllGroupsForUser(2L).get(1L).getName(), "test");
        Assertions.assertEquals(groupService.findAllGroupsForUser(2L).get(1L).getUsers().size(), 3);
        Assertions.assertEquals(groupService.findAllGroupsForUser(2L).get(2L).getUsers().size(), 1);
        Assertions.assertThrows(NoSuchElementException.class, () -> groupService.findAllGroupsForUser(4L));
    }
}