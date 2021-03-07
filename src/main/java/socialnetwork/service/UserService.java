package socialnetwork.service;

import socialnetwork.domain.User;
import socialnetwork.repository.database.UserDb;
import socialnetwork.repository.exceptions.RepoException;

import java.util.HashMap;


/**
 *  service for users
 */
public class UserService {

    private final UserDb userRepository;

    /**
     *  constructor
     * @param userRepository - repo of type Repository
     */
    public UserService(UserDb userRepository) {
        this.userRepository = userRepository;
    }

    /**
     *  saves the user
     * @param username - username of the account
     * @param password - password of the account
     * @param firstName - first name of the user
     * @param lastName - last name of the user
     * @throws RepoException - if the username is already in use
     */
    public void addUser(String username, String password, String firstName, String lastName) throws RepoException {
        userRepository.save(new User(username, password, firstName, lastName));
    }

    /**
     *  removes the user
     * @param id - id of the user that will be removed
     * @throws RepoException - if a database error occurs
     */
    public void removeUser(long id) throws RepoException {
        userRepository.delete(id);
    }

    /**
     *  getter for the users
     * @return a map with all users
     * @throws RepoException - if a database error occurs
     */
    public HashMap<Long, User> findAll() throws RepoException {
        return userRepository.findAll();
    }

    /**
     *  getter for user
     * @param id - id of the user
     * @return user with id
     * @throws RepoException - if a database error occurs
     */
    public User findOne(Long id) throws RepoException {
        return userRepository.findOne(id);
    }

    /**
     *  getter for account
     * @param username - username of the account
     * @return the user of the account
     * @throws RepoException - if a database error occurs
     */
    public User findAccount(String username, String password) throws RepoException {
        return userRepository.findAccount(username, password);
    }
}
