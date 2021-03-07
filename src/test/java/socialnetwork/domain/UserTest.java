package socialnetwork.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


class UserTest {

    @Test
    void getFirstName() {
        User user = new User("Mircea", "Badea", "Mircea", "Badea");
        Assertions.assertEquals(user.getFirstName(), "Mircea");
    }

    @Test
    void setFirstName() {
        User user = new User("Mircea", "Badea", "Mircea", "Badea");
        user.setFirstName("Qqq");
        Assertions.assertEquals(user.getFirstName(), "Qqq");
    }

    @Test
    void getLastName() {
        User user = new User("Mircea", "Badea", "Mircea", "Badea");
        Assertions.assertEquals(user.getLastName(), "Badea");
    }

    @Test
    void setLastName() {
        User user = new User("Mircea", "Badea", "Mircea", "Badea");
        user.setLastName("Qqq");
        Assertions.assertEquals(user.getLastName(), "Qqq");
    }

    @Test
    void getFriends() {
        User user = new User("Mircea", "Badea", "Mircea", "Badea");
        Assertions.assertEquals(user.getFriends(), new ArrayList<>());
    }

    @Test
    void getUsername() {
        User user = new User("test", "test1", "Mircea", "Badea");
        Assertions.assertEquals(user.getUsername(), "test");
    }

    @Test
    void getPassword() {
        User user = new User("test", "test1", "Mircea", "Badea");
        Assertions.assertEquals(user.getPassword(), "test1");
    }
}