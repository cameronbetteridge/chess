package client;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class ServerFacade {
    private final String host;
    private final int port;

    public ServerFacade(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public AuthData register(String username, String password, String email) {

    }

    public AuthData login(String username, String password) {

    }

    public void logout(String authToken) {

    }

    public ArrayList<GameData> listGames(String authToken) {

    }

    public int createGame(String authToken, String gameName) {

    }

    public void joinGame(String authToken, int gameID, String playerColor) {

    }

    public void clear() {

    }
}
