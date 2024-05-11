package sso.endpoints;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sso.mappers.PolicyMapper;
import sso.mappers.UserMapper;
import sso.models.dtos.*;
import sso.models.entities.IDToken;
import sso.models.entities.Policy;
import sso.models.entities.User;
import sso.services.interfaces.PolicyService;
import sso.services.interfaces.TokenService;
import sso.services.interfaces.UserService;
import sso.utils.annotations.Authorize;
import sso.utils.annotations.LimitRequests;
import sso.utils.exceptions.*;
import sso.utils.security.Validator;

import java.lang.invoke.MethodHandles;
import java.util.List;


@Configuration
@RestController
@RequestMapping("/administrate")
public class AdministrationEndpoint {

    private final static String BASE_URL = "/administrate";
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final PolicyService policyService;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final PolicyMapper policyMapper;

    @Autowired
    public AdministrationEndpoint(UserService userService, PolicyService policyService, TokenService tokenService, UserMapper userMapper, PolicyMapper policyMapper) {
        this.userService = userService;
        this.policyService = policyService;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
        this.policyMapper = policyMapper;
    }

    @LimitRequests
    @Authorize("security.authorize.administration.getAllUsers")
    @GetMapping("/users")
    public List<UserDTO> getAllUsers(HttpServletRequest request) {
        LOGGER.info("HTTP-REQ: GET {}/users # FROM: {}", BASE_URL, request.getRemoteHost());

        try {
            return userMapper.usersToUserDTOs(userService.getAllUsers());
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LimitRequests
    @Authorize("security.authorize.administration.getUserByUUID")
    @GetMapping("/users/{UUID}")
    public UserDTO getUserByUUID(HttpServletRequest request, @PathVariable String UUID) {
        LOGGER.info("HTTP-REQ: GET {}/users/{} # FROM: ", BASE_URL, UUID, request.getRemoteHost());

        try {
            Validator.validatePattern(User.PATTERN_UUID, UUID);
            return userMapper.userToUserDTO(userService.getUserByUUID(UUID));
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "-1#invalid uuid format");
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "-1#user not found");
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LimitRequests
    @Authorize("security.authorize.administration.createUser")
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(HttpServletRequest request, @RequestBody NewUserDTO newUserDTO) {
        LOGGER.info("HTTP-REQ: POST {}/users # BODY: {} # FROM: {}", BASE_URL, newUserDTO, request.getRemoteHost());

        try {
            User user = userMapper.newUserDTOToUser(newUserDTO);
            User resultUser = userService.createUser(user);
            return userMapper.userToUserDTO(resultUser);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "-1#invalid new user data");
        } catch (AlreadyExistsException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "-1#user already exists");
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LimitRequests
    @Authorize("security.authorize.administration.updateUser")
    @PutMapping("/users")
    public UserDTO updateUser(HttpServletRequest request, @RequestBody UpdateUserDTO updateUserDTO) {
        LOGGER.info("HTTP-REQ: PUT {}/users # BODY: {} # FROM: {}", BASE_URL, updateUserDTO, request.getRemoteHost());

        try {
            User updateUser = userMapper.updateUserDTOToUser(updateUserDTO);
            User resultUser = userService.updateUser(updateUser);
            return userMapper.userToUserDTO(resultUser);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "-1#invalid update user data");
        } catch (AlreadyExistsException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "-1#unique update user data already in use");
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "-1#user not found");
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LimitRequests
    @Authorize("security.authorize.administration.deleteUserByUUID")
    @DeleteMapping("/users/{UUID}")
    public UserDTO deleteUserByUUID(HttpServletRequest request, @PathVariable String UUID) {
        LOGGER.info("HTTP-REQ: DELETE {}/users/{} # FROM: {}", BASE_URL, UUID, request.getRemoteHost());

        try {
            Validator.validatePattern(User.PATTERN_UUID, UUID);
            User deletedUser = userService.deleteUserByUUID(UUID);
            return userMapper.userToUserDTO(deletedUser);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "-1#invalid uuid format");
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "-1#user not found");
        } catch (PersistenceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LimitRequests
    @Authorize("security.authorize.administration.getAuthenticatedUser")
    @GetMapping("/users/authenticated")
    public UserDTO getAuthenticatedUser(HttpServletRequest request, @CookieValue(name = "idtoken") String idtoken) {
        LOGGER.info("HTTP-REQ: GET {}/users/authenticated # FROM: {}", BASE_URL, request.getRemoteHost());

        IDToken IDToken = new IDToken(idtoken, tokenService.getPublicKey());
        User user;

        try {
            user = userService.getUserByUUID(IDToken.getUUIDClaim());
        } catch (JwtException e) {
            // Should never happen as token is validated by @Authorize annotation
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            // Should never happen as user is checked by @Authorize annotation
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return userMapper.userToUserDTO(user);
    }

    @LimitRequests
    @Authorize("security.authorize.administration.updateAuthenticatedUser")
    @PutMapping("/users/authenticated")
    public UserDTO updateAuthenticatedUser(HttpServletRequest request, @CookieValue(name = "idtoken") String idtoken, @RequestBody UpdateUserDTO updateUserDTO) {
        LOGGER.info("HTTP-REQ: PUT {}/users/authenticated # FROM: {}", BASE_URL, request.getRemoteHost());

        IDToken IDToken = new IDToken(idtoken, tokenService.getPublicKey());
        updateUserDTO.setUUID(IDToken.getUUIDClaim());

        // Updating attributes not allowed for authenticated user
        updateUserDTO.setAttributes(null);

        try {
            User updateUser = userMapper.updateUserDTOToUser(updateUserDTO);
            User resultUser = userService.updateUser(updateUser);
            return userMapper.userToUserDTO(resultUser);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "-1#invalid update user data");
        } catch (AlreadyExistsException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "-1#unique update user data already in use");
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "-1#user not found");
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LimitRequests
    @Authorize("security.authorize.administration.getAllPolicies")
    @GetMapping("/policies")
    public List<PolicyDTO> getAllPolicies(HttpServletRequest request) {
        LOGGER.info("HTTP-REQ: GET {}/policies # FROM: {}", BASE_URL, request.getRemoteHost());

        try {
            return policyMapper.policiesToPolicyDTOs(policyService.getAllPolicies());
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LimitRequests
    @Authorize("security.authorize.administration.createPolicy")
    @PostMapping("/policies")
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyDTO createPolicy(HttpServletRequest request, @RequestBody NewPolicyDTO newPolicyDTO) {
        LOGGER.info("HTTP-REQ: POST {}/policies # BODY: {} # FROM: {}", BASE_URL, newPolicyDTO, request.getRemoteHost());

        try {
            Policy policy = policyMapper.newPolicyDTOToPolicy(newPolicyDTO);
            Policy resultPolicy = policyService.createPolicy(policy);
            return policyMapper.policyToPolicyDTO(resultPolicy);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "-1#invalid new policy data");
        } catch (AlreadyExistsException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "-1#policy already exists");
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LimitRequests
    @Authorize("security.authorize.administration.updatePolicy")
    @PutMapping("/policies")
    public PolicyDTO updatePolicy(HttpServletRequest request, @RequestBody UpdatePolicyDTO updatePolicyDTO) {
        LOGGER.info("HTTP-REQ: PUT {}/policies # BODY: {} # FROM: {}", BASE_URL, updatePolicyDTO, request.getRemoteHost());

        try {
            Policy updatePolicy = policyMapper.updatePolicyDTOToPolicy(updatePolicyDTO);
            Policy resultPolicy = policyService.updatePolicy(updatePolicy);
            return policyMapper.policyToPolicyDTO(resultPolicy);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "-1#invalid update policy data");
        } catch (AlreadyExistsException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "-1#unique update policy data already in use");
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LimitRequests
    @Authorize("security.authorize.administration.deletePolicyByUUID")
    @DeleteMapping("/policies/{UUID}")
    public PolicyDTO deletePolicyByUUID(HttpServletRequest request, @PathVariable String UUID) {
        LOGGER.info("HTTP-REQ: DELETE {}/policies{} # FROM: {}", BASE_URL, UUID, request.getRemoteHost());

        try {
            Validator.validatePattern(Policy.PATTERN_UUID, UUID);
            Policy deletedPolicy = policyService.deletePolicyByUUID(UUID);
            return policyMapper.policyToPolicyDTO(deletedPolicy);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "-1#invalid uuid pattern");
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "-1#policy not found");
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
