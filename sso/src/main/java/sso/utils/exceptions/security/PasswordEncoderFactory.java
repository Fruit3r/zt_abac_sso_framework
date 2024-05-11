package sso.utils.exceptions.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class PasswordEncoderFactory {

    private final static int strength = 10;

    public static BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(strength, new SecureRandom());
    }
}
