package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;


public class FriendshipDb extends AbstractDb<Tuple<Long, Long>, Friendship> {

    private final FriendshipValidator validator;

    public FriendshipDb(String url, String username, String password, FriendshipValidator validator) {
        super(url, username, password);
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null");
        try (PreparedStatement getStatement = connection.prepareStatement(String.format("SELECT * FROM Friendships WHERE (id_user1 = %d AND id_user2 = %d) OR (id_user1 = %d AND id_user2 = %d)", id.getLeft(), id.getRight(), id.getRight(), id.getLeft()));
             ResultSet resultSet = getStatement.executeQuery()) {
            if (resultSet.next()) {
                Long id1 = resultSet.getLong("id_user1");
                Long id2 = resultSet.getLong("id_user2");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Friendship friendship = new Friendship(id1, id2, date);
                validator.validate(friendship);
                return friendship;
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return null;
    }

    @Override
    public HashMap<Tuple<Long, Long>, Friendship> findAll() {
        HashMap<Tuple<Long, Long>, Friendship> friendships = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Friendships;");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id_user1");
                Long id2 = resultSet.getLong("id_user2");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Friendship friendship = new Friendship(id1, id2, date);
                validator.validate(friendship);
                friendships.put(new Tuple<>(id1, id2), friendship);
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return friendships;
    }

    @Override
    public void save(Friendship friendship) {
        validator.validate(friendship);
        try (PreparedStatement selectStatement = connection.prepareStatement(String.format("SELECT * FROM Friendships WHERE (id_user1 = %d AND id_user2 = %d) OR (id_user1 = %d AND id_user2 = %d)", friendship.getId().getLeft(), friendship.getId().getRight(), friendship.getId().getRight(), friendship.getId().getLeft()));
             PreparedStatement saveStatement = connection.prepareStatement(String.format("INSERT INTO Friendships VALUES (%d, %d, '%s');", friendship.getId().getLeft(), friendship.getId().getRight(), friendship.getDate()))) {
            if (selectStatement.executeQuery().next())
                throw new RepoException("Friendship already exists!");
            saveStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public void delete(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null.");
        try (PreparedStatement deleteStatement = connection.prepareStatement(String.format("DELETE FROM Friendships WHERE (id_user1 = %d AND id_user2 = %d) OR (id_user1 = %d AND id_user2 = %d);", id.getLeft(), id.getRight(), id.getRight(), id.getLeft()))) {
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public void update(Friendship friendship) {
    }

    @Override
    public int getSize() {
        try (PreparedStatement sizeStatement = connection.prepareStatement("SELECT COUNT(*) AS total FROM Friendships;");
             ResultSet resultSet = sizeStatement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt("total");
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }
}
