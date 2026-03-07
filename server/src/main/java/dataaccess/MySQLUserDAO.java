package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLUserDAO implements UserDAO {
    public MySQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO users (username, passwordHash, email) values (?, ?, ?)";
        String passwordHash = hashPassword(userData.password());
        executeUpdate(statement, userData.username(), passwordHash, userData.email());
    }

    public UserData getUser(String username) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            var statement = "SELECT username, passwordHash, email FROM users WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Error: doesn't exist", 500);
                    }
                    return readUser(rs);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
    }

    public void clear() throws DataAccessException {
        String statement = "TRUNCATE users";
        executeUpdate(statement);
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

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case UserData p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()), 500);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `username` varchar(256) NOT NULL,
                `passwordHash` varchar(256) NOT NULL,
                `email` varchar(256),
                PRIMARY KEY (`username`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error configuring database: %s", ex.getMessage()), 500);
        }
    }
}
