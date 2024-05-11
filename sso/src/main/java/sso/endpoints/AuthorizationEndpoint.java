package sso.endpoints;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.weaver.ast.Not;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import sso.models.dtos.AuthorizationReqDTO;
import sso.models.entities.IDToken;
import sso.models.entities.User;
import sso.services.interfaces.PolicyService;
import sso.services.interfaces.TokenService;
import sso.services.interfaces.UserService;
import sso.utils.annotations.LimitRequests;
import sso.utils.exceptions.BlockedException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.ServiceException;
import sso.utils.security.AuthorizationUtil;
import sso.utils.security.CookieUtil;

import java.lang.invoke.MethodHandles;


@Configuration
@RestController
@RequestMapping("/authorize")
public class AuthorizationEndpoint {
    @Value("${security.idtoken.refreshing}")
    public long IDTOKEN_REFRESHING;
    private final static String BASE_URL = "/authorize";
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AuthorizationUtil authorizationUtil = new AuthorizationUtil();
    private final CookieUtil cookieUtil = new CookieUtil();
    private final PolicyService policyService;
    private final TokenService tokenService;
    private final UserService userService;

    @Autowired
    public AuthorizationEndpoint(PolicyService policyService, TokenService tokenService, UserService userService) {
        this.policyService = policyService;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @LimitRequests
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void authorize(HttpServletRequest request,  @RequestBody AuthorizationReqDTO authorizationReqDTO, HttpServletResponse response) {
        LOGGER.info("HTTP-REQ: POST {} # BODY: {} # FROM: {}", BASE_URL, authorizationReqDTO, request.getRemoteHost());

        JSONObject object, action;
        User subjectUser = null;
        String subjectUserUUID = null;

        // Parse object attributes
        try {
            object = new JSONObject(authorizationReqDTO.getObject());
        } catch (JSONException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "-1#bad object attributes format");
        }
        // Parse action attributes
        try {
            action = new JSONObject(authorizationReqDTO.getAction());
        } catch (JSONException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "-2#base action attributes format");
        }


        IDToken subjectIDToken = new IDToken(authorizationReqDTO.getSubject(), tokenService.getPublicKey());
        if (subjectIDToken.getToken() == null || subjectIDToken.getToken().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-2#missing idtoken");
        }

        // Validate IDToken and its user and get subject attributes
        try {
            subjectUserUUID = tokenService.validateIDTokenAndUser(subjectIDToken);
        } catch (ExpiredJwtException e) {
            System.out.println("-3#expired idtoken");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-3#expired idtoken");
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
            subjectUser = userService.getUserByUUID(subjectUserUUID);
        } catch (NotFoundException e) {
            System.out.println("-6#account does not exist");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-6#account does not exist");
        } catch (ServiceException e) {
            System.out.println("internal server error");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Evaluate
        StandardEvaluationContext context = authorizationUtil.buildContext(subjectUser.getAttributes(), object, action);
        if (!policyService.evaluate(context)) {
            System.out.println("-1#missing permissions");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-1#missing permissions");
        }
    }
}
