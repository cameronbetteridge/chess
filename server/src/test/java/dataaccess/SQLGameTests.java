package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SQLGameTests {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

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
        gameDAO.createGame(new GameData(-1, null, null, "testGame", new ChessGame()));
    }

    @Test
    public void createGamePositiveTest() {

    }

    @Test
    public void createGameNegativeTest() {

    }

    @Test
    public void getGamePositiveTest() {

    }

    @Test
    public void getGameNegativeTest() {

    }

    @Test
    public void listGamesPositiveTest() {

    }

    @Test
    public void listGamesNegativeTest() {

    }

    @Test
    public void updateGamePositiveTest() {

    }

    @Test
    public void updateGameNegativeTest() {

    }

    @Test
    public void clearTest() {

    }
}
