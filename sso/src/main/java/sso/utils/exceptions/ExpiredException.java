package sso.utils.exceptions;

public class ExpiredException extends RuntimeException {

    private static final long serialVersionUID = 2261743830419322393L;

    public ExpiredException(String message) { super(message); }
}
