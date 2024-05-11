package sso.services.interfaces;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import sso.models.entities.Policy;
import sso.utils.exceptions.AlreadyExistsException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.ServiceException;

import java.util.List;

public interface PolicyService {

    /**
     * Fetches all policies
     *
     * @return  Returns all policies
     * @throws ServiceException if an exception occured which has no specific handling
     */
    List<Policy> getAllPolicies() throws ServiceException;

    /**
     * Creates a new policy
     *
     * @param policy    New policy to create
     * @return  Returns the created policy
     * @throws AlreadyExistsException if the policy already exists (policy with same name or UUID exists)
     * @throws ServiceException  if an exception occured which has no specific handling
     */
    Policy createPolicy(Policy policy) throws AlreadyExistsException, ServiceException;

    /**
     * Updates the policy defined by the UUID inside the policy parameter
     *
     * @param policy    Policy to update
     * @return  Returns the updated policy
     * @throws NotFoundException if no policy with the given UUID inside the policy paramter has been found
     * @throws AlreadyExistsException if the policy already exists (policy with same name or UUID exists)
     * @throws ServiceException if an exception occured which has no specific handling
     */
    Policy updatePolicy(Policy policy) throws NotFoundException, AlreadyExistsException, ServiceException;

    /**
     * Deletes the policy with the given UUID
     *
     * @param UUID  UUID of the policy to delete
     * @return  Returns the deleted policy on success
     * @throws NotFoundException if no policy with the given UUID exists
     * @throws ServiceException if an exception occured which has no specific handling
     */
    Policy deletePolicyByUUID(String UUID) throws NotFoundException, ServiceException;

    /**
     * Evaluates if the subject in the given context is authorized. In other words if a
     * policy exists where all rules evaluate to true
     *
     * @param context   Context to evaluate policies under
     * @return  Returns true if a policy where all rules evaluate to true under the given
     *          context exists, else false
     * @throws ServiceException if an exception occured which has no specific handling
     */
    Boolean evaluate(StandardEvaluationContext context);
}
