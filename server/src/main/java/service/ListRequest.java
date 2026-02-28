package service;

public record ListRequest(String authToken) {
    public ListRequest {
        if (authToken == null) {
            authToken = "";
        }
    }
}
