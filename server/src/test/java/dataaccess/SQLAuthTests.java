package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SQLAuthTests {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public SQLAuthTests() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
        userDAO = new MySQLUserDAO();
    }

    @BeforeEach
    public void resetTests() throws DataAccessException {
        authDAO.clear();
        userDAO.clear();
        userDAO.createUser(new UserData("test", "testPassword", "testEmail"));
        authDAO.createAuth(new AuthData("test", "test"));
    }

    @Test
    public void createAuthPositiveTest() throws DataAccessException {
        userDAO.createUser(new UserData("user1", "password", "emailTest"));
        AuthData expected = new AuthData("testToken", "user1");
        authDAO.createAuth(expected);
        AuthData result = authDAO.getAuth("testToken");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void createAuthNegativeTest() throws DataAccessException {
        AuthData auth = new AuthData("test", "test");
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth), "already taken");
    }

    @Test
    public void getAuthPositiveTest() throws DataAccessException {
        AuthData expected = new AuthData("test", "test");
        AuthData result = authDAO.getAuth("test");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getAuthNegativeTest() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("notHere"));
    }

    @Test
    public void deleteAuthPositiveTest() throws DataAccessException {
        authDAO.deleteAuth(authDAO.getAuth("test"));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("test"));

    }

    @Test
    public void deleteAuthNegativeTest() throws DataAccessException {
        AuthData auth = new AuthData("doesNotExist", "blah");
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuth(auth));
    }

    @Test
    public void clearTest() throws DataAccessException {
        authDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("test"));
    }
}
