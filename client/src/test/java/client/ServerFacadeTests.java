package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;


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
    public void resetServer() {
        serverFacade.clear();
        testAuth = serverFacade.register("test1", "test1password", "test1email").authToken();
        String auth2 = serverFacade.register("test2", "test2password", "test2email").authToken();
        serverFacade.logout(auth2);
        testGame = serverFacade.createGame(testAuth, "testGame");
        serverFacade.joinGame(testAuth, testGame, "WHITE");
    }

    @Test
    public void registerPositiveTest() {
        AuthData authData = serverFacade.register("newUser", "123abc", "email@test.com");
        Assertions.assertEquals("newUser", authData.userName());
    }

    @Test
    public void registerNegativeTest() {
        Assertions.assertThrows(Exception.class, () -> serverFacade.register("test2", "123abc", "email@test.com"));
    }

    @Test
    public void loginPositiveTest() {
        AuthData authData = serverFacade.login("test2", "test2password");
        Assertions.assertEquals("test2", authData.userName());
    }

    @Test
    public void loginNegativeTest() {
        Assertions.assertThrows(Exception.class, () -> serverFacade.login("notAUser", "myPassword"));
    }

    @Test
    public void logoutPositiveTest() {

    }

    @Test
    public void logoutNegativeTest() {

    }

    @Test
    public void listPositiveTest() {

    }

    @Test
    public void listNegativeTest() {

    }

    @Test
    public void createPositiveTest() {

    }

    @Test
    public void createNegativeTest() {

    }

    @Test
    public void joinPositiveTest() {

    }

    @Test
    public void joinNegativeTest() {

    }

    @Test
    public void clearTest() {

    }
}
