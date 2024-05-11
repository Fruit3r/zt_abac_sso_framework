package sso.services.interfaces;

import sso.models.entities.Credentials;
import sso.models.entities.IDToken;
import sso.utils.exceptions.BadCredentialsException;
import sso.utils.exceptions.BlockedException;
import sso.utils.exceptions.ServiceException;

public interface AuthenticationService {

    /**
     * Authenticates a user by his credentials and generates an IDToken on success
     *
     * @param cred  Credentials of the user
     * @return  Returns the generated IDToken for the user
     * @throws BadCredentialsException  if one of the credentials is invalid
     * @throws BlockedException if the user referenced by the credentials is blocked
     * @throws ServiceException if an exception occured which has no specific handling
     */
    IDToken authenticate(Credentials cred) throws BadCredentialsException, BlockedException, ServiceException;
}
