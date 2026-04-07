package client;

import java.net.http.HttpClient;

public class WebsocketCommunicator {
    private final String serverUrl;
    private final HttpClient httpClient;

    public WebsocketCommunicator(String host, int port) {
        serverUrl = "http://" + host + ":" + port;
        httpClient = HttpClient.newHttpClient();
    }

    public void connectWebsocket(String authToken, int gameID) throws Exception {

    }
}
