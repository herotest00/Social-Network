package socialnetwork.domain.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.domain.User;


class UserValidatorTest {

    @Test
    void validate() {
        UserValidator validator = new UserValidator();
        User user = new User("test", "test1", "24asd", "Mircea");
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(user));
        user.setFirstName("Gabi");
        validator.validate(user);
    }
}