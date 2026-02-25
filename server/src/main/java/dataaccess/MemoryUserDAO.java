package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users;

    public MemoryUserDAO() {
        users = new HashMap<>();
    }

    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: already taken");
        }
        users.put(user.username(), user);
    }

    public UserData getUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("Error: does not exist");
        }
        return users.get(username);
    }

    public void clear() {
        users.clear();
    }
}
