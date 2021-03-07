package socialnetwork.domain.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;


class FriendshipValidatorTest {

    @Test
    void validate() {
        FriendshipValidator validator = new FriendshipValidator();
        Friendship friendship = new Friendship(24L, 17L);
        validator.validate(friendship);
        friendship.setId(new Tuple<>(15L, -4L));
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(friendship));
        friendship.setId(new Tuple<>(-3L, 16L));
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(friendship));
        friendship.setId(new Tuple<>(-24L, -5L));
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(friendship));
        friendship.setId(new Tuple<>(17L, 17L));
        Assertions.assertThrows(ValidationException.class, () -> validator.validate(friendship));
    }
}