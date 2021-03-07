package socialnetwork.repository.database;

import socialnetwork.domain.Group;
import socialnetwork.domain.User;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class GroupDb extends AbstractDb<Long, Group> {

    public GroupDb(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Group findOne(Long id) {
        try (PreparedStatement selectGStatement = connection.prepareStatement(String.format("SELECT id, g_name FROM Groups WHERE id = %d;", id));
             PreparedStatement selectUGStatement = connection.prepareStatement(String.format("SELECT U.id, U.first_name, U.last_name FROM Users AS U INNER JOIN UserGroupTo AS UG ON U.id = UG.id_user INNER JOIN Groups AS G ON UG.id_group = %d", id));
             ResultSet resultGSet = selectGStatement.executeQuery();
             ResultSet resultUGSet = selectUGStatement.executeQuery()) {
            if (resultGSet.next()) {
                long id_group = resultGSet.getLong("id");
                String name = resultGSet.getString("g_name");
                Group group = new Group(id_group, name);
                while (resultUGSet.next()) {
                    long id_user = resultUGSet.getLong("id");
                    String first_name = resultUGSet.getString("first_name");
                    String last_name = resultUGSet.getString("last_name");
                    group.getUsers().add(new User(id_user, first_name, last_name));
                }
                return group;
            }
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
        return null;
    }

    /**
     *  finds the groups that contain an user
     * @param id - id of the user
     * @return the groups that contain the user
     */
    public HashMap<Long, Group> findAllGroupsForUser(Long id) {
        HashMap<Long, Group> groups = new HashMap<>();
        try (PreparedStatement selectGStatement = connection.prepareStatement(String.format("SELECT DISTINCT G.id, G.g_name FROM Groups AS G INNER JOIN UserGroupTo AS UG ON G.id = UG.id_group WHERE UG.id_user = %d", id));
             ResultSet resultGSet = selectGStatement.executeQuery()) {
            while (resultGSet.next()) {
                long id_group = resultGSet.getLong("id");
                String name = resultGSet.getString("g_name");
                groups.put(id_group, new Group(id_group, name));
            }
            for (Group group : groups.values()) {
                PreparedStatement selectUStatement = connection.prepareStatement(String.format("SELECT U.id, U.first_name, U.last_name FROM Users AS U INNER JOIN UserGroupTo AS UG ON U.id = UG.id_user WHERE UG.id_group = %d", group.getId()));
                ResultSet resultUSet = selectUStatement.executeQuery();
                while (resultUSet.next()) {
                    long id_user = resultUSet.getLong("id");
                    String first_name = resultUSet.getString("first_name");
                    String last_name = resultUSet.getString("last_name");
                    groups.get(group.getId()).getUsers().add(new User(id_user, first_name, last_name));
                }
                selectUStatement.close();
                resultUSet.close();
            }
            return  groups;
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public HashMap<Long, Group> findAll() {
        return null;
    }

    @Override
    public void save(Group group) {
        try (PreparedStatement saveStatement = connection.prepareStatement(String.format("INSERT INTO Groups (g_name) VALUES ('%s') RETURNING id;", group.getName()));
             ResultSet resultSet = saveStatement.executeQuery()) {
            resultSet.next();
            Long id_group = resultSet.getLong("id");
            String sql = "INSERT INTO UserGroupTo VALUES ";
            for (User user : group.getUsers())
                sql = sql.concat(String.format("(%d, %d), ", user.getId(), id_group));
            sql = sql.substring(0, sql.length() - 2);
            PreparedStatement saveUGStatement = connection.prepareStatement(sql);
            saveUGStatement.executeUpdate();
            saveUGStatement.close();
        } catch (SQLException e) {
            throw new RepoException("Database error!");
        }
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void update(Group group) {
        if (group.getName() != null)
        {
            try (PreparedStatement updateStatement = connection.prepareStatement(String.format("UPDATE Groups SET g_name = '%s' WHERE id = %d;", group.getName(), group.getId()))) {
                updateStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RepoException("Database error!");
            }
        }
        if (group.getUsers().size() != 0) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(String.format("DELETE FROM UserGroupTo WHERE id_group = %d;", group.getId()))) {
                deleteStatement.executeUpdate();
                String sql = "INSERT INTO UserGroupTo VALUES ";
                for (User user : group.getUsers())
                    sql = sql.concat(String.format("(%d, %d), ", user.getId(), group.getId()));
                sql = sql.substring(0, sql.length() - 2);
                PreparedStatement saveUGStatement = connection.prepareStatement(sql);
                saveUGStatement.executeUpdate();
                saveUGStatement.close();
            } catch (SQLException e) {
                throw new RepoException("Database error!");
            }
        }
    }

    @Override
    public int getSize() {
        return 0;
    }
}
