package sso.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sso.models.entities.Credentials;
import sso.models.entities.IDToken;
import sso.models.entities.User;
import sso.services.interfaces.AuthenticationService;
import sso.services.interfaces.TokenService;
import sso.services.interfaces.UserService;
import sso.utils.exceptions.BadCredentialsException;
import sso.utils.exceptions.BlockedException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.ServiceException;
import sso.utils.security.PasswordEncoderFactory;

@Service
public class AuthenticationServiceStd implements AuthenticationService {

    private final BCryptPasswordEncoder passwordEncoder = PasswordEncoderFactory.getPasswordEncoder();
    private final UserService userService;
    private final TokenService tokenService;

    @Autowired
    public AuthenticationServiceStd(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;

    }

    @Override
    public IDToken authenticate(Credentials cred) {
        User targetUser = null;

        try {
            // Fetch target user by username
            targetUser = userService.getUserByUsername(cred.getUsername());

            // Validate password of target user
            if (!passwordEncoder.matches(cred.getPassword(), targetUser.getPasswordEncoded())) {
                // Increase failed auth count
                userService.increaseFailedAuth(targetUser);
                throw new BadCredentialsException("AuthenticationServiceStd.authenticate() # MSG: wrong password used for authentication of user with UUID '" + targetUser.getUUID() + "'");
            }

            // Validate blocked state of target user
            if (targetUser.isBlocked()) {
                throw new BlockedException("AuthenticationServiceStd.authenticate() # MSG: account is blocked");
            }

            // Reset failed auth count
            if (targetUser.getFailedAuth() > 0) {
                userService.resetFailedAuth(targetUser);
            }
            return tokenService.generateIDToken(targetUser);
        } catch (NotFoundException e) {
            throw new BadCredentialsException("AuthenticationServiceStd.authenticate() # MSG: user does not exist");
        } catch (ServiceException e) {
            throw new ServiceException("AuthenticationServiceStd.authenticate() > " + e.getMessage());
        }
    }
}
