package dataaccess;

import model.*;

public interface AuthDAO {
    void createAuth(AuthData a);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData a);
}
