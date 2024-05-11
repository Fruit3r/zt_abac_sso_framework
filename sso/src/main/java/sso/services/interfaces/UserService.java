package sso.services.interfaces;

import org.json.JSONObject;
import sso.models.entities.User;
import sso.utils.exceptions.*;

import java.util.List;

public interface UserService {

    /**
     * Returns all users
     *
     * @return Returns all users
     * @throws ServiceException if an exception occured which has no specific handling
     */
    List<User> getAllUsers() throws ServiceException;

    /**
     * Fetches the user with the specified UUID
     *
     * @param UUID  UUID of the user to fetch
     * @return  Returns the user with the specified UUID
     * @throws NotFoundException if user with specified UUID does not exist
     * @throws ServiceException if an exception occured which has no specific handling
     */
    User getUserByUUID(String UUID) throws ServiceException, NotFoundException;

    /**
     * Fetches the user with the specified UUID
     *
     * @param username  Username of the user to fetch
     * @return  Returns the user with the specified username
     * @throws NotFoundException if user with specified username does not exist
     * @throws ServiceException if an exception occured which has no specific handling
     */
    User getUserByUsername(String username) throws ServiceException, NotFoundException;

    /**
     * Persists the specified user
     *
     * @param user  User to persist
     * @return  Returns the persisted user on success
     * @throws AlreadyExistsException if one of the unique attributes of the user
     *                                to persist already exists for another user
     * @throws ServiceException if an exception occured which has no specific handling
     */
    User createUser(User user) throws ServiceException;

    /**
     * Updates the user with the given UUID according to the given user parameter
     * Values = null will not be updated!
     * Special update specifications:
     * Attributes:
     * (attributes structure -> { "Attributname1": <Attributevalue1>, "Attributname2": <Attributevalue2>, ... }
     * 1. Create Attribute: Attributename does not exist
     * 2. Update Attribute: Attributename does already exist -> new value in attributes will be used
     * 3. Delete Attribute: Attributename exists + Attributevalue == null
     *
     * @param user  User object with updated values
     * @return
     */
    User updateUser(User user) throws AlreadyExistsException, NotFoundException, ServiceException;

    /**
     * Deletes a persisted user
     *
     * @param UUID  UUID of the user to delete
     * @return  Returns the deleted user on success
     * @throws NotFoundException if the user to delete does not exist
     * @throws ServiceException if an exception occured which has no specific handling
     */
    User deleteUserByUUID(String UUID) throws ServiceException, NotFoundException;

    void increaseFailedAuth(User user) throws ServiceException, NotFoundException, BlockedException;

    void resetFailedAuth(User user) throws ServiceException, NotFoundException;
}
