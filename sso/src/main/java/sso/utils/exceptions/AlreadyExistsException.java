package sso.utils.exceptions;

public class AlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = -8448668421927942554L;

    public AlreadyExistsException(String message) {
        super(message);
    }
}
