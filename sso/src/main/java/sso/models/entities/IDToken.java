package sso.models.entities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;


import java.security.PublicKey;
import java.util.Date;

public class IDToken {
    private String token;
    private PublicKey publicKey;

    public IDToken(String token, PublicKey publicKey) {
        this.token = token;
        this.publicKey = publicKey;
    }

    public String getToken() {
        return token;
    }

    public String getUUIDClaim() throws JwtException {
        return this.getClaims().get("uuid").toString();
    }

    public void validateToken() throws JwtException {
        Jwts.parserBuilder()
            .setSigningKey(this.publicKey)
            .build()
            .parseClaimsJws(this.token)
            .getBody();
    }

    public Date getExpirationDate() throws JwtException {
        Date exp, now;

        now = new Date();
        try {
            exp = this.getClaims().getExpiration();
        } catch (ExpiredJwtException e) {
            exp = e.getClaims().getExpiration();
        }

        long diff = now.getTime() - exp.getTime();
        if (diff < 0) {
            return new Date(0);
        } else {
            return new Date(diff);
        }
    }

    private Claims getClaims() throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(this.publicKey)
                .build()
                .parseClaimsJws(this.token)
                .getBody();
    }
}
