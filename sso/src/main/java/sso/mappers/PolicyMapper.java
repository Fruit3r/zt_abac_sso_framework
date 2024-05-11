package sso.mappers;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import sso.models.dtos.NewPolicyDTO;
import sso.models.dtos.PolicyDTO;
import sso.models.dtos.UpdatePolicyDTO;
import sso.models.entities.Policy;
import sso.models.entities.PolicyRule;
import sso.utils.exceptions.ValidationException;
import sso.utils.security.Validator;

import java.util.ArrayList;
import java.util.List;

@Component
public class PolicyMapper {

    public Policy policyDTOToPolicy(PolicyDTO policyDTO) throws ValidationException {
        policyDTO.validate();
        return new Policy(
            policyDTO.getUUID(),
            policyDTO.getName(),
            policyDTO.getRules(),
        null
        );
    }

    public Policy newPolicyDTOToPolicy(NewPolicyDTO newPolicyDTO) throws ValidationException {
        newPolicyDTO.validate();
        return new Policy(
            newPolicyDTO.getUUID(),
            newPolicyDTO.getName(),
            newPolicyDTO.getRules(),
        null
        );
    }

    public Policy updatePolicyDTOToPolicy(UpdatePolicyDTO updatePolicyDTO) throws ValidationException {
        updatePolicyDTO.validate();
        return new Policy(
            updatePolicyDTO.getUUID(),
            updatePolicyDTO.getName(),
            updatePolicyDTO.getRules(),
        null
        );
    }

    public PolicyDTO policyToPolicyDTO(Policy policy) {
        return new PolicyDTO(
            policy.getUUID(),
            policy.getName(),
            policy.getRulesAsStrings()
        );
    }

    public List<PolicyDTO> policiesToPolicyDTOs(List<Policy> policies) {
        List<PolicyDTO> policyDTOs = new ArrayList<>();

        for (Policy policy : policies) {
            policyDTOs.add(this.policyToPolicyDTO(policy));
        }

        return policyDTOs;
    }
}
