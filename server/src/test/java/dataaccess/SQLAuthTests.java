package dataaccess;

import model.AuthData;
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
        authDAO.createAuth(new AuthData("2ldsgs892hg2", "test"));
    }

    @Test
    public void createAuthPositiveTest() throws DataAccessException {

    }

    @Test
    public void createAuthNegativeTest() throws DataAccessException {

    }

    @Test
    public void getAuthPositiveTest() throws DataAccessException {

    }

    @Test
    public void getAuthNegativeTest() throws DataAccessException {

    }

    @Test
    public void deleteAuthPositiveTest() throws DataAccessException {

    }

    @Test
    public void deleteAuthNegativeTest() throws DataAccessException {

    }

    @Test
    public void clearTest() throws DataAccessException {

    }
}
