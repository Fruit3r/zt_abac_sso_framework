package sso.services.implementations;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import sso.models.entities.IDToken;
import sso.models.entities.User;
import sso.persistance.interfaces.TokenKeyDAO;
import sso.services.interfaces.TokenService;
import sso.services.interfaces.UserService;
import sso.utils.exceptions.BlockedException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.ServiceException;

import java.security.PublicKey;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@Configuration
public class TokenServiceJWT implements TokenService  {

    @Value("${security.idtoken.expiration}")
    public long IDTOKEN_EXPIRATION;
    @Value("${security.idtoken.refreshing}")
    public long IDTOKEN_REFRESHING;
    private final TokenKeyDAO tokenKeyDAO;
    private final UserService userService;

    @Autowired
    public TokenServiceJWT(TokenKeyDAO tokenKeyDAO, UserService userService) {
        this.tokenKeyDAO = tokenKeyDAO;
        this.userService = userService;
    }

    @Override
    public PublicKey getPublicKey() {
        return tokenKeyDAO.getPublicKey();
    }

    @Override
    public IDToken generateIDToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDateTime = now.plusSeconds(IDTOKEN_EXPIRATION);

        String token = Jwts.builder()
                .setExpiration(Timestamp.valueOf(expiryDateTime))
                .claim("uuid", user.getUUID())
                .signWith(tokenKeyDAO.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();

        return new IDToken(token, tokenKeyDAO.getPublicKey());
    }

    @Override
    public String validateIDTokenAndUser(IDToken IDToken) throws JwtException, BlockedException, NotFoundException, ServiceException {
        // Validate token
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(tokenKeyDAO.getPublicKey())
            .build()
            .parseClaimsJws(IDToken.getToken())
            .getBody();

        // Validate user
        String UUID = claims.get("uuid").toString();
        User user = userService.getUserByUUID(UUID);
        if (user.isBlocked()) {
            throw new BlockedException("");
        }

        return UUID;
    }
}
