package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class GameService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListResult list(ListRequest listRequest) throws DataAccessException {

    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {

    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {

    }

    public void clear() {}
}
