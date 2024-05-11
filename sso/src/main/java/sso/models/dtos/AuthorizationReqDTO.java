package sso.models.dtos;

import org.json.JSONObject;

import java.util.stream.Collectors;

public class AuthorizationReqDTO {

    private String subject;
    private String object;
    private String action;

    public String getSubject() {
        return subject;
    }

    public AuthorizationReqDTO(String subject, String object, String action) {
        this.subject = subject;
        this.object = object;
        this.action = action;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return  "{ " +
                "'subject': '" + subject + "', " +
                "'object': '" + object + "', " +
                "'action': '" + action + "' " +
                "}";
    }
}
