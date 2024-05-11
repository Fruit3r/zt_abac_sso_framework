package sso.persistance.interfaces;

import sso.models.entities.Policy;
import sso.utils.exceptions.AlreadyExistsException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.PersistenceException;

import java.util.List;

public interface PolicyDAO {

    /**
     * Fetches all policies
     *
     * @return  Returns all policies
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    List<Policy> getAllPolicies() throws PersistenceException;

    /**
     * Fetches the policy with the given UUID
     *
     * @param UUID  UUID of policy to fetch
     * @return  Returns the found policy
     * @throws NotFoundException if no policy has been found with the given UUID
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    Policy getPolicyByUUID(String UUID) throws NotFoundException, PersistenceException;

    /**
     * Creates a new policy by the given policy parameter
     *
     * @param policy    New policy
     * @return  Returns the created policy
     * @throws AlreadyExistsException if policy parameter has a unique property which already exists
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    Policy createPolicy(Policy policy) throws AlreadyExistsException, PersistenceException;

    /**
     * Updates the policy defined by the UUID in the given policy parameter. Values != null in the
     * policy parameter are updated (replaced)
     *
     * @param policy    Updated policy
     * @return  Returns the updated policy
     * @throws AlreadyExistsException if policy parameter has a unique property which already exists
     * @throws NotFoundException if the policy to update (defined by UUID) has not been found
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    Policy updatePolicy(Policy policy) throws AlreadyExistsException, NotFoundException, PersistenceException;

    /**
     * Deletes the policy with the given UUID
     *
     * @param UUID  UUID of the policy to delete
     * @return  Returns the deleted policy
     * @throws NotFoundException if no policy with the given UUID has been found
     * @throws PersistenceException if an exception occured which has no specific handling
     */
    Policy deletePolicyByUUID(String UUID) throws NotFoundException, PersistenceException;
}
