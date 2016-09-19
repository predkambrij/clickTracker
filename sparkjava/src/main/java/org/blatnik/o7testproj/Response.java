package org.blatnik.o7testproj;

public class Response {
    private String message;

    public Response(String message, String... args) {
        this.message = String.format(message, (Object) args);
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
