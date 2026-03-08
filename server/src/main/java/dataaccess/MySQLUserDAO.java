package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class MySQLUserDAO implements UserDAO {
    public MySQLUserDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS users (
                `username` varchar(256) NOT NULL,
                `passwordHash` varchar(256) NOT NULL,
                `email` varchar(256),
                PRIMARY KEY (`username`)
            )
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    public void createUser(UserData userData) throws DataAccessException {
        try {
            getUser(userData.username());
        } catch (DataAccessException ex) {
            String statement = "INSERT INTO users (username, passwordHash, email) values (?, ?, ?)";
            String passwordHash = hashPassword(userData.password());
            DatabaseManager.executeUpdate(statement, userData.username(), passwordHash, userData.email());
            return;
        }
        throw new DataAccessException("Error: already taken", 403);
    }

    public UserData getUser(String username) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            var statement = "SELECT username, passwordHash, email FROM users WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Error: doesn't exist", 401);
                    }
                    return readUser(rs);
                }
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()), e.toHttpStatusCode());
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()), 500);
        }
    }

    public void clear() throws DataAccessException {
        String statement = "DELETE FROM users";
        DatabaseManager.executeUpdate(statement);
    }

    public boolean verifyUser(String username, String providedPassword) throws DataAccessException {
        UserData user = getUser(username);
        return BCrypt.checkpw(providedPassword, user.password());
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String passwordHash = rs.getString("passwordHash");
        String email = rs.getString("email");
        return new UserData(username, passwordHash, email);
    }

}
