package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLGameDAO implements GameDAO {
    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public int createGame(GameData gameData) throws DataAccessException {
        String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String gameJson = new Gson().toJson(gameData.game());
        return DatabaseManager.executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameJson);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Error: doesn't exist", 400);
                    }
                    return readGame(rs);
                }
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()), e.toHttpStatusCode());
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()), 500);
        }
    }

    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()), 401);
        }
        return result;
    }

    public void updateGame(int gameID, GameData newGame) throws DataAccessException {
        getGame(gameID);
        String statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE id = ?";
        String gameJson = new Gson().toJson(newGame.game());
        DatabaseManager.executeUpdate(statement, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), gameJson, gameID);
    }

    public void clear() throws DataAccessException {
        String statement = "DELETE FROM games";
        DatabaseManager.executeUpdate(statement);
        statement = "AlTER TABLE games AUTO_INCREMENT = 1";
        DatabaseManager.executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("id");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameJson = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
                `id` int NOT NULL AUTO_INCREMENT,
                `whiteUsername` varchar(256),
                `blackUsername` varchar(256),
                `gameName` varchar(256) NOT NULL,
                `game` varchar(2000) NOT NULL,
                PRIMARY KEY (`id`),
                FOREIGN KEY (`whiteUsername`) REFERENCES users(`username`) ON DELETE CASCADE,
                FOREIGN KEY (`blackUsername`) REFERENCES users(`username`) ON DELETE CASCADE
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
