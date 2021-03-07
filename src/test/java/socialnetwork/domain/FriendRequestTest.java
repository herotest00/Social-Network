package socialnetwork.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.utils.FriendRequestStatuses;


class FriendRequestTest {

    @Test
    void getStatus() {
        FriendRequest friendRequest = new FriendRequest(4L, 6L);
        Assertions.assertEquals(friendRequest.getStatus(), FriendRequestStatuses.PENDING.name());
    }
}