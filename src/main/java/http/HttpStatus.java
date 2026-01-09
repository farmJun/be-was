package http;

public enum HttpStatus {
    // 2xx Success
    OK(200, "OK"),

    // 3xx Redirection
    FOUND(302, "Found");

    private int statusCode;
    private String message;

    HttpStatus(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
