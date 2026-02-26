package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearTests {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    public void resetTests() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(userDAO, gameDAO, authDAO);
    }

    @Test
    public void clearTest() throws DataAccessException {
        userDAO.createUser(new UserData("username1", "password1", "email1"));
        userDAO.createUser(new UserData("username2", "password2", "email2"));
        userDAO.createUser(new UserData("username3", "password3", "email3"));

        gameDAO.createGame(new GameData(0, null, "username3", "my game", new ChessGame()));
        gameDAO.createGame(new GameData(1, "username1", "username2", "finished", new ChessGame()));

        authDAO.createAuth(new AuthData("iewighsgdlsg", "username1"));
        authDAO.createAuth(new AuthData("osdfodsgdsgds", "username2"));

        gameService.clear();

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("username1"), "Error: does not exist");
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("username2"), "Error: does not exist");
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("username3"), "Error: does not exist");

        Assertions.assertEquals(0, gameDAO.listGames().size());

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("iewighsgdlsg"), "Error: unauthorized");
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("osdfodsgdsgds"), "Error: unauthorized");
    }
}
