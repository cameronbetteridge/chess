package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
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

    public ListResult list(ListRequest request) throws DataAccessException {
        authDAO.getAuth(request.authToken());

        ArrayList<GameDataList> gamesList = new ArrayList<>();
        for (GameData gameData : gameDAO.listGames()) {
            gamesList.add(new GameDataList(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName()));
        }

        return new ListResult(gamesList);
    }

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
        AuthData auth = authDAO.getAuth(request.authToken());
        UserData user = userDAO.getUser(auth.userName());
        GameData game = gameDAO.getGame(request.gameID());

        if (request.playerColor().equals("WHITE") && game.whiteUsername() == null) {
            gameDAO.updateGame(request.gameID(), new GameData(game.gameID(), user.username(), game.blackUsername(), game.gameName(), game.game()));
        } else if (request.playerColor().equals("BLACK") && game.blackUsername() == null) {
            gameDAO.updateGame(request.gameID(), new GameData(game.gameID(), game.whiteUsername(), user.username(), game.gameName(), game.game()));
        } else {
            throw new DataAccessException("Error: already taken");
        }
    }

    public void clear() {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
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
