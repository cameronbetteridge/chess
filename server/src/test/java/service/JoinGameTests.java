package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinGameTests {
    private GameService gameService;
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void resetTests() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(userDAO, gameDAO, authDAO);
    }

    @Test
    public void emptyRequestTest() {
        JoinGameRequest request = new JoinGameRequest("", "", 0);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(request), "Error: bad request");
    }

    @Test
    public void emptyAuthTokenTest() {
        JoinGameRequest request = new JoinGameRequest("", "WHITE", 0);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(request), "Error: bad request");
    }

    @Test
    public void emptyPlayerColorTest() throws DataAccessException {
        String authToken = "jklsgeslgseg";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData(authToken, "test"));
        JoinGameRequest request = new JoinGameRequest(authToken, "", 0);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(request), "Error: unauthorized");
    }

    @Test
    public void badAuthTokenTest() throws DataAccessException {
        String authToken = "jdsglssgesguuu";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData("notthisauthtoken", "test"));
        gameDAO.createGame(new GameData(0, null, null, "game1", new ChessGame()));
        JoinGameRequest request = new JoinGameRequest(authToken, "WHITE", 0);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(request), "Error: unauthorized");
    }

    @Test
    public void gameDoesNotExistTest() throws DataAccessException {
        String authToken = "iiiisdhsgs";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData(authToken, "test"));
        gameDAO.createGame(new GameData(0, null, null, "game1", new ChessGame()));
        JoinGameRequest request = new JoinGameRequest(authToken, "WHITE", 1);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(request), "Error: game doesn't exist");
    }

    @Test
    public void spotAlreadyTakenTest() throws DataAccessException {
        String authToken = "iiiisdhsgs";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        userDAO.createUser(new UserData("guy2", "helloworld", "hi@bye.com"));
        authDAO.createAuth(new AuthData(authToken, "test"));
        gameDAO.createGame(new GameData(0, "guy2", null, "game1", new ChessGame()));
        JoinGameRequest request = new JoinGameRequest(authToken, "WHITE", 0);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(request), "Error: already taken");
    }

    @Test
    public void goodRequestTest() throws DataAccessException {
        String authToken = "iiiisdhsgs";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData(authToken, "test"));
        ChessGame chessGame = new ChessGame();
        gameDAO.createGame(new GameData(0, null, null, "game1", chessGame));
        JoinGameRequest request = new JoinGameRequest(authToken, "WHITE", 0);
        gameService.joinGame(request);

        GameData game = gameDAO.getGame(0);
        GameData expected = new GameData(0, "test", null, "game1", chessGame);
        Assertions.assertEquals(expected, game);
    }
}
