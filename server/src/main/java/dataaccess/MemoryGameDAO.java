package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games;

    public MemoryGameDAO() {
        games = new HashMap<>();
    }

    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Error: bad request", 400);
        }
        games.put(game.gameID(), game);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: bad request", 400);
        }
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
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
    }
}
