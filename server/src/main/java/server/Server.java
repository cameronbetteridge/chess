package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

public class Server {
    private final Javalin javalin;
    private boolean databaseGood;

    public Server() {
        UserDAO userDAO = null;
        GameDAO gameDAO = null;
        AuthDAO authDAO = null;
        try {
            userDAO = new MySQLUserDAO();
            gameDAO = new MySQLGameDAO();
            authDAO = new MySQLAuthDAO();
            databaseGood = true;
        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
            databaseGood = false;
        }
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(userDAO, gameDAO, authDAO);
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .before(this::badDatabaseHandler)
                .delete("/db", gameHandler::clear)
                .post("/user", userHandler::register)
                .post("/session", userHandler::login)
                .delete("/session", userHandler::logout)
                .get("/game", gameHandler::list)
                .post("/game", gameHandler::createGame)
                .put("/game", gameHandler::joinGame)
                .exception(DataAccessException.class, this::exceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(DataAccessException e, Context context) {
        context.status(e.toHttpStatusCode());
        context.result(e.toJson());
    }

    private void badDatabaseHandler(Context context) throws DataAccessException {
        if (!databaseGood) {
            throw new DataAccessException("Error: An error occurred with the server's database", 500);
        }
    }
}
