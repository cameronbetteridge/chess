package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> auths;

    public MemoryAuthDAO() {
        auths = new HashMap<>();
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        if (auths.containsKey(auth.authToken())) {
            throw new DataAccessException("Error: already taken", 500);
        }
        auths.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized", 401);
        }
        return auths.get(authToken);
    }

    public void deleteAuth(AuthData auth) throws DataAccessException {
        if (!auths.containsKey(auth.authToken())) {
            throw new DataAccessException("Error: unauthorized", 401);
        }
        auths.remove(auth.authToken());
    }

    public void clear() {
        auths.clear();
    }
}
