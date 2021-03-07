package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.domain.User;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;


public class EventDb extends AbstractDb<Long, Event>{

    private final int pageSize = 6;

    public EventDb(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Event findOne(Long id) {
        try (PreparedStatement selectStatement = connection.prepareStatement(String.format("SELECT U.id, U.first_name, U.last_name, E.id AS id_event, E.title, E.description, E.start_time, E.end_time FROM Events AS E INNER JOIN Users AS U ON U.id = E.id_owner WHERE E.id = %d;", id));
            ResultSet resultSet = selectStatement.executeQuery()) {
            if (resultSet.next()) {
                long id_user = resultSet.getLong("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                User user = new User(id_user, first_name, last_name);
                long id_event = resultSet.getLong("id_event");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime start_time = resultSet.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end_time = resultSet.getTimestamp("end_time").toLocalDateTime();
                return new Event(id_event, user, title, description, start_time, end_time);
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return null;
    }

    /**
     * @param id - id of the event
     * @return all users attending to the event
     */
    public HashMap<Long, User> findAllForEvent(long id) {
        HashMap<Long, User> users = new HashMap<>();
        try (PreparedStatement selectStatement = connection.prepareStatement(String.format("SELECT U.id, U.first_name, U.last_name FROM UserEvent AS UE INNER JOIN Users AS U ON UE.id_user = U.id WHERE UE.id_event = %d;", id));
            ResultSet resultSet = selectStatement.executeQuery()) {
            while (resultSet.next()) {
                long id_user = resultSet.getLong("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                users.put(id_user, new User(id_user, first_name, last_name));
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return users;
    }

    /**
     * @param id - id of the user
     * @return all events the user is attending to
     */
    public HashMap<Long, Event> findAllForUser(long id) {
        HashMap<Long, Event> events = new HashMap<>();
        try (PreparedStatement selectStatement = connection.prepareStatement(String.format("SELECT U.id, U.first_name, U.last_name, E.id AS id_event, E.title, E.description, E.start_time, E.end_time, UE.notify FROM Events AS E INNER JOIN Users AS U ON E.id_owner = U.id INNER JOIN UserEvent AS UE ON UE.id_event = E.id WHERE UE.id_user = %d AND E.start_time > '%s';", id, LocalDateTime.now()));
             ResultSet resultSet = selectStatement.executeQuery()) {
            while (resultSet.next()) {
                long id_user = resultSet.getLong("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                User user = new User(id_user, first_name, last_name);
                long id_event = resultSet.getLong("id_event");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime start_time = resultSet.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end_time = resultSet.getTimestamp("end_time").toLocalDateTime();
                boolean notify = resultSet.getBoolean("notify");
                events.put(id_event, new Event(id_event, user, title, description, start_time, end_time, notify));
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return events;
    }

    /**
     * @param id - id of the user
     * @param pageIndex - page number
     * @return events of the specified page
     */
    public HashMap<Long, Event> findAllForUser(long id, long pageIndex) {
        HashMap<Long, Event> events = new HashMap<>();
        try (PreparedStatement selectStatement = connection.prepareStatement(String.format("SELECT U.id, U.first_name, U.last_name, E.id AS id_event, E.title, E.description, E.start_time, E.end_time, UE.notify FROM Events AS E INNER JOIN Users AS U ON E.id_owner = U.id INNER JOIN UserEvent AS UE ON UE.id_event = E.id WHERE UE.id_user = %d AND E.start_time > '%s' LIMIT %d OFFSET %d;", id, LocalDateTime.now(), pageSize, pageIndex * pageSize));
             ResultSet resultSet = selectStatement.executeQuery()) {
            while (resultSet.next()) {
                long id_user = resultSet.getLong("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                User user = new User(id_user, first_name, last_name);
                long id_event = resultSet.getLong("id_event");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime start_time = resultSet.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end_time = resultSet.getTimestamp("end_time").toLocalDateTime();
                boolean notify = resultSet.getBoolean("notify");
                events.put(id_event, new Event(id_event, user, title, description, start_time, end_time, notify));
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return events;
    }

    /**
     *  saves an user to an event
     * @param id_user - id of the user
     * @param id_event - id of the event
     */
    public void saveUserInEvent(long id_user, long id_event) {
        try (PreparedStatement saveStatement = connection.prepareStatement(String.format("INSERT INTO UserEvent (id_event, id_user) VALUES (%d, %d);", id_event, id_user))) {
            saveStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Already attending to this event!");
        }
    }

    @Override
    public HashMap<Long, Event> findAll() {
        HashMap<Long, Event> events = new HashMap<>();
        try (PreparedStatement selectStatement = connection.prepareStatement(String.format("SELECT U.id, U.first_name, U.last_name, E.id AS id_event, E.title, E.description, E.start_time, E.end_time FROM Events AS E INNER JOIN Users AS U ON E.id_owner = U.id WHERE E.start_time > '%s';", LocalDateTime.now()));
             ResultSet resultSet = selectStatement.executeQuery()) {
            while (resultSet.next()) {
                long id_user = resultSet.getLong("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                User user = new User(id_user, first_name, last_name);
                long id_event = resultSet.getLong("id_event");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime start_time = resultSet.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end_time = resultSet.getTimestamp("end_time").toLocalDateTime();
                events.put(id_event, new Event(id_event, user, title, description, start_time, end_time));
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return events;
    }

    /**
     * @param pageIndex - page number
     * @return all events of the specified page
     */
    public HashMap<Long, Event> findAll(long pageIndex) {
        HashMap<Long, Event> events = new HashMap<>();
        try (PreparedStatement selectStatement = connection.prepareStatement(String.format("SELECT U.id, U.first_name, U.last_name, E.id AS id_event, E.title, E.description, E.start_time, E.end_time FROM Events AS E INNER JOIN Users AS U ON E.id_owner = U.id WHERE E.start_time > '%s' LIMIT %d OFFSET %d;", LocalDateTime.now(), pageSize, pageIndex * pageSize));
             ResultSet resultSet = selectStatement.executeQuery()) {
            while (resultSet.next()) {
                long id_user = resultSet.getLong("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                User user = new User(id_user, first_name, last_name);
                long id_event = resultSet.getLong("id_event");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime start_time = resultSet.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end_time = resultSet.getTimestamp("end_time").toLocalDateTime();
                events.put(id_event, new Event(id_event, user, title, description, start_time, end_time));
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return events;
    }

    @Override
    public void save(Event event) {
        try (PreparedStatement saveStatement = connection.prepareStatement(String.format("INSERT INTO Events (id_owner, title, description, start_time, end_time) VALUES (%d, '%s', '%s', '%s', '%s') RETURNING id;", event.getUser().getId(), event.getTitle(), event.getDescription(), event.getStart_time(), event.getEnd_time()))) {
            saveStatement.execute();
            ResultSet resultSet = saveStatement.getResultSet();
            resultSet.next();
            long id_event = resultSet.getLong("id");
            saveUserInEvent(event.getUser().getId(), id_event);
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    /**
     * removes an user from the event
     * @param id_user - id of the user
     * @param id_event - id of the event
     */
    public void deleteUserFromEvent(long id_user, long id_event) {
        try (PreparedStatement deleteStatement = connection.prepareStatement(String.format("DELETE FROM UserEvent WHERE id_user = %d AND id_event = %d;", id_user, id_event))) {
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    /**
     *
     * @param id_user - id of the user
     * @param id_event - id of the event
     * @param notify_value - new notify status
     */
    public void updateNotifyForEvent(long id_user, long id_event, boolean notify_value) {
        try (PreparedStatement updateStatement = connection.prepareStatement(String.format("UPDATE UserEvent SET notify = %b WHERE id_user = %d AND id_event = %d;", notify_value, id_user, id_event))) {
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public void delete(Long id) {
        try (PreparedStatement deleteStatement = connection.prepareStatement(String.format("DELETE FROM Events WHERE id = %d;", id))) {
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public void update(Event event) {
        try (PreparedStatement updateStatement = connection.prepareStatement(String.format("UPDATE Events SET title = '%s', description = '%s' WHERE id = %d;", event.getTitle(), event.getDescription(), event.getId()))) {
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public int getSize() { return 0; }

    public int getNoPages() {
        try (PreparedStatement countStatement = connection.prepareStatement(String.format("SELECT COUNT(*) AS total FROM Events WHERE Events.start_time > '%s';", LocalDateTime.now()));
             ResultSet resultSet = countStatement.executeQuery()) {
            resultSet.next();
            int noOfPages = resultSet.getInt("total");
            if (noOfPages > 0 && noOfPages % pageSize == 0)
                return resultSet.getInt("total") / pageSize;
            return resultSet.getInt("total") / pageSize + 1;
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    public int getNoPagesForUser(long id) {
        try (PreparedStatement countStatement = connection.prepareStatement(String.format("SELECT COUNT(*) AS total FROM UserEvent AS UE INNER JOIN Events AS E ON UE.id_event = E.id AND E.start_time > '%s' WHERE UE.id_user = %d;", LocalDateTime.now(), id));
             ResultSet resultSet = countStatement.executeQuery()) {
            resultSet.next();
            int noOfPages = resultSet.getInt("total");
            if (noOfPages > 0 && noOfPages % pageSize == 0)
                return resultSet.getInt("total") / pageSize;
            return resultSet.getInt("total") / pageSize + 1;
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }
}
