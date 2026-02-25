package dataaccess;

import model.*;
import java.util.Collection;

public interface GameDAO {
    void createGame(GameData g) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames();
    void updateGame(int gameID, GameData newGame) throws DataAccessException;
    void clear();
}
