package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SQLUserTests {
    private final UserDAO userDAO;

    public SQLUserTests() throws DataAccessException {
        userDAO = new MySQLUserDAO();
    }

    @BeforeEach
    public void resetTests() throws DataAccessException {
        userDAO.clear();
        userDAO.createUser(new UserData("test", "wordpass", "hi@test.com"));
    }

    @Test
    public void createUserPositiveTest() throws DataAccessException {
        UserData user = new UserData("user1", "123abc", "hi@test.com");
        userDAO.createUser(user);
        UserData result = userDAO.getUser("user1");
        Assertions.assertEquals(user, result);
    }

    @Test
    public void createUserNegativeTest() throws DataAccessException {
        UserData user = new UserData("test", "123abc", "hi@test.com");
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user), "Error: already taken");
    }

    @Test
    public void getUserPositiveTest() throws DataAccessException {
        UserData result = userDAO.getUser("test");
        UserData expected = new UserData("test", "wordpass", "hi@test.com");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getUserNegativeTest() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("hi"), "Error: doesn't exist");
    }

    @Test
    public void clearTest() throws DataAccessException {
        userDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("test"));
    }
}
