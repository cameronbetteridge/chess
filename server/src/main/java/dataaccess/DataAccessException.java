package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    private final int statusCode;

    public DataAccessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public DataAccessException(String message, int statusCode, Throwable ex) {
        super(message, ex);
        this.statusCode = statusCode;
    }

    public int toHttpStatusCode() {
        return statusCode;
    }

    public String toJson() {
        return " { \"message\": \"" + getMessage() + "\" } ";
    }
}
