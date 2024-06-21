package vu.jesource.authentication.web.exceptions;

public class TokenProcessingException extends RuntimeException {
    public TokenProcessingException() {
        super("Failed to process token");
    }

    public TokenProcessingException(String message) {
        super(message);
    }
}
