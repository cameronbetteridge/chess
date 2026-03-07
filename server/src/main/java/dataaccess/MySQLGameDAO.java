package dataaccess;

import model.GameData;

import java.util.Collection;

public class MySQLGameDAO implements GameDAO {
    public MySQLGameDAO() throws DataAccessException {
        createTable();
    }

    public void createGame(GameData gameData) throws DataAccessException {

    }

    public GameData getGame(int gameID) throws DataAccessException {

    }

    public Collection<GameData> listGames() {

    }

    public void updateGame(int gameID, GameData newGame) throws DataAccessException {

    }

    public void clear() {

    }

    private void createTable() throws DataAccessException {

    }
}
