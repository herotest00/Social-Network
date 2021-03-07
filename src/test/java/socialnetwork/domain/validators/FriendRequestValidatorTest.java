package socialnetwork.domain.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Tuple;


class FriendRequestValidatorTest {

    @Test
    void validate() {
        FriendRequestValidator validator = new FriendRequestValidator();
        FriendRequest friendRequest = new FriendRequest(24L, 17L);
        validator.validate(friendRequest);
        friendRequest.setId(new Tuple<>(15L, -4L));
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(friendRequest));
        friendRequest.setId(new Tuple<>(-3L, 16L));
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(friendRequest));
        friendRequest.setId(new Tuple<>(-24L, -5L));
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(friendRequest));
        friendRequest.setId(new Tuple<>(17L, 17L));
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(friendRequest));
    }
}