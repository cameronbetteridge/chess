package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPositiveTest() {

    }

    @Test
    public void registerNegativeTest() {

    }

    @Test
    public void loginPositiveTest() {

    }

    @Test
    public void loginNegativeTest() {

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
    public void clearPositiveTest() {

    }

    @Test
    public void clearNegativeTest() {

    }

}
