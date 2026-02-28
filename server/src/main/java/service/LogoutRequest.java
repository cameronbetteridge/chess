package service;

public record LogoutRequest(String authToken) {
    public LogoutRequest {
        if (authToken == null) {
            authToken = "";
        }
    }
}
