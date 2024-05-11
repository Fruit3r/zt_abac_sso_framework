package sso.utils.exceptions;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 406981247904752070L;

    public ServiceException(String message) {
        super(message);
    }
}
