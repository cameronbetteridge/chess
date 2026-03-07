package dataaccess;

import model.UserData;

public class MySQLUserDAO implements UserDAO {
    public MySQLUserDAO() throws DataAccessException {
        createTable();
    }

    public void createUser(UserData userData) throws DataAccessException {

    }

    public UserData getUser(String username) throws DataAccessException {

    }

    public void clear() {

    }

    private void createTable() throws DataAccessException {

    }
}
