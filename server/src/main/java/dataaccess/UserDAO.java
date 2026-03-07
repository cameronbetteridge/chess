package dataaccess;

import model.*;

public interface UserDAO {
    void createUser(UserData u) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean verifyUser(String username, String password) throws DataAccessException;
    void clear() throws DataAccessException;
}
