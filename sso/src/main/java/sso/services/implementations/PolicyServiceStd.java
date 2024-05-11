package sso.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import sso.models.entities.Policy;
import sso.persistance.interfaces.PolicyDAO;
import sso.services.interfaces.PolicyService;
import sso.utils.exceptions.AlreadyExistsException;
import sso.utils.exceptions.NotFoundException;
import sso.utils.exceptions.PersistenceException;
import sso.utils.exceptions.ServiceException;

import java.util.List;

@Service
public class PolicyServiceStd implements PolicyService {

    private final PolicyDAO policyDAO;

    @Autowired
    public PolicyServiceStd(PolicyDAO policyDAO) {
        this.policyDAO = policyDAO;
    }

    @Override
    public List<Policy> getAllPolicies() throws ServiceException {
        try {
            return policyDAO.getAllPolicies();
        } catch (PersistenceException e) {
            throw new ServiceException("PolicyServiceStd.getAllPolicies() > " + e.getMessage());
        }
    }

    @Override
    public Policy createPolicy(Policy policy) throws AlreadyExistsException, ServiceException {
        try {
            return policyDAO.createPolicy(policy);
        } catch (PersistenceException e) {
            throw new ServiceException("PolicyServiceStd.createPolicy() > " + e.getMessage());
        }
    }

    @Override
    public Policy updatePolicy(Policy policy) throws NotFoundException, AlreadyExistsException, ServiceException {
        Policy targetPolicy;

        try {
            // Get targetPolicy
            targetPolicy = policyDAO.getPolicyByUUID(policy.getUUID());

            // Update policy
            if (policy.getName() != null) {
                targetPolicy.setName(policy.getName());
            }
            if (policy.getRules() != null) {
                targetPolicy.setRules(policy.getRules());
            }

            // Save policy
            return policyDAO.updatePolicy(targetPolicy);
        } catch (PersistenceException e) {
            throw new ServiceException("PolicyServiceStd.updatePolicy() > " + e.getMessage());
        }
    }

    @Override
    public Policy deletePolicyByUUID(String UUID) throws ServiceException {
        try {
            return policyDAO.deletePolicyByUUID(UUID);
        } catch (PersistenceException e) {
            throw new ServiceException("PolicyServiceStd.deletePolicyByName() > " + e.getMessage());
        }
    }

    @Override
    public Boolean evaluate(StandardEvaluationContext context) throws ServiceException {
        List<Policy> policies;

        System.out.println("##### Starting evaluation process #####");
        System.out.println("-- Context: " + context.toString());
        try {
            policies = policyDAO.getAllPolicies();
            if (policies.isEmpty()) {
                System.out.println("No policies defined");
                return false;
            }

            for (Policy policy : policies) {
                System.out.print("+ Evaluate Policy: " + policy.getName());
                if (policy.evaluate(context)) {
                    System.out.println(" ---> true");
                    return true;
                } else {
                    System.out.println(" ---> false");
                }
            }
            System.out.println("#######################################");
            return false;
        } catch (PersistenceException e) {
            throw new ServiceException("PolicyServiceStd.evaluate() > " + e.getMessage());
        }
    }
}
