package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterTests {
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
        RegisterRequest request = new RegisterRequest("", "", "");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(request), "Error: bad request");
    }

    @Test
    public void emptyUsernameTest() {
        RegisterRequest request = new RegisterRequest("", "password", "hello@test.com");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(request), "Error: bad request");
    }

    @Test
    public void emptyPasswordTest() {
        RegisterRequest request = new RegisterRequest("thebest", "", "hello@test.com");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(request), "Error: bad request");
    }

    @Test
    public void emptyEmailTest() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("thebest", "password", "");
        RegisterResult result = userService.register(request);
        Assertions.assertEquals("thebest", result.username());
        Assertions.assertNotEquals("", result.authToken());
        assertUserCreated(request);
        assertAuthCreated(request.username(), result.authToken());
    }

    @Test
    public void goodRequest() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("thebest", "password", "hello@test.com");
        RegisterResult result = userService.register(request);
        Assertions.assertEquals("thebest", result.username());
        Assertions.assertNotEquals("", result.authToken());
        assertUserCreated(request);
        assertAuthCreated(request.username(), result.authToken());
    }

    @Test
    public void usernameTaken() throws DataAccessException {
        userDAO.createUser(new UserData("thebest", "password", "hello@test.com"));
        RegisterRequest request = new RegisterRequest("thebest", "password", "hello@test.com");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(request), "Error: already taken");
    }

    private void assertUserCreated(RegisterRequest request) throws DataAccessException {
        UserData userData = userDAO.getUser(request.username());
        UserData expected = new UserData(request.username(), request.password(), request.email());
        Assertions.assertEquals(expected, userData);
    }

    private void assertAuthCreated(String username, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        AuthData expected = new AuthData(authToken, username);
        Assertions.assertEquals(expected, authData);
    }
}
