package socialnetwork.repository.database;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;


public class FriendRequestDb extends AbstractDb<Tuple<Long, Long>, FriendRequest> {

    private final FriendRequestValidator validator;

    public FriendRequestDb(String url, String username, String password, FriendRequestValidator validator) {
        super(url, username, password);
        this.validator = validator;
    }

    @Override
    public FriendRequest findOne(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null");
        try (PreparedStatement getStatement = connection.prepareStatement(String.format("SELECT * FROM FriendRequests WHERE id_user1 = %d AND id_user2 = %d;", id.getLeft(), id.getRight()));
             ResultSet resultSet = getStatement.executeQuery()) {
            if (resultSet.next()) {
                Long id1 = resultSet.getLong("id_user1");
                Long id2 = resultSet.getLong("id_user2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                FriendRequest friendRequest = new FriendRequest(id1, id2, date);
                validator.validate(friendRequest);
                return friendRequest;
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return null;
    }

    @Override
    public HashMap<Tuple<Long, Long>, FriendRequest> findAll() {
        HashMap<Tuple<Long, Long>, FriendRequest> friendRequests = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM FriendRequests;");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id_user1");
                Long id2 = resultSet.getLong("id_user2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                FriendRequest friendRequest = new FriendRequest(id1, id2, date);
                validator.validate(friendRequest);
                friendRequests.put(new Tuple<>(id1, id2), friendRequest);
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return friendRequests;
    }

    @Override
    public void save(FriendRequest friendRequest) {
        validator.validate(friendRequest);
        try (PreparedStatement saveStatement = connection.prepareStatement(String.format("INSERT INTO FriendRequests VALUES (%d, %d, '%s');", friendRequest.getId().getLeft(), friendRequest.getId().getRight(), friendRequest.getDate()))) {
            saveStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Friend request already exists!");
        }
    }

    @Override
    public void delete(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null.");
        try (PreparedStatement deleteStatement = connection.prepareStatement(String.format("DELETE FROM FriendRequests WHERE id_user1 = %d AND id_user2 = %d;", id.getLeft(), id.getRight()))) {
             deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public void update(FriendRequest friendRequest) {

    }

    @Override
    public int getSize() {
        try (PreparedStatement sizeStatement = connection.prepareStatement("SELECT COUNT(*) AS total FROM FriendRequests;");
             ResultSet resultSet = sizeStatement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt("total");
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }
}
