package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

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
        GameData game1 = new GameData(-1, null, null, "game1", new ChessGame());
        int gameID1 = gameDAO.createGame(game1);
        game1 = new GameData(gameID1, null, null, "game1", new ChessGame());
        GameData game2 = gameDAO.getGame(gameID);
        Collection<GameData> expected = new ArrayList<>();
        expected.add(game2);
        expected.add(game1);
        Collection<GameData> result = gameDAO.listGames();
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void updateGamePositiveTest() throws DataAccessException, InvalidMoveException {
        ChessGame chessGame = new ChessGame();
        chessGame.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        GameData expected = new GameData(gameID, null, null, "betterGame", chessGame);
        gameDAO.updateGame(gameID, expected);
        GameData result = gameDAO.getGame(gameID);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void updateGameNegativeTest() throws DataAccessException {
        GameData newGame = new GameData(23432, null, null, "betterGame", new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(23432, newGame));
    }

    @Test
    public void clearTest() throws DataAccessException {
        gameDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(gameID));
    }
}
