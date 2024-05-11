package sso.persistance.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sso.models.entities.Policy;
import sso.models.entities.PolicyRule;
import sso.models.entities.User;
import sso.persistance.interfaces.PolicyDAO;
import sso.utils.exceptions.AlreadyExistsException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.PersistenceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class PolicyDAOJdbc implements PolicyDAO {

    private final JdbcTemplate jdbcTemplate;
    public static final String TABLE_NAME = "Policies";

    @Autowired
    public PolicyDAOJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Policy> getAllPolicies() throws PersistenceException {
        final String SQL = "SELECT * FROM " + TABLE_NAME + ";";

        try {
            return jdbcTemplate.query(SQL, this::mapRow);
        } catch (RuntimeException e) {
            throw new PersistenceException("PolicyDAOJdbc.getAllPolicies # MSG: exception occured during fetching all policies # REASON: " + e.getMessage());
        }
    }

    @Override
    public Policy getPolicyByUUID(String UUID) throws NotFoundException, PersistenceException {
        final String SQL = "SELECT * FROM " + TABLE_NAME + " WHERE UUID::text = ?;";
        List<Policy> resultPolicies;

        try {
            resultPolicies = jdbcTemplate.query(SQL, this::mapRow, UUID);
        } catch (RuntimeException e) {
            throw new PersistenceException("PolicyDAOJdbc.getPolicyByUUID() # MSG: exception occured during fetching policy with UUID '" + UUID + "' # REASON: " + e.getMessage());
        }

        if (resultPolicies.isEmpty()) {
            throw new NotFoundException("PolicyDAOJdbc.getPolicyByUUID() # MSG: policy with UUID '" + UUID + "' not found");
        }
        return resultPolicies.get(0);
    }

    @Override
    public Policy createPolicy(Policy policy) throws AlreadyExistsException, PersistenceException {
        final String SQL = "INSERT INTO " + TABLE_NAME + "(name, rules) VALUES (?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, policy.getName());
                stmt.setString(2, policy.getRulesAsString());
                return stmt;
            }, keyHolder);

            // Create policy from finally generated values
            Map<String, Object> keys = keyHolder.getKeys();
            return new Policy(
                keys.get("UUID").toString(),
                keys.get("name").toString(),
                keys.get("rules").toString()
            );
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistsException("PolicyDAOJdbc.createPolicy() # MSG: a unique constraint has been violated # REASON: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new PersistenceException("PolicyDAOJdbc.createPolicy() # MSG: exception occured during creating policy with UUID '" + policy.getUUID() + "' # REASON: " + e.getMessage());
        }
    }

    @Override
    public Policy updatePolicy(Policy policy) throws AlreadyExistsException, NotFoundException, PersistenceException {
        final String SQL = "UPDATE " + TABLE_NAME + " SET name = ?, rules = ? WHERE UUID::text = ?;";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, policy.getName());
                stmt.setString(2, policy.getRulesAsString());
                stmt.setString(3, policy.getUUID());
                return stmt;
            }, keyHolder);

            // Create policy from finally generated values
            Map<String, Object> keys = keyHolder.getKeys();
            return new Policy(
                    keys.get("UUID").toString(),
                    keys.get("name").toString(),
                    keys.get("rules").toString()
            );
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistsException("PolicyDAOJdbc.updatePolicy() # MSG: a unique contraint has been violated # REASON: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new PersistenceException("PolicyDAOJdbc.updatePolicy() # MSG: exception occured during updating policy with UUID '" + policy.getUUID() + "' # REASON: " + e.getMessage());
        }
    }

    @Override
    public Policy deletePolicyByUUID(String UUID) throws NotFoundException, PersistenceException {
        final String SQL = "DELETE FROM " + TABLE_NAME + " WHERE UUID::text = ?;";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, UUID);
                return stmt;
            }, keyHolder);
        } catch (RuntimeException e) {
            throw new PersistenceException("PolicyDAOJdbc.deletePolicyByName() # MSG: exception occured during deletion of policy with UUID '" + UUID + "' # REASON: " + e.getMessage());
        }
        // Throw NotFoundException if no policy has been deleted
        if (keyHolder.getKeys() == null) {
            throw new NotFoundException("PolicyDAOJdbc.deletePolicyByName() # MSG: policy with UUID '" + UUID + "' not found");
        }

        Map<String, Object> keys = keyHolder.getKeys();
        return new Policy(
                keys.get("UUID").toString(),
                keys.get("name").toString(),
                keys.get("rules").toString()
        );
    }

    private Policy mapRow(ResultSet resultSet, int i) throws SQLException {
        String UUID = resultSet.getString("UUID");
        String name = resultSet.getString("name");
        String rulesString = resultSet.getString("rules");

        final Policy policy = new Policy(UUID, name, rulesString);
        return policy;
    }
}
