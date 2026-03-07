package dataaccess;

import model.AuthData;

public class MySQLAuthDAO implements AuthDAO {
    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createAuth(AuthData authData) throws DataAccessException {

    }

    public AuthData getAuth(String authToken) throws DataAccessException {

    }

    public void deleteAuth(AuthData authData) throws DataAccessException {

    }

    public void clear() {

    }

    private void configureDatabase() throws DataAccessException {

    }
}
