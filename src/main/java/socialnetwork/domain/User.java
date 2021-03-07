package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;

/**
 *  class that defines an user
 */
public class User extends Entity<Long> {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private final List<User> friends = new ArrayList<>();

    /**
     *  constructor
     */
    public User() {
        this.setId(null);
    }

    /**
     *  constructor
     * @param ID - the ID of the user
     */
    public User(long ID) {
        this.setId(ID);
    }

    /**
     *  constructor
     * @param firstName - first name
     * @param lastName - last name
     */
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     *  constructor
     * @param username - username
     * @param password - password
     * @param firstName - first name
     * @param lastName - last name
     */
    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     *  constructor
     * @param username - username
     * @param password - password
     * @param firstName - first name
     * @param lastName - last name
     */
    public User(Long id, String username, String password, String firstName, String lastName) {
        this.setId(id);
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     *  constructor
     * @param ID - the ID of the user
     * @param firstName - first name
     * @param lastName - last name
     */
    public User(long ID, String firstName, String lastName) {
        this.setId(ID);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     *  constructor
     * @param ID - the ID of the user
     * @param firstName - first name
     * @param lastName - last name
     * @param username - username
     */
    public User(long ID, String firstName, String lastName, String username) {
        this.setId(ID);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    /**
     *  username getter
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     *  password getter
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     *  firstName getter
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *  firstName setter
     * @param firstName - the first name that will be saved
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *  lastName getter
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *  lastName setter
     * @param lastName - the last name that will be saved
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *  getter for the list of friends
     * @return the list of friends for the user
     */
    public List<User> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        friends.forEach(user -> s.append(" ").append(user.getId()));
        return "User{" +
                "ID='" + getId() + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + s +
                '}';
    }
}