package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SQLAuthTests {
    private final AuthDAO authDAO;

    public SQLAuthTests() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
    }

    @BeforeEach
    public void resetTests() throws DataAccessException {
        authDAO.clear();
        authDAO.createAuth(new AuthData("test", "test"));
    }

    @Test
    public void createAuthPositiveTest() throws DataAccessException {
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
        Assertions.assertThrows(DataAccessException.class, () -> new AuthData("notHere", "blah"));
    }

    @Test
    public void clearTest() throws DataAccessException {

    }
}
