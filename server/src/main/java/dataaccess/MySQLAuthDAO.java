package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {
    public MySQLAuthDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auths (
                `authToken` varchar(256) NOT NULL,
                `username` varchar(256) NOT NULL,
                PRIMARY KEY (`authToken`),
                FOREIGN KEY (`username`) REFERENCES users(`username`) ON DELETE CASCADE
            )
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auths (authToken, username) values (?, ?)";
        DatabaseManager.executeUpdate(statement, authData.authToken(), authData.userName());
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths WHERE authToken = ?";
            try (PreparedStatement ps = connection.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Error: doesn't exist", 401);
                    }
                    return readAuth(rs);
                }
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()), e.toHttpStatusCode());
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()), 500);
        }
    }

    public void deleteAuth(AuthData authData) throws DataAccessException {
        getAuth(authData.authToken());
        String statement = "DELETE FROM auths WHERE authToken = ? AND username = ?";
        DatabaseManager.executeUpdate(statement, authData.authToken(), authData.userName());
    }

    public void clear() throws DataAccessException {
        String statement = "DELETE FROM auths";
        DatabaseManager.executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);
    }

}
