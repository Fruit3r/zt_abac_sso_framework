package sso.services.implementations;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sso.models.entities.User;
import sso.persistance.interfaces.UserDAO;
import sso.services.interfaces.UserService;
import sso.utils.exceptions.AlreadyExistsException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.PersistenceException;
import sso.utils.exceptions.ServiceException;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceStd implements UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserServiceStd(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public List<User> getAllUsers() throws ServiceException {
        try {
            return userDAO.getAllUsers();
        } catch (PersistenceException e) {
            throw new ServiceException("UserServiceStd.getAllUsers() > " + e.getMessage());
        }
    }

    @Override
    public User getUserByUUID(String UUID) throws NotFoundException, ServiceException {
        try {
            return userDAO.getUserByUUID(UUID);
        } catch (PersistenceException e) {
            throw new ServiceException("UserServiceStd.getUserByUUID() > " + e.getMessage());
        }
    }

    @Override
    public User getUserByUsername(String username) throws NotFoundException, ServiceException {
        try {
            return userDAO.getUserByUsername(username);
        } catch (PersistenceException e) {
            throw new ServiceException("UserServiceStd.getUserByUsername() > " + e.getMessage());
        }
    }

    @Override
    public User createUser(User user) throws AlreadyExistsException, ServiceException {
        try {
            user.setFailedAuth(0L);
            return userDAO.createUser(user);
        } catch (PersistenceException e) {
            throw new ServiceException("UserServiceStd.createUser() > " + e.getMessage());
        }
    }

    @Override
    public User updateUser(User user) throws AlreadyExistsException, NotFoundException, ServiceException {
        User targetUser;
        
        try {
            // Get user
            targetUser = userDAO.getUserByUUID(user.getUUID());

            // Update username
            if (user.getUsername() != null) {
                targetUser.setUsername(user.getUsername());
            }
            // Update password
            if (user.getPasswordEncoded() != null) {
                targetUser.setPasswordEncoded(user.getPasswordEncoded());
            }
            // Update blocked
            if (user.isBlocked() != null) {
                targetUser.setBlocked(user.isBlocked());
            }
            // Update attributes
            for (Object key : user.getAttributes().keySet()) {
                String keyStr = (String) key;
                Object value = user.getAttributes().get(keyStr);

                if (value == JSONObject.NULL) {
                    // Delete if value is null
                    targetUser.getAttributes().remove(keyStr);
                } else {
                    // Create or update if value is set
                    targetUser.getAttributes().put(keyStr, value);
                }
            }
            // Save user
           return userDAO.updateUserByUUID(targetUser);
        } catch (PersistenceException e) {
            throw new ServiceException("UserServiceStd.updateAttributes() > " + e.getMessage());
        }
    }

    @Override
    public User deleteUserByUUID(String UUID) throws NotFoundException, ServiceException {
        try {
            return userDAO.deleteUserByUUID(UUID);
        } catch (PersistenceException e) {
            throw new ServiceException("UserServiceStd.deleteUserByUUID() > " + e.getMessage());
        }
    }

    @Override
    public void increaseFailedAuth(User user) {
        try {
            userDAO.increaseFailedAuth(user);
        } catch (PersistenceException e) {
            throw new ServiceException("UserServiceStd.increaseFailedAuth() > " + e.getMessage());
        }
    }

    @Override
    public void resetFailedAuth(User user) {
        try {
            userDAO.resetFailedAuth(user);
        } catch (PersistenceException e) {
            throw new ServiceException("UserServiceStd.resetFailedAuth() > " + e.getMessage());
        }
    }
}
