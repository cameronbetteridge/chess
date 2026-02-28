package service;

public record CreateGameRequest(String authToken, String gameName) {
    public CreateGameRequest {
        if (authToken == null) {
            authToken = "";
        }
        if (gameName == null) {
            gameName = "";
        }
    }
}
