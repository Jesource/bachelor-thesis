package vu.jesource.productservice.web.exceptions;

public class CrudOperationException extends RuntimeException {
    public CrudOperationException(String message) {
        super(message);
    }

    public CrudOperationException(Operations operation, String message) {
        super(String.format("Failure occurred while performing %s operation: %s", operation, message));
    }

    public enum Operations {
        CREATE, READ, UPDATE, DELETE
    }
}
