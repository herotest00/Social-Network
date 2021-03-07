package socialnetwork.domain;

import socialnetwork.utils.FriendRequestStatuses;

import java.time.LocalDateTime;

/**
 *  class that defines a friend request
 */
public class FriendRequest extends Entity<Tuple<Long, Long>> {

    private final String status = FriendRequestStatuses.PENDING.name();
    private final LocalDateTime date;

    /**
     *  constructor
     * @param ID1 - ID of the first user
     * @param ID2 - ID of the second user
     */
    public FriendRequest(Long ID1, Long ID2) {
        super.setId(new Tuple<>(ID1, ID2));
        this.date = LocalDateTime.now();
    }

    /**
     *  constructor
     * @param ID1 - ID of the first user
     * @param ID2 - ID of the second user
     * @param date - date when the friend request was sent
     */
    public FriendRequest(Long ID1, Long ID2, LocalDateTime date) {
        super.setId(new Tuple<>(ID1, ID2));
        this.date = date;
    }

    /**
     *  status getter
     * @return the status of the friend request
     */
    public String getStatus() {
        return status;
    }

    /**
     *  date getter
     * @return the date when the friend request was sent
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}
