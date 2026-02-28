package service;

public record LoginRequest(String username, String password) {
    public LoginRequest {
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
    }
}
