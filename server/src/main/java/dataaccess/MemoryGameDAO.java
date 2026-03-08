package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games;
    private int idCounter;

    public MemoryGameDAO() {
        games = new HashMap<>();
        idCounter = 1;
    }

    public int createGame(GameData game) throws DataAccessException {
        int gameID = idCounter;
        idCounter++;
        game = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(gameID, game);
        return gameID;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: bad request", 400);
        }
        return games.get(gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    public void updateGame(int gameID, GameData game) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: bad request", 400);
        }
        games.put(gameID, game);
    }

    public void clear() {
        games.clear();
        idCounter = 1;
    }
}
