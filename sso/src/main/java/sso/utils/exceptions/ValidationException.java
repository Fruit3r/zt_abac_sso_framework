package sso.utils.exceptions;

public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 7467228149209887108L;

    public ValidationException(String message) {
        super(message);
    }
}
