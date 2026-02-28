package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import io.javalin.http.Context;

public class UserHandler {
    private final UserService service;

    public UserHandler(UserService service) {
        this.service = service;
    }

    public void register(Context context) throws DataAccessException {
        RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);
        RegisterResult result = service.register(request);
        context.result(new Gson().toJson(result));
    }

    public void login(Context context) throws DataAccessException {
        LoginRequest request = new Gson().fromJson(context.body(), LoginRequest.class);
        LoginResult result = service.login(request);
        context.result(new Gson().toJson(result));
    }

    public void logout(Context context) throws DataAccessException {

    }
}
