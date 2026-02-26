package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateGameTests {
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
        CreateGameRequest request = new CreateGameRequest("", "");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(request), "Error: bad request");
    }

    @Test
    public void emptyAuthTokenTest() {
        CreateGameRequest request = new CreateGameRequest("", "Foo's game");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(request), "Error: bad request");
    }

    @Test
    public void emptyGameNameTest() throws DataAccessException {
        String authToken = "jklsgeslgseg";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData(authToken, "test"));
        CreateGameRequest request = new CreateGameRequest(authToken, "");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(request), "Error: unauthorized");
    }

    @Test
    public void badAuthTokenTest() throws DataAccessException {
        String authToken = "jdsglssgesguuu";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData("notthisauthtoken", "test"));
        CreateGameRequest request = new CreateGameRequest(authToken, "game1");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(request), "Error: unauthorized");
    }

    @Test
    public void goodRequestTest() throws DataAccessException {
        String authToken = "euwiwogsg";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData(authToken, "test"));
        CreateGameRequest request = new CreateGameRequest(authToken, "mygame");
        CreateGameResult result = gameService.createGame(request);
        GameData game = gameDAO.getGame(result.gameID());
        GameData expected = new GameData(result.gameID(), null, null, "mygame", new ChessGame());
        Assertions.assertEquals(expected, game);
    }

    @Test
    public void identicalNameTest() throws DataAccessException {
        String authToken = "ioiosgs";
        GameData existingGame = new GameData(8, null, null, "mygame", new ChessGame());
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData(authToken, "test"));
        gameDAO.createGame(existingGame);
        CreateGameRequest request = new CreateGameRequest(authToken, "mygame");
        CreateGameResult result = gameService.createGame(request);
        GameData game = gameDAO.getGame(result.gameID());
        GameData expected = new GameData(result.gameID(), null, null, "mygame", new ChessGame());
        Assertions.assertEquals(expected, game);
        Assertions.assertEquals(existingGame, gameDAO.getGame(8));
    }
}
