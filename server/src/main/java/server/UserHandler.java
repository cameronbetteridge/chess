package server;

import service.UserService;
import io.javalin.http.Context;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context context) {

    }

    public void login(Context context) {

    }

    public void logout(Context context) {

    }
}
