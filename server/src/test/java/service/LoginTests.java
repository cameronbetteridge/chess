package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginTests {
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
        LoginRequest request = new LoginRequest("", "");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(request), "Error: bad request");
    }

    @Test
    public void emptyUsernameTest() {
        LoginRequest request = new LoginRequest("", "password");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(request), "Error: bad request");
    }

    @Test
    public void emptyPasswordTest() {
        LoginRequest request = new LoginRequest("thebest", "");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(request), "Error: bad request");
    }

    @Test
    public void usernameDoesNotExist() throws DataAccessException {
        userDAO.createUser(new UserData("notthisone", "helloworld", "something@yes.io"));
        LoginRequest request = new LoginRequest("thebest", "password");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(request), "Error: unauthorized");
    }

    @Test
    public void goodRequest() throws DataAccessException {
        userDAO.createUser(new UserData("thebest", "password", "hello@hi.com"));
        LoginRequest request = new LoginRequest("thebest", "password");
        LoginResult result = userService.login(request);
        Assertions.assertEquals(request.username(), result.username());
        Assertions.assertNotEquals("", result.authToken());
        assertAuthCreated(request.username(), result.authToken());
    }

    @Test
    public void wrongPassword() throws DataAccessException {
        userDAO.createUser(new UserData("thebest", "password", "hello@hi.com"));
        LoginRequest request = new LoginRequest("thebest", "123");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(request), "Error: unauthorized");
    }

    private void assertAuthCreated(String username, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        AuthData expected = new AuthData(authToken, username);
        Assertions.assertEquals(expected, authData);
    }
}
