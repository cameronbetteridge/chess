package dataaccess;

import model.*;

public interface AuthDAO {
    void createAuth(AuthData a) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData a) throws DataAccessException;
    void clear();
}
