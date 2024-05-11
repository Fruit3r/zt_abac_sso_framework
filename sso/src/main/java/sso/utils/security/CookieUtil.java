package sso.utils.security;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;
import sso.models.entities.IDToken;

@Component
public class CookieUtil {

    public Cookie createIDTokenCookie(IDToken IDToken) {
        Cookie IDTokenCookie = new Cookie("idtoken", IDToken.getToken());
        IDTokenCookie.setPath("/");
        IDTokenCookie.setSecure(false);
        IDTokenCookie.setHttpOnly(false);
        IDTokenCookie.setMaxAge(Integer.MAX_VALUE);

        return IDTokenCookie;
    }
}
