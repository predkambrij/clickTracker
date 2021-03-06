package org.blatnik.o7testproj;

public class ResponseError {

    private String message;

    public ResponseError(String message, String... args) {
        this.message = String.format(message, (Object) args);
    }

    public ResponseError(Exception ex) {
        this.message = ex.getMessage();
    }
}
