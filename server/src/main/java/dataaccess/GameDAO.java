package dataaccess;

import model.*;
import java.util.Collection;

public interface GameDAO {
    int createGame(GameData g) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, GameData newGame) throws DataAccessException;
    void clear() throws DataAccessException;
}
