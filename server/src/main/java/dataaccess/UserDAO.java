package dataaccess;

import model.*;

public interface UserDAO {
    void createUser(UserData u) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear();
}
