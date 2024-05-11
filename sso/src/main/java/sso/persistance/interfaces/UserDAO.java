package sso.persistance.interfaces;

import sso.models.entities.User;
import sso.utils.exceptions.PersistenceException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.AlreadyExistsException;

import java.util.List;

public interface UserDAO {

    /**
     * Returns all existing users
     *
     * @return  All existing users
     * @throws  PersistenceException if an exception occured which has no specific handling
     */
    List<User> getAllUsers() throws PersistenceException;

    /**
     * Finds a user by the given UUID
     *
     * @param UUID  UUID of the user to fetch
     * @return  Returns the user with the specified UUID if exists
     * @throws  NotFoundException if no user exists with the given UUID
     * @throws  PersistenceException if an exception occured which has no specific handling
     */
    User getUserByUUID(String UUID) throws PersistenceException, NotFoundException;

    /**
     * Finds a user by the given username
     *
     * @param Username  Username of the user to fetch
     * @return  Returns the user with the specified username if exists
     * @throws NotFoundException if no user exists with the given username
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    User getUserByUsername(String Username) throws PersistenceException, NotFoundException;

    /**
     * Persists the specified user
     *
     * @param user  User to persist
     * @return      Returns the persisted user on success
     * @throws  AlreadyExistsException if the user has a property which is required to be unique and
     *                                 is already used by another user
     * @throws  PersistenceException if an exception occured which has no specific handling
     */
    User createUser(User user) throws PersistenceException, AlreadyExistsException;

    /**
     * Updates all values of a user with the values in the user parameter. The user is defined
     * by the UUID of the user parameter
     *
     * @param user  User with updated values (UUID defines the user -> cannot be updated)
     * @return  Returns the updated user
     * @throws NotFoundException if no user exists with the UUID in the user parameter
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    User updateUserByUUID(User user) throws PersistenceException, NotFoundException;

    /**
     * Deletes a persisted user
     *
     * @param UUID  UUID of the user to delete
     * @return  Returns the deleted user on success
     * @throws NotFoundException if the user to delete does not exist
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    User deleteUserByUUID(String UUID) throws PersistenceException, NotFoundException;

    /**
     * Increases the failed authentication count by 1
     *
     * @param user  The user whose failed authentication count should be increased
     * @return  Returns the count of failed authentications after increasing
     * @throws NotFoundException if the user does not exist
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    Long increaseFailedAuth(User user) throws PersistenceException, NotFoundException;

    /**
     * Sets the failed authentication count of the given user to 0
     *
     * @param user  The user whose failed authentication count should be resetted
     * @throws NotFoundException if the user does not exist
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    void resetFailedAuth(User user) throws PersistenceException, NotFoundException;
}
