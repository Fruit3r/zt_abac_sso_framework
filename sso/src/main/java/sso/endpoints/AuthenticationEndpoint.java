package sso.endpoints;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sso.mappers.CredentialsMapper;
import sso.models.dtos.CredentialsDTO;
import sso.models.entities.Credentials;
import sso.models.entities.IDToken;
import sso.models.entities.User;
import sso.services.interfaces.AuthenticationService;
import sso.services.interfaces.TokenService;
import sso.services.interfaces.UserService;
import sso.utils.annotations.LimitRequests;
import sso.utils.exceptions.BadCredentialsException;
import sso.utils.exceptions.BlockedException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.ServiceException;
import sso.utils.security.CookieUtil;

import java.lang.invoke.MethodHandles;
import java.util.Base64;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationEndpoint {

    @Value("${security.idtoken.refreshing}")
    public long IDTOKEN_REFRESHING;
    private final static String BASE_URL = "/authenticate";
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final UserService userService;
    private final CredentialsMapper credentialsMapper;

    private final CookieUtil cookieUtil;

    @Autowired
    public AuthenticationEndpoint(AuthenticationService authenticationService, TokenService tokenService, UserService userService, CredentialsMapper credentialsMapper, CookieUtil cookieUtil) {
        this.authenticationService = authenticationService;
        this.credentialsMapper = credentialsMapper;
        this.tokenService = tokenService;
        this.userService = userService;
        this.cookieUtil = cookieUtil;
    }

    @LimitRequests
    @PostMapping
    public void authenticate(HttpServletRequest request, HttpServletResponse response, @RequestBody CredentialsDTO credDTO) {
        LOGGER.info("HTTP-REQ: POST {} # BODY: {} # FROM: {}", BASE_URL, credDTO, request.getRemoteHost());

        IDToken IDToken;
        Credentials cred = credentialsMapper.credentialsDTOToCredentials(credDTO);

        try {
            IDToken = authenticationService.authenticate(cred);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-1");
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "-1");
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.addCookie(cookieUtil.createIDTokenCookie(IDToken));
        //return IDToken.getToken();
    }

    @LimitRequests
    @GetMapping("/maintain")
    public void maintain(HttpServletRequest request, HttpServletResponse response, @CookieValue(name = "idtoken", required = false) String idtoken) {
        LOGGER.info("HTTP-REQ: GET {}/maintain # FROM: {}", BASE_URL, request.getRemoteHost());

        if (idtoken == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "-1#missing idtoken");
        }

        IDToken IDToken = new IDToken(idtoken, tokenService.getPublicKey());
        User user;
        String userUUID;

        // Validate IDToken and its user and get subject attributes
        Boolean refreshIDToken = false;
        try {
            userUUID = tokenService.validateIDTokenAndUser(IDToken);
        } catch (ExpiredJwtException e) {
            long expDurationMS = IDToken.getExpirationDate().getTime();
            long refreshingMS = IDTOKEN_REFRESHING * 1000;

            if (expDurationMS <= refreshingMS) {
                // Idtoken can be refreshed
                refreshIDToken = true;
                userUUID = e.getClaims().get("uuid").toString();
            } else {
                // Idtoken cannot be refreshed
                System.out.println("-3#expired idtoken");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-3#expired idtoken");
            }
        } catch (JwtException e) {
            System.out.println("-4#invalid idtoken");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-4#invalid idtoken");
        } catch (BlockedException e) {
            System.out.println("-5#account blocked");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-5#account blocked");
        } catch (NotFoundException e) {
            System.out.println("-6#account does not exist");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-6#account does not exist");
        } catch (ServiceException e) {
            System.out.println("internal server error");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Fetch target user
        try {
            user = userService.getUserByUUID(userUUID);
        } catch (NotFoundException e) {
            System.out.println("-6#account does not exist");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-6#account does not exist");
        } catch (ServiceException e) {
            System.out.println("internal server error");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //  Issue new idtoken if possible
        if (refreshIDToken) {
            System.out.println("Issue new token");
            issueNewIDTokenToResponse(user, response);
        }
    }

    @LimitRequests
    @GetMapping("/pubkey")
    public String getPublicAuthenticationKey(HttpServletRequest request) {
        LOGGER.info("HTTP-REQ: GET {}/pubkey # FROM: {}", BASE_URL, request.getRemoteHost());
        return Base64.getEncoder().encodeToString(tokenService.getPublicKey().getEncoded());
    }

    private void issueNewIDTokenToResponse(User targetUser, HttpServletResponse response) {
        // TODO: Check if exception handling is needed
        IDToken newToken = tokenService.generateIDToken(targetUser);
        response.addCookie(cookieUtil.createIDTokenCookie(newToken));
    }
}
