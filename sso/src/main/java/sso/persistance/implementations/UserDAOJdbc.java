package sso.persistance.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import sso.models.entities.User;
import sso.persistance.interfaces.UserDAO;
import sso.utils.exceptions.AlreadyExistsException;
import sso.utils.exceptions.BlockedException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.PersistenceException;
import sso.utils.security.PasswordEncoderFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.List;
import java.util.Map;


@Repository
public class UserDAOJdbc implements UserDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final BCryptPasswordEncoder passwordEncoder = PasswordEncoderFactory.getPasswordEncoder();
    private final JdbcTemplate jdbcTemplate;
    public static final String TABLE_NAME = "Users";

    @Autowired
    public UserDAOJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() throws PersistenceException {
        final String SQL = "SELECT * FROM " + TABLE_NAME + ";";
        List<User> resultUsers;

        try {
            resultUsers = jdbcTemplate.query(SQL, this::mapRow);
            return resultUsers;
        } catch (RuntimeException e) {
            throw new PersistenceException("UserDAOJdbc.getAllUsers # MSG: exception occured during fetching all users # REASON: " + e.getMessage());
        }
    }

    @Override
    public User getUserByUUID(String UUID) throws PersistenceException, NotFoundException {
        final String SQL = "SELECT * FROM " + TABLE_NAME + " WHERE UUID::text = ?;";
        List<User> resultUsers;

        try {
            resultUsers = jdbcTemplate.query(SQL, this::mapRow, UUID);
        } catch (RuntimeException e) {
            throw new PersistenceException("UserDAOJdbc.getUserByUUID() # MSG: exception occured during fetching user with UUID '" + UUID + "' # REASON: " + e.getMessage());
        }

        if (resultUsers.isEmpty()) {
            throw new NotFoundException("UserDAOJdbc.getUserByUUID() # MSG: user with UUID '" + UUID + "' not found");
        }
        return resultUsers.get(0);
    }

    @Override
    public User getUserByUsername(String username) throws NotFoundException, PersistenceException {
        final String SQL = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?;";
        List<User> resultUsers;

        try {
            resultUsers = jdbcTemplate.query(SQL, this::mapRow, username);
        } catch (RuntimeException e) {
            throw new PersistenceException("UserDAOJdbc.getUserByUsername() # MSG: exception occured during fetching user with username '" + username + "' # REASON: " + e.getMessage());
        }

        if (resultUsers.isEmpty()) {
            throw new NotFoundException("UserDAOJdbc.getUserByUsername() # MSG: user with username '" + username + "' not found");
        }
        return resultUsers.get(0);
    }
    @Override
    public User createUser(User user) throws AlreadyExistsException, PersistenceException {
        final String SQL = "INSERT INTO " + TABLE_NAME + "(username, password, email, blocked, failedauth, attributes) VALUES (?, ?, ?, ?, ?, ?);";
        User resultUser;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPasswordEncoded());
                stmt.setString(3, user.getEmail());
                stmt.setBoolean(4, user.isBlocked());
                stmt.setLong(5, user.getFailedAuth());
                stmt.setString(6, user.getAttributesAsString());
                return stmt;
            }, keyHolder);

            // Create user from finally generated values
            Map<String, Object> keys = keyHolder.getKeys();
            resultUser = new User(
                keys.get("UUID").toString(),
                keys.get("username").toString(),
                keys.get("password").toString(),
                keys.get("email").toString(),
                Timestamp.valueOf(keys.get("created").toString()),
                Boolean.valueOf(keys.get("blocked").toString()),
                Long.valueOf(keys.get("failedauth").toString()),
                keys.get("attributes").toString()
            );
            return resultUser;
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistsException("UserDAOJdbc # MSG: a unique constraint has been violated # REASON: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new PersistenceException("UserDAOJdbc # MSG: exception occured during creating user with username '" + user.getUsername() + "' # REASON: " + e.getMessage());
        }
    }

    @Override
    public User updateUserByUUID(User user) throws PersistenceException, NotFoundException {
        final String SQL = "UPDATE " + TABLE_NAME + " SET username = ?, password = ?, email = ?, created = ?, blocked = ?, failedauth = ?, attributes = ? WHERE UUID::text = ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPasswordEncoded());
                stmt.setString(3, user.getEmail());
                stmt.setTimestamp(4, user.getCreated());
                stmt.setBoolean(5, user.isBlocked());
                stmt.setLong(6, user.getFailedAuth());
                stmt.setString(7, user.getAttributesAsString());
                stmt.setString(8, user.getUUID());
                return stmt;
            }, keyHolder);

            // Create user from finally generated values
            Map<String, Object> keys = keyHolder.getKeys();
            return new User(
                    keys.get("UUID").toString(),
                    keys.get("username").toString(),
                    keys.get("password").toString(),
                    keys.get("email").toString(),
                    Timestamp.valueOf(keys.get("created").toString()),
                    Boolean.valueOf(keys.get("blocked").toString()),
                    Long.valueOf(keys.get("failedauth").toString()),
                    keys.get("attributes").toString()
            );
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistsException("UserDAOJdbc # MSG: a unique constraint has been violated # REASON: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new PersistenceException("UserDAOJdbc # MSG: exception occured during creating user with username '" + user.getUsername() + "' # REASON: " + e.getMessage());
        }
    }

    @Override
    public User deleteUserByUUID(String UUID) throws PersistenceException, NotFoundException {
        final String SQL = "DELETE FROM " + TABLE_NAME + " WHERE UUID::text = ?;";
        User deletedUser;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, UUID);
                return stmt;
            }, keyHolder);

            // Create user from finally generated values
            Map<String, Object> keys = keyHolder.getKeys();
            deletedUser = new User(
                    keys.get("UUID").toString(),
                    keys.get("username").toString(),
                    keys.get("password").toString(),
                    keys.get("email").toString(),
                    Timestamp.valueOf(keys.get("created").toString()),
                    Boolean.valueOf(keys.get("blocked").toString()),
                    Long.valueOf(keys.get("failedauth").toString()),
                    keys.get("attributes").toString()
            );
        } catch (RuntimeException e) {
            throw new PersistenceException("UserDAOJdbc.deleteUserByUUID() # MSG: exception occured during deleting user with UUID '" + UUID + "' # REASON: " + e.getMessage());
        }
        // Throw NotFoundException if no user has been deleted
        if (keyHolder.getKeys() == null) {
            throw new NotFoundException("UserDAOJdbc.deleteUserByUUID() # MSG: user with UUID '" + UUID + "' not found");
        }
        return deletedUser;
    }

    @Override
    public Long increaseFailedAuth(User user) throws PersistenceException, NotFoundException, BlockedException {
        final String SQL = "UPDATE " + TABLE_NAME + " SET failedauth = failedauth + 1 WHERE UUID::text = ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, user.getUUID());
                return stmt;
            }, keyHolder);
        } catch (RuntimeException e) {
            throw new PersistenceException("UserDAOJdbc.increaseFailedAuth() # MSG: exception occured while increasing failed authentication count of user with UUID '" + user.getUUID() + "' # REASON: " + e.getMessage());
        }
        // Throw NotFoundException if user has not been found
        if (keyHolder.getKeys() == null) {
            throw new NotFoundException("UserDAOJdbc.increaseFailedAuth() # MSG: user with UUID '" + user.getUUID() + "' not found");
        }

        return Long.valueOf(keyHolder.getKeys().get("failedauth").toString());
    }

    @Override
    public void resetFailedAuth(User user) throws PersistenceException, NotFoundException {
        final String SQL = "UPDATE " + TABLE_NAME + " SET failedauth = 0 WHERE UUID::text = ?;";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, user.getUUID());
                return stmt;
            }, keyHolder);
        } catch (RuntimeException e) {
            throw new PersistenceException("UserDAOJdbc.resetFailedAuth() # MSG: exception occured while resetting failed authentication count of user with UUID '" + user.getUUID() + "' # REASON: " + e.getMessage());
        }
        // Throw NotFoundException if user has not been found
        if (keyHolder.getKeys() == null) {
            throw new NotFoundException("UserDAOJdbc.resetFailedAuth() # MSG: user with UUID '" + user.getUUID() + "' not found");
        }
    }

    private User mapRow(ResultSet resultSet, int i) throws SQLException {
        String UUID = resultSet.getString("UUID");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String email = resultSet.getString("email");
        Timestamp created = resultSet.getTimestamp("created");
        Boolean blocked = resultSet.getBoolean("blocked");
        Long failedAuth = resultSet.getLong("failedAuth");
        String attributes = resultSet.getString("attributes");

        final User user = new User(UUID, username, password, email, created, blocked, failedAuth, attributes);
        return user;
    }
}
