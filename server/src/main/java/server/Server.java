package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

public class Server {
    private final Javalin javalin;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(userDAO, gameDAO, authDAO);
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
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
}
