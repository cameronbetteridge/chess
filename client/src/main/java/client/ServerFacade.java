package client;

import model.AuthData;
import model.GameData;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient httpClient;

    public ServerFacade(String host, int port) {
        serverUrl = "https://" + host + ":" + port;
        httpClient = HttpClient.newHttpClient();
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

    private HttpRequest buildRequest(String method, String path, Object body) {

    }

    private HttpResponse<String> sendRequest(HttpRequest request) {

    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {

    }
}
