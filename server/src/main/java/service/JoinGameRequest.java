package service;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {
    public JoinGameRequest {
        if (authToken == null) {
            authToken = "";
        }
        if (playerColor == null) {
            playerColor = "";
        }
    }
}
