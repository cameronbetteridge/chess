package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterTests {
    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    public void reset() {
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO);
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
    }

    @Test
    public void goodRequest() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("thebest", "password", "hello@test.com");
        RegisterResult result = userService.register(request);
        Assertions.assertEquals("thebest", result.username());
        Assertions.assertNotEquals("", result.authToken());
        assertUserCreated(request);
    }

    @Test
    public void usernameTaken() throws DataAccessException {
        RegisterRequest oldRequest = new RegisterRequest("thebest", "password", "hello@test.com");
        userService.register(oldRequest);
        RegisterRequest request = new RegisterRequest("thebest", "password", "hello@test.com");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(request), "Error: already taken");
    }

    private void assertUserCreated(RegisterRequest request) throws DataAccessException {
        UserData userData = userDAO.getUser(request.username());
        UserData expected = new UserData(request.username(), request.password(), request.email());
        Assertions.assertEquals(expected, userData);
    }
}
