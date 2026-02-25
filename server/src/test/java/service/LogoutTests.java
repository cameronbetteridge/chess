package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LogoutTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    public void reset() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void emptyRequestTest() {
        LogoutRequest request = new LogoutRequest("");
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout(request), "Error: unauthorized");
    }

    @Test
    public void goodRequest() throws DataAccessException {
        String authToken = "fdjskglshgdsg";
        userDAO.createUser(new UserData("thebest", "password", "email@hi.com"));
        authDAO.createAuth(new AuthData(authToken, "thebest"));
        LogoutRequest request = new LogoutRequest(authToken);
        userService.logout(request);
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(authToken), "Error: does not exist");
    }

    @Test
    public void doesNotExist() throws DataAccessException {
        String authToken = "fdjskglshgdsg";
        userDAO.createUser(new UserData("thebest", "password", "email@hi.com"));
        authDAO.createAuth(new AuthData("notthisauthtoken", "thebest"));
        LogoutRequest request = new LogoutRequest(authToken);
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout(request), "Error: unauthorized");
    }
}
