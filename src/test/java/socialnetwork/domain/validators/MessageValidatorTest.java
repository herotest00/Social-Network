package socialnetwork.domain.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;


class MessageValidatorTest {

    @Test
    void validate() {
        MessageValidator validator = new MessageValidator();
        Message message = new Message(1L, new User(-1L, "Asd", "Asd"), "Salut", null);
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(message));
    }
}