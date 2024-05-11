package sso.models.dtos;

import sso.models.entities.Policy;
import sso.models.entities.PolicyRule;
import sso.utils.exceptions.ValidationException;
import sso.utils.security.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PolicyDTO {

    protected String UUID;
    protected String name;
    protected List<String> rules;

    public PolicyDTO(String UUID, String name, List<String> rules) {
        this.UUID = UUID;
        this.name = name;
        this.rules = rules;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public void validate() throws ValidationException {
        Validator.validatePattern(Policy.PATTERN_UUID, this.UUID);
        Validator.validatePattern(Policy.PATTERN_POLICY_NAME, this.name);
        for (String ruleExp : this.rules) {
            Validator.validatePattern(PolicyRule.PATTERN_POLICYRULE_EXPRESSION, ruleExp);
        }
    }

    @Override
    public String toString() {
        return  "{ " +
                "'UUID': '" + UUID + "', " +
                "'name': '" + name + "', " +
                "'rules': '" + rules.stream().map(Object::toString).collect(Collectors.joining(";")) + "' " +
                "}";
    }
}
