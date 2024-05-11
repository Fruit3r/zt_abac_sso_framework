package sso.utils.security;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationUtil {

    public AuthorizationUtil() {}

    public StandardEvaluationContext buildContext(JSONObject subject, JSONObject object, JSONObject action) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

        subject.keySet().forEach(key -> {
            if (!(subject.get(key) instanceof JSONObject || subject.get(key) instanceof JSONArray)) {
                evaluationContext.setVariable("subject_" + key, subject.get(key));
            }
        });

        object.keySet().forEach(key -> {
            if (!(object.get(key) instanceof JSONObject || object.get(key) instanceof JSONArray)) {
                evaluationContext.setVariable("object_" + key, object.get(key));
            }
        });

        action.keySet().forEach(key -> {
            if (!(action.get(key) instanceof JSONObject || action.get(key) instanceof JSONArray)) {
                evaluationContext.setVariable("action_" + key, action.get(key));
            }
        });

        return evaluationContext;
    }
}
