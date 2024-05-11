package sso.models.entities;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Policy {

    public static final String PATTERN_UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public static final String PATTERN_POLICY_NAME = "^\\w{1,100}$";
    private String UUID;
    private String name;
    private List<PolicyRule> rules;

    public Policy(String name) {
        this.name = name;
        this.rules = new ArrayList<>();
    }

    public Policy(String UUID, String name, List<PolicyRule> rules) {
        this.UUID = UUID;
        this.name = name;
        this.rules = rules;
    }

    public Policy(String UUID, String name, List<String> rulesStrings, String non) {
        this.UUID = UUID;
        this.name = name;
        this.rules = parseRulesAsStringsToPolicyRules(rulesStrings);
    }

    public Policy(String UUID, String name, String rulesString) {
        this.UUID = UUID;
        this.name = name;
        this.rules = parseRulesAsStringToPolicyRules(rulesString);
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

    public List<PolicyRule> getRules() {
        return rules;
    }

    public void setRules(List<PolicyRule> rules) {
        this.rules = rules;
    }

    public ArrayList<String> getRulesAsStrings() {
        ArrayList<String> rulesExps = new ArrayList<>();

        for (PolicyRule policyRule : this.rules) {
            rulesExps.add(policyRule.getExpression());
        }

        return rulesExps;
    }

    public String getRulesAsString() {
        List<String> rulesStrings = new ArrayList<>();

        for (PolicyRule policyRule : this.rules) {
            rulesStrings.add(policyRule.getExpression());
        }

        return rulesStrings.stream().map(Object::toString).collect(Collectors.joining(";"));
    }

    public void addRule(PolicyRule rule) {
        this.rules.add(rule);
    }

    public PolicyRule removeRule(PolicyRule rule) {
        if (this.rules.remove(rule)) {
            return rule;
        } else {
            return null;
        }
    }

    public Boolean evaluate(StandardEvaluationContext context) {
        if (this.rules.isEmpty()) { return false; } // Empty policy != policy

        for (PolicyRule rule : rules) {
            if (!rule.evaluate(context)) {
                return false;
            }
        }
        return true;
    }

    private List<PolicyRule> parseRulesAsStringToPolicyRules(String rulesString) {
        List<PolicyRule> rules = new ArrayList<>();

        if (rulesString == null) {
            return null;
        }

        // Prevent adding empty element
        if (rulesString.length() > 0) {
            for (String rule : rulesString.split(";")) {
                rules.add(new PolicyRule(rule));
            }
        }
        return rules;
    }

    private List<PolicyRule> parseRulesAsStringsToPolicyRules(List<String> rulesStrings) {
        List<PolicyRule> rules = new ArrayList<>();

        if (rulesStrings == null) {
            return null;
        }

        for (String rule : rulesStrings) {
            rules.add(new PolicyRule(rule));
        }
        return rules;
    }
}
