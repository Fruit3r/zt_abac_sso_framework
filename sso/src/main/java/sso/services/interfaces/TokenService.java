package sso.services.interfaces;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import sso.models.entities.IDToken;
import sso.models.entities.User;
import sso.utils.exceptions.*;

import java.security.PublicKey;
import java.security.SecureRandom;

public interface TokenService {

    /**
     * Returns the current public key used for IDToken verification
     *
     * @return The public key
     */
    PublicKey getPublicKey();

    /**
     * Generate an IDToken for the given user
     *
     * @param user  User to generate IDToken for
     * @return  Returns the generated IDToken
     */
    IDToken generateIDToken(User user) throws NotFoundException, BlockedException;

    /**
     * Validates the given IDToken
     *
     * @param IDToken   IDToken to validate
     * @throws JwtException if the IDToken is expired
     * @throws BlockedException if the user referenced by the IDToken is blocked
     * @throws NotFoundException if the user referenced by the IDToken does not exist
     * @throws ServiceException if an exception occured which has no specific handling
     * @return  Returns the UUID of the user referenced by the given IDToken
     */
    String validateIDTokenAndUser(IDToken IDToken) throws JwtException, BlockedException, NotFoundException, ServiceException;
}
