package socialnetwork.domain;

import java.time.LocalDate;

/**
 *  class for filtering purposes
 */
public class FriendDTO {

    private final long ID;
    private final String firstName;
    private final String lastName;
    LocalDate date;

    /**
     *  constructor
     * @param ID - id of the user
     * @param firstName - first name of the user
     * @param lastName - last name of the user
     * @param date - date of the friendship
     */
    public FriendDTO(long ID, String firstName, String lastName, LocalDate date) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    /**
     *  id getter
     * @return id of the friend
     */
    public long getID() {
        return ID;
    }

    /**
     *  first name getter
     * @return first name of the friend
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *  last name getter
     * @return last name of the friend
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *  date getter
     * @return date when the friendship was made
     */
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s", firstName, lastName, date);
    }
}
