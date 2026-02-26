package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.Random;

public class GameService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

//    public ListResult list(ListRequest request) throws DataAccessException {
//
//    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        authDAO.getAuth(request.authToken());
        if (request.gameName().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        int gameID = createGameID();
        GameData game = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(game);
        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException {

    }

    public void clear() {

    }

    private int createGameID() {
        int id;
        boolean unique;
        Random random = new Random();
        Collection<GameData> games = gameDAO.listGames();
        do {
            id = Math.abs(random.nextInt());
            unique = true;
            for (GameData game : games) {
                if (game.gameID() == id) {
                    unique = false;
                    break;
                }
            }
        } while (!unique);
        return id;
    }
}
