package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import io.javalin.http.Context;

public class GameHandler {
    private final GameService service;

    public GameHandler(GameService service) {
        this.service = service;
    }

    public void createGame(Context context) throws DataAccessException {
        CreateGameRequest request = new Gson().fromJson(context.body(), CreateGameRequest.class);
        request = new CreateGameRequest(context.header("authorization"), request.gameName());
        CreateGameResult result = service.createGame(request);
        context.result(new Gson().toJson(result));
    }

    public void joinGame(Context context) throws DataAccessException {

    }

    public void list(Context context) throws DataAccessException {

    }

    public void clear(Context context) {
        service.clear();
        context.result("{}");
    }
}
