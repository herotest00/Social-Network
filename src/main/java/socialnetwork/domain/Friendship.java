package socialnetwork.domain;


import java.time.LocalDate;

/**
 *  class that defines a friendship
 */
public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDate date;

    /**
     *  constructor
     * @param ID1 - ID of the first user
     * @param ID2 - ID of the second user
     */
    public Friendship(Long ID1, Long ID2) {
        this.date = LocalDate.now();
        setId(new Tuple<>(ID1, ID2));
    }

    /**
     *  constructor
     * @param ID1 - ID of the first user
     * @param ID2 - ID of the second user
     * @param date - date when the friendship was made
     */
    public Friendship(Long ID1, Long ID2, LocalDate date) {
        setId(new Tuple<>(ID1, ID2));
        this.date = date;
    }

    /**
     * date getter
     * @return the date when the friendship was created
     */
    public LocalDate getDate() {
        return date;
    }
}
