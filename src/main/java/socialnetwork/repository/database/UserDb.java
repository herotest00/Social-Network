package socialnetwork.repository.database;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.utils.AES;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class UserDb extends AbstractDb<Long, User> {

    private final Validator<User> validator;

    public UserDb(String url, String username, String password, Validator<User> validator) {
        super(url, username, password);
        this.validator = validator;
    }

    /**
     *  finds the account of an user
     * @param username - username of the account
     * @param password - password of the account
     * @return the user with the given account
     */
    public User findAccount(String username, String password) {
        if(username == null)
            throw new IllegalArgumentException("Username must be not null");
        try (PreparedStatement statement = connection.prepareStatement("SELECT id, first_name, last_name, username FROM Users WHERE username = ? AND password = ?;")) {
            statement.setString(1, username);
            statement.setBytes(2, AES.encrypt(password, username));
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                throw new RepoException("Invalid username/password!");
            long id_user = resultSet.getLong("id");
            String first_name = resultSet.getString("first_name");
            String last_name = resultSet.getString("last_name");
            String username1 = resultSet.getString("username");
            User user = new User(id_user, first_name, last_name, username1);
            validator.validate(user);
            return user;
        } catch (SQLException exception) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public User findOne(Long id) {
        if(id == null)
            throw new IllegalArgumentException("ID must be not null");
        try(PreparedStatement statement = connection.prepareStatement(String.format("SELECT id, first_name, last_name FROM Users WHERE id = %d", id));
            ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next())
            {
                long id_user = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(id_user, firstName, lastName);
                validator.validate(user);
                return user;
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return null;
    }

    @Override
    public HashMap<Long, User> findAll() {
        HashMap<Long, User> users = new HashMap<>();
        try (PreparedStatement getUsersStatement = connection.prepareStatement("SELECT id, first_name, last_name FROM Users;");
            ResultSet resultSetUsers = getUsersStatement.executeQuery()) {
            while (resultSetUsers.next()) {
                long id = resultSetUsers.getLong("id");
                String firstName = resultSetUsers.getString("first_name");
                String lastName = resultSetUsers.getString("last_name");
                User user = new User(id, firstName, lastName);
                validator.validate(user);
                users.put(id, user);
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return users;
    }

    /**
     *  retrieves all users with their friends list
     * @return all users
     */
    public HashMap<Long, User> findAllWithFriends() {
        HashMap<Long, User> users = new HashMap<>();
        try (PreparedStatement getUsersStatement = connection.prepareStatement("SELECT id, first_name, last_name FROM Users;");
             PreparedStatement getFriendshipsStatement = connection.prepareStatement("SELECT id_user1, id_user2 FROM Friendships;");
             ResultSet resultSetUsers = getUsersStatement.executeQuery();
             ResultSet resultSetFriendships = getFriendshipsStatement.executeQuery()) {
            while (resultSetUsers.next()) {
                long id = resultSetUsers.getLong("id");
                String firstName = resultSetUsers.getString("first_name");
                String lastName = resultSetUsers.getString("last_name");
                User user = new User(id, firstName, lastName);
                validator.validate(user);
                users.put(id, user);
            }
            while (resultSetFriendships.next()) {
                long id1 = resultSetFriendships.getLong("id_user1");
                long id2 = resultSetFriendships.getLong("id_user2");
                users.get(id1).getFriends().add(users.get(id2));
                users.get(id2).getFriends().add(users.get(id1));
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return users;
    }

    /**
     *  retrieves all users with their friends list
     * @param id - id of the user
     * @return all users
     */
    public User findOneWithFriends(Long id) {
        try (PreparedStatement getUsersStatement = connection.prepareStatement(String.format("SELECT id, first_name, last_name FROM Users WHERE id = %d;", id));
             PreparedStatement getFriendshipsStatement = connection.prepareStatement(String.format("SELECT id_user1, id_user2 FROM Friendships WHERE id_user1 = %d OR id_user2 = %d;", id, id));
             ResultSet resultSetUser = getUsersStatement.executeQuery();
             ResultSet resultSetFriendships = getFriendshipsStatement.executeQuery()) {
            if(!resultSetUser.next())
                throw new RepoException("No user found!");
            long id_user = resultSetUser.getLong("id");
            String firstName = resultSetUser.getString("first_name");
            String lastName = resultSetUser.getString("last_name");
            User user = new User(id_user, firstName, lastName);
            validator.validate(user);
            while (resultSetFriendships.next()) {
                long id1 = resultSetFriendships.getLong("id_user1");
                long id2 = resultSetFriendships.getLong("id_user2");
                if (id1 == id)
                    user.getFriends().add(findOne(id2));
                else
                    user.getFriends().add(findOne(id1));
            }
            return user;
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public void save(User user) {
        validator.validate(user);
        try (PreparedStatement saveStatement = connection.prepareStatement("INSERT INTO Users (first_name, last_name, username, password) VALUES (?, ?, ?, ?);")) {
            saveStatement.setString(1, user.getFirstName());
            saveStatement.setString(2, user.getLastName());
            saveStatement.setString(3, user.getUsername());
            saveStatement.setBytes(4, AES.encrypt(user.getPassword(), user.getUsername()));
            saveStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Username already exists!");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null.");
        try (PreparedStatement deleteStatement = connection.prepareStatement(String.format("DELETE FROM Users WHERE Users.id = %d;", id))) {
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public void update(User user) { }

    @Override
    public int getSize() {
        try (PreparedStatement sizeStatement = connection.prepareStatement("SELECT COUNT(*) AS total FROM Users;");
             ResultSet resultSet = sizeStatement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt("total");
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }
}
