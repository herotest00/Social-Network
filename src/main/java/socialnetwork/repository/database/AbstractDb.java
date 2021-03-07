package socialnetwork.repository.database;

import socialnetwork.domain.Entity;
import socialnetwork.repository.Repository;
import socialnetwork.repository.exceptions.RepoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractDb<ID, E extends Entity<ID>> implements Repository<ID,E> {

    Connection connection;

    public AbstractDb(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e)
        {
            throw new RepoException("Couldn't connect to the database!");
        }
    }
}
