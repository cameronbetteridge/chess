package server;

import dataaccess.DataAccessException;
import service.GameService;
import io.javalin.http.Context;

public class GameHandler {
    private final GameService service;

    public GameHandler(GameService service) {
        this.service = service;
    }

    public void createGame(Context context) throws DataAccessException {

    }

    public void joinGame(Context context) throws DataAccessException {

    }

    public void list(Context context) throws DataAccessException {

    }

    public void clear(Context context) {

    }
}
