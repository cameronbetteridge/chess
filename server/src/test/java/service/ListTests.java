package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListTests {
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
        ListRequest request = new ListRequest("");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.list(request), "Error: bad request");
    }

    @Test
    public void badAuthTokenTest() throws DataAccessException {
        String authToken = "jdsglssgesguuu";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData("notthisauthtoken", "test"));
        ListRequest request = new ListRequest(authToken);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.list(request), "Error: unauthorized");
    }

    @Test
    public void noGamesTest() throws DataAccessException {
        String authToken = "euwiwogsg";
        userDAO.createUser(new UserData("test", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData(authToken, "test"));
        ListRequest request = new ListRequest(authToken);
        ListResult result = gameService.list(request);
        Assertions.assertEquals(0, result.games().size());
    }

    @Test
    public void threeGamesTest() throws DataAccessException {
        String authToken = "yyyyygsgdsg";
        GameData game1 = new GameData(1, null, "tester", "game1", new ChessGame());
        GameData game2 = new GameData(2, "tester", null, "game2", new ChessGame());
        GameData game3 = new GameData(3, null, null, "game3", new ChessGame());

        userDAO.createUser(new UserData("tester", "password", "email@hi.us"));
        authDAO.createAuth(new AuthData(authToken, "tester"));
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        gameDAO.createGame(game3);

        GameDataList gameData1 = new GameDataList(game1.gameID(), game1.whiteUsername(), game1.blackUsername(), game1.gameName());
        GameDataList gameData2 = new GameDataList(game2.gameID(), game2.whiteUsername(), game2.blackUsername(), game2.gameName());
        GameDataList gameData3 = new GameDataList(game3.gameID(), game3.whiteUsername(), game3.blackUsername(), game3.gameName());

        ListRequest request = new ListRequest(authToken);
        Iterator<GameDataList> games = gameService.list(request).games().iterator();

        Assertions.assertEquals(gameData1, games.next());
        Assertions.assertEquals(gameData2, games.next());
        Assertions.assertEquals(gameData3, games.next());
        Assertions.assertThrows(NoSuchElementException.class, games::next);
    }
}
