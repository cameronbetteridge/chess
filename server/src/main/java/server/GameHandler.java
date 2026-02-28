package server;

import service.GameService;
import io.javalin.http.Context;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void createGame(Context context) {

    }

    public void joinGame(Context context) {

    }

    public void list(Context context) {

    }

    public void clear(Context context) {

    }
}
