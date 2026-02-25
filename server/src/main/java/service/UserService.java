package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request.username().isEmpty() || request.password().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(user);
        String authToken = createAuthToken();
        AuthData auth = new AuthData(authToken, request.username());
        authDAO.createAuth(auth);
        return new RegisterResult(request.username(), authToken);
    }

//    public LoginResult login(LoginRequest request) throws DataAccessException {
//
//    }
//
//    public void logout(LogoutRequest request) throws DataAccessException {
//
//    }

    private String createAuthToken() {
        String uuid;
        boolean unique;
        do {
            uuid = UUID.randomUUID().toString();
            try {
                authDAO.getAuth(uuid);
                unique = false;
            } catch (DataAccessException e) {
                unique = true;
            }
        } while (!unique);
        return uuid;
    }
}
