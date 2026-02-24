package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;

import javax.xml.crypto.Data;

public class UserService {
    UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {

    }
    public void logout(LogoutRequest logoutRequest) throws DataAccessException {

    }
}
