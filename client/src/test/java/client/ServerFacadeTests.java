package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.ArrayList;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade serverFacade;
    private String testAuth;
    private int testGame;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("localhost", port);
    }

    @AfterAll
    static void stopServer() {
        serverFacade.clear();
        server.stop();
    }

    @BeforeEach
    public void resetServer() throws Exception {
        serverFacade.clear();
        UserData userData = new UserData("test1", "test1password", "test1email");
        testAuth = serverFacade.register(userData).authToken();
        userData = new UserData("test2", "test2password", "test2email");
        String auth2 = serverFacade.register(userData).authToken();
        serverFacade.logout(auth2);
        testGame = serverFacade.createGame(testAuth, "testGame");
        serverFacade.joinGame(testAuth, testGame, "WHITE");
    }

    @Test
    public void registerPositiveTest() throws Exception {
        UserData userData = new UserData("newUser", "123abc", "email@test.com");
        AuthData authData = serverFacade.register(userData);
        Assertions.assertEquals("newUser", authData.userName());
    }

    @Test
    public void registerNegativeTest() {
        UserData userData = new UserData("test2", "123abc", "email@test.com");
        Assertions.assertThrows(Exception.class, () -> serverFacade.register(userData));
    }

    @Test
    public void loginPositiveTest() throws Exception {
        AuthData authData = serverFacade.login("test2", "test2password");
        Assertions.assertEquals("test2", authData.userName());
    }

    @Test
    public void loginNegativeTest() {
        Assertions.assertThrows(Exception.class, () -> serverFacade.login("notAUser", "myPassword"));
    }

    @Test
    public void logoutPositiveTest() {
        serverFacade.logout(testAuth);
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout(testAuth));
    }

    @Test
    public void logoutNegativeTest() {
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout("notAnAuthToken"));
    }

    @Test
    public void listPositiveTest() {
        ArrayList<GameData> games = serverFacade.listGames(testAuth);
        Assertions.assertEquals(1, games.size());
        GameData game = games.getFirst();
        Assertions.assertEquals(testGame, game.gameID());
        Assertions.assertEquals("testGame", game.gameName());
        Assertions.assertNull(game.blackUsername());
        Assertions.assertEquals("test1", game.whiteUsername());
        Assertions.assertEquals(new ChessGame(), game.game());
    }

    @Test
    public void listNegativeTest() {
        Assertions.assertThrows(Exception.class, () -> serverFacade.listGames("notAnAuthToken"));
    }

    @Test
    public void createPositiveTest() {
        int gameID = serverFacade.createGame(testAuth, "newGame");
        ArrayList<GameData> games = serverFacade.listGames(testAuth);
        Assertions.assertEquals(2, games.size());
        GameData game = games.get(1);
        Assertions.assertEquals(gameID, game.gameID());
        Assertions.assertEquals("newGame", game.gameName());
        Assertions.assertNull(game.blackUsername());
        Assertions.assertNull(game.whiteUsername());
        Assertions.assertEquals(new ChessGame(), game.game());
    }

    @Test
    public void createNegativeTest() {
        Assertions.assertThrows(Exception.class, () -> serverFacade.createGame("notAnAuthToken", "newGame"));
    }

    @Test
    public void joinPositiveTest() throws Exception {
        String authToken = serverFacade.login("test2", "test2password").authToken();
        serverFacade.joinGame(authToken, testGame, "BLACK");
        ArrayList<GameData> games = serverFacade.listGames(testAuth);
        Assertions.assertEquals(1, games.size());
        GameData game = games.getFirst();
        Assertions.assertEquals(testGame, game.gameID());
        Assertions.assertEquals("testGame", game.gameName());
        Assertions.assertEquals("test2", game.blackUsername());
        Assertions.assertEquals("test1", game.whiteUsername());
        Assertions.assertEquals(new ChessGame(), game.game());
    }

    @Test
    public void joinNegativeTest() throws Exception {
        String authToken = serverFacade.login("test2", "test2password").authToken();
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(authToken, testGame, "WHITE"));
    }

    @Test
    public void clearTest() throws Exception {
        serverFacade.clear();
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout(testAuth));
        Assertions.assertThrows(Exception.class, () -> serverFacade.login("test2", "test2password"));
        UserData userData = new UserData("test", "test", "test");
        String auth = serverFacade.register(userData).authToken();
        ArrayList<GameData> games = serverFacade.listGames(auth);
        Assertions.assertEquals(0, games.size());
    }
}
