package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLAuthDAO implements AuthDAO {
    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auths (authToken, username) values (?, ?)";
        executeUpdate(statement, authData.authToken(), authData.userName());
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths WHERE authToken = ?";
            try (PreparedStatement ps = connection.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Error: doesn't exist", 500);
                    }
                    return readAuth(rs);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
    }

    public void deleteAuth(AuthData authData) throws DataAccessException {
        getAuth(authData.authToken());
        String statement = "DELETE FROM auths WHERE authToken = ? AND username = ?";
        executeUpdate(statement, authData.authToken(), authData.userName());
    }

    public void clear() throws DataAccessException {
        String statement = "DELETE FROM auths";
        executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case AuthData p -> ps.setString(i + 1, p.toString());
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
            CREATE TABLE IF NOT EXISTS auths (
                `authToken` varchar(256) NOT NULL,
                `username` varchar(256) NOT NULL,
                PRIMARY KEY (`authToken`),
                FOREIGN KEY (`username`) REFERENCES users(`username`) ON DELETE CASCADE
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
