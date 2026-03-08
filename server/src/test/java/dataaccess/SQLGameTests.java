package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SQLGameTests {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private int gameID;

    public SQLGameTests() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
        userDAO = new MySQLUserDAO();
        gameDAO = new MySQLGameDAO();
    }

    @BeforeEach
    public void resetTests() throws DataAccessException {
        authDAO.clear();
        userDAO.clear();
        gameDAO.clear();
        userDAO.createUser(new UserData("test", "testPassword", "testEmail"));
        authDAO.createAuth(new AuthData("test", "test"));
        gameID = gameDAO.createGame(new GameData(-1, null, null, "testGame", new ChessGame()));
    }

    @Test
    public void createGamePositiveTest() throws DataAccessException {
        GameData game = new GameData(-1, null, null, "game1", new ChessGame());
        int gameID = gameDAO.createGame(game);
        GameData result = gameDAO.getGame(gameID);
        GameData expected = new GameData(gameID, null, null, "game1", new ChessGame());
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void createGameNegativeTest() throws DataAccessException {
        GameData game = new GameData(-1, "player1", null, "game1", new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(game));
    }

    @Test
    public void getGamePositiveTest() throws DataAccessException {
        GameData expected = new GameData(gameID, null, null, "testGame", new ChessGame());
        GameData result = gameDAO.getGame(gameID);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getGameNegativeTest() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(3131));
    }

    @Test
    public void listGamesPositiveTest() throws DataAccessException {

    }

    @Test
    public void listGamesNegativeTest() throws DataAccessException {

    }

    @Test
    public void updateGamePositiveTest() throws DataAccessException {

    }

    @Test
    public void updateGameNegativeTest() throws DataAccessException {

    }

    @Test
    public void clearTest() throws DataAccessException {

    }
}
