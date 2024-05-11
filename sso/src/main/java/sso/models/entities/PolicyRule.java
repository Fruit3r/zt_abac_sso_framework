package sso.models.entities;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


public class PolicyRule {

    public static final String PATTERN_POLICYRULE_EXPRESSION = "^((#(subject|object|action))_[a-zA-Z][a-zA-Z0-9]*)( )*(==|!=|<=|>=|<|>)( )*(((#(subject|object|action))_[a-zA-Z][a-zA-Z0-9]*)|\"[a-zA-Z0-9_@./#&+-]*\"|[0-9]+|(true|false))$";
    private ExpressionParser parser;
    private String ruleExp;

    public PolicyRule(String ruleExp) {
        this.ruleExp = ruleExp;
        this.parser = new SpelExpressionParser();
    }

    public String getExpression() {
        return this.ruleExp;
    }

    public Boolean evaluate(StandardEvaluationContext context) {
       return Boolean.valueOf(parser.parseExpression(this.ruleExp).getValue(context).toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            return this.ruleExp.equals(((PolicyRule) obj).ruleExp);
        }
        return false;
    }
}
