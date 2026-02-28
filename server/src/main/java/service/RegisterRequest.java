package service;

public record RegisterRequest(String username, String password, String email) {
    public RegisterRequest {
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (email == null) {
            email = "";
        }
    }
}
