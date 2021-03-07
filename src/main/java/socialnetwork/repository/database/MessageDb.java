package socialnetwork.repository.database;

import socialnetwork.domain.Group;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;


public class MessageDb extends AbstractDb<Long, Message> {

    private final MessageValidator validator;

    public MessageDb(String url, String username, String password, MessageValidator validator) {
        super(url, username, password);
        this.validator = validator;
    }

    @Override
    public Message findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null");
        try (PreparedStatement selectMessageStatement = connection.prepareStatement(String.format("SELECT id, msg, date, reply FROM Messages WHERE id = %d;", id));
             PreparedStatement selectFromStatement = connection.prepareStatement(String.format("SELECT U.id, U.first_name, U.last_name FROM Users AS U INNER JOIN Messages AS M ON U.id = (SELECT fromu FROM Messages WHERE id = %d);", id));
             PreparedStatement selectToStatement = connection.prepareStatement(String.format("SELECT G.id, G.g_name FROM Groups AS G INNER JOIN Messages AS M ON G.id = M.tou WHERE M.id = %d", id));
             ResultSet resultMessage = selectMessageStatement.executeQuery();
             ResultSet resultToGroup = selectToStatement.executeQuery();
             ResultSet resultFromUser = selectFromStatement.executeQuery()){
            Message message;
            Group group;
            User user;
            if (resultToGroup.next()) {
                long id_group = resultToGroup.getLong("id");
                String name = resultToGroup.getString("g_name");
                group = new Group(id_group, name);
            } else return null;

            if (resultFromUser.next()) {
                long id_user = resultFromUser.getLong("id");
                String firstName = resultFromUser.getString("first_name");
                String lastName = resultFromUser.getString("last_name");
                user = new User(id_user, firstName, lastName);
            } else return null;

            if (resultMessage.next()) {
                long id_message = resultMessage.getLong("id");
                String string = resultMessage.getString("msg");
                LocalDateTime date = resultMessage.getTimestamp("date").toLocalDateTime();
                message = new Message(id_message, user, group, string, date);
                long id_reply = resultMessage.getLong("reply");
                if (!resultMessage.wasNull()) {
                    message.setReply(new Message(id_reply));
                }
                else
                    message.setReply(null);
            } else return null;
            return message;
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public HashMap<Long, Message> findAll() {
        return null;
    }

    /**
     *  finds all messages from a group
     * @param id_group - id of the group
     * @return all messages from a group
     */
    public HashMap<Long, Message> findAllMessagesForGroup(Long id_group) {
        HashMap<Long, Message> messages = new HashMap<>();
        try(PreparedStatement selectGroupStatement = connection.prepareStatement(String.format("SELECT id, g_name FROM Groups WHERE id = %d", id_group));
            PreparedStatement selectMessagesStatement = connection.prepareStatement(String.format("SELECT id, fromu, msg, date, reply FROM Messages WHERE tou = %d;", id_group));
            ResultSet resultSetGroup = selectGroupStatement.executeQuery();
            ResultSet resultSetMessages = selectMessagesStatement.executeQuery()){
            Group group;
            if (resultSetGroup.next()) {
                long id_user = resultSetGroup.getLong("id");
                String g_name = resultSetGroup.getString("g_name");
                group = new Group(id_user, g_name);
            } else return null;

            while (resultSetMessages.next()) {
                long id_message = resultSetMessages.getLong("id");
                String msg = resultSetMessages.getString("msg");
                long id_user = resultSetMessages.getLong("fromu");
                User user;
                PreparedStatement selectFromStatement = connection.prepareStatement(String.format("SELECT id, first_name, last_name FROM Users WHERE id = %d", id_user));
                ResultSet resultSet = selectFromStatement.executeQuery();
                    if (resultSet.next()) {
                        long id = resultSet.getLong("id");
                        String first_name = resultSet.getString("first_name");
                        String last_name = resultSet.getString("last_name");
                        user = new User(id, first_name, last_name);
                    } else return null;
                selectFromStatement.close();
                resultSet.close();

                LocalDateTime date = resultSetMessages.getTimestamp("date").toLocalDateTime();
                Message message = new Message(id_message, user, group, msg, date);
                long id_reply = resultSetMessages.getLong("reply");
                if (!resultSetMessages.wasNull()) {
                    PreparedStatement selectReplyStatement = connection.prepareStatement(String.format("SELECT msg FROM Messages WHERE id = %d;", id_reply));
                    ResultSet resultReplySet = selectReplyStatement.executeQuery();
                    resultReplySet.next();
                    String msg_reply = resultReplySet.getString("msg");
                    message.setReply(new Message(id_reply, msg_reply));
                    selectReplyStatement.close();
                    resultReplySet.close();
                }
                else
                    message.setReply(null);
                messages.put(id_message, message);
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return messages;
    }

    @Override
    public void save(Message message) {
        validator.validate(message);
        try (PreparedStatement saveMessageStatement = connection.prepareStatement(String.format("INSERT INTO Messages (fromu, tou, msg, date) VALUES (%d, %d, '%s', '%s') RETURNING id;", message.getFrom().getId(), message.getTo().getId(), message.getMessage(), message.getDate()))) {
            saveMessageStatement.execute();
            ResultSet resultSet = saveMessageStatement.getResultSet();
            resultSet.next();
            long id_message = resultSet.getLong("id");
            resultSet.close();
            if (message.getReply() != null) {
                PreparedStatement statement = connection.prepareStatement(String.format("UPDATE Messages SET reply = %d WHERE id = %d;", message.getReply().getId(), id_message));
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public void delete(Long id) {
    }

    @Override
    public void update(Message message) {
    }

    @Override
    public int getSize() {
        try (PreparedStatement sizeStatement = connection.prepareStatement("SELECT COUNT(*) AS total FROM Messages;");
             ResultSet resultSet = sizeStatement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt("total");
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }
}
