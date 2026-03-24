package client;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient httpClient;

    public ServerFacade(String host, int port) {
        serverUrl = "http://" + host + ":" + port;
        httpClient = HttpClient.newHttpClient();
    }

    public AuthData register(UserData userData) throws Exception {
        var request = buildRequest("POST", "/user", userData, null);
        var response = sendRequest(request);

        AuthData authData = handleResponse(response, AuthData.class);
        assert authData != null;
        return new AuthData(authData.authToken(), userData.username());
    }

    public AuthData login(String username, String password) throws Exception {
        UserData userData = new UserData(username, password, null);
        var request = buildRequest("POST", "/session", userData, null);
        var response = sendRequest(request);

        AuthData authData = handleResponse(response, AuthData.class);
        assert authData != null;
        return new AuthData(authData.authToken(), userData.username());
    }

    public void logout(String authToken) throws Exception {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameList listGames(String authToken) throws Exception {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, GameList.class);
    }

    public CreateResult createGame(String authToken, String gameName) throws Exception {
        CreateRequest createRequest = new CreateRequest(gameName);
        var request = buildRequest("POST", "/game", createRequest, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateResult.class);
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        JoinRequest joinRequest = new JoinRequest(gameID, playerColor);
        var request = buildRequest("PUT", "/game", joinRequest, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (authToken != null) {
            request = request.header("authorization", authToken);
        }
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception("Bad connection. Please try again later.");
        }
    }

    private final Map<Integer, String> statusCodeMessages = Map.of(
            400, "That's not right.",
            401, "Incorrect username or password.",
            403, "Already taken.",
            500, "Something went wrong. Please try again later."
    );

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        int status = response.statusCode();
        if (!isSuccessful(status)) {
            throw new Exception(statusCodeMessages.get(status));
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
