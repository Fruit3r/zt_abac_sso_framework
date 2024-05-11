package sso.models.dtos;

import sso.models.entities.Policy;
import sso.models.entities.PolicyRule;
import sso.utils.exceptions.ValidationException;
import sso.utils.security.Validator;

import java.util.List;

public class NewPolicyDTO extends PolicyDTO {

    public NewPolicyDTO(String UUID, String name, List<String> rules) {
        super(UUID, name, rules);
    }

    @Override
    public void validate() throws ValidationException {
        Validator.validatePattern(Policy.PATTERN_POLICY_NAME, this.name);

        if (this.rules != null) {
            for (String ruleExp : this.rules) {
                Validator.validatePattern(PolicyRule.PATTERN_POLICYRULE_EXPRESSION, ruleExp);
            }
        }
    }
}
