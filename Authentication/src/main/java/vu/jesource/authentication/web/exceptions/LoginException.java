package vu.jesource.authentication.web.exceptions;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }

    public LoginException() {
        super("Failed to login");
    }
}
