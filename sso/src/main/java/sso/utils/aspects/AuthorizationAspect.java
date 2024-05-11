package sso.utils.aspects;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import sso.models.entities.IDToken;
import sso.models.entities.User;
import sso.services.implementations.TokenServiceJWT;
import sso.services.interfaces.PolicyService;
import sso.services.interfaces.TokenService;
import sso.services.interfaces.UserService;
import sso.utils.annotations.Authorize;
import sso.utils.exceptions.BlockedException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.ServiceException;
import sso.utils.security.AuthorizationUtil;
import sso.utils.security.CookieUtil;

import java.util.Date;

@Aspect
@Configuration
@Component
public class AuthorizationAspect {
    @Value("${security.idtoken.refreshing}")
    public long IDTOKEN_REFRESHING;
    Environment env;
    private final UserService userService;
    private final PolicyService policyService;
    private final TokenService tokenService;
    private final AuthorizationUtil authorizationUtil;
    private final CookieUtil cookieUtil;

    @Autowired
    public AuthorizationAspect(Environment env, UserService userService, PolicyService policyService, TokenService tokenService, AuthorizationUtil authorizationUtil, CookieUtil cookieUtil) {
        this.env = env;
        this.userService = userService;
        this.policyService = policyService;
        this.tokenService = tokenService;
        this.authorizationUtil = authorizationUtil;
        this.cookieUtil = cookieUtil;
    }

    @Around("@annotation(sso.utils.annotations.Authorize)")
    public Object authorize(ProceedingJoinPoint joinPoint) throws Throwable {
        JSONObject subjectAttributes, objectAttributes, actionAttributes;
        JSONObject annotationAttributes;
        User targetUser = null;
        String targetUserUUID = null;

        // Get IDToken from idtoken cookie
        IDToken IDToken = getIDTokenFromCookie();
        if (IDToken == null) {
            // No cookie sent
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-2#missing idtoken");
        }

        // Validate IDToken and its user and get subject attributes
        Boolean refreshIDToken = false;
        try {
            targetUserUUID = tokenService.validateIDTokenAndUser(IDToken);
        } catch (ExpiredJwtException e) {
            long expDurationMS = IDToken.getExpirationDate().getTime();
            long refreshingMS = IDTOKEN_REFRESHING * 1000;

            if (expDurationMS <= refreshingMS) {
                // Idtoken can be refreshed
                refreshIDToken = true;
                targetUserUUID = e.getClaims().get("uuid").toString();
            } else {
                // Idtoken cannot be refreshed
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-3#expired idtoken");
            }
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-4#invalid idtoken");
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-5#account blocked");
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-6#account does not exist");
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Fetch target user
        try {
            targetUser = userService.getUserByUUID(targetUserUUID);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-6#account does not exist");
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //  Issue new idtoken if possible
        if (refreshIDToken) {
            issueNewIDTokenToResponse(targetUser);
        }


        // Get subject attributes
        subjectAttributes = targetUser.getAttributes();

        // Get object and action attributes from property defined by annotation value
        String configPropertyName = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Authorize.class).value();
        String annotationAttributesAsString = env.getProperty(configPropertyName);
        try {
            annotationAttributes = new JSONObject(annotationAttributesAsString);
        } catch (JSONException e) {
            // Configures object / action attributes have an invalid json format
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-7#invalid endpoint authorization context");
        }

        // Get object attributes
        try {
            objectAttributes = annotationAttributes.getJSONObject("object");
        } catch (JSONException e) {
            objectAttributes = new JSONObject("{}");
        }
        // Get action attributes
        try {
            actionAttributes = annotationAttributes.getJSONObject("action");
        } catch (JSONException e) {
            actionAttributes = new JSONObject("{}");
        }

        // Build authorization context
        StandardEvaluationContext context = authorizationUtil.buildContext(subjectAttributes, objectAttributes, actionAttributes);
        // Evaluate context for authorization
        if (!policyService.evaluate(context)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "-1#missing permissions");
        }

        return joinPoint.proceed();
    }

    private IDToken getIDTokenFromCookie() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        Cookie IDTokenCookie = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("idtoken")) {
                IDTokenCookie = cookie;
            }
        }

        if (IDTokenCookie == null) {
            return null;
        } else {
            return new IDToken(IDTokenCookie.getValue(), tokenService.getPublicKey());
        }
    }

    private void issueNewIDTokenToResponse(User targetUser) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();

        // Issue new idtoken
        // TODO: Check if exception handling is needed
        IDToken newToken = tokenService.generateIDToken(targetUser);
        response.addCookie(cookieUtil.createIDTokenCookie(newToken));
    }
}