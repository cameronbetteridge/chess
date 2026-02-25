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
            throw new DataAccessException("Error: already taken");
        }
        games.put(game.gameID(), game);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: doesn't exist");
        }
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    public void updateGame(int gameID, GameData game) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: doesn't exist");
        }
        games.put(gameID, game);
    }

    public void clear() {
        games.clear();
    }
}
