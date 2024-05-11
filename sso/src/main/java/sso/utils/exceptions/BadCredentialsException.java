package sso.utils.exceptions;

public class BadCredentialsException extends RuntimeException {

    private static final long serialVersionUID = -7206405744402786576L;

    public BadCredentialsException(String message) { super(message); }
}
