package sso.models.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class User {

    public static final String PATTERN_UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public static final String PATTERN_USERNAME = "^[a-zA-Z0-9_?!-]{3,100}$";
    public static final String PATTERN_PASSWORD = "^.{8,100}$";
    public static final String PATTERN_EMAIL = "^[a-zA-Z0-9!#$%&.+]{1,64}@[a-z_.]{1,255}$";

    private String UUID;
    private String username;
    private String passwordEncoded;
    private String email;
    private Timestamp created;
    private Boolean blocked;
    private Long failedAuth;
    private JSONObject attributes;


    public User(String UUID, String username, String passwordEncoded, String email, Timestamp created, Boolean blocked, Long failedAuth, JSONObject attributes) {
        this.UUID = UUID;
        this.username = username;
        this.passwordEncoded = passwordEncoded;
        this.email = email;
        this.created = created;
        this.blocked = blocked;
        this.failedAuth = failedAuth;
        this.attributes = attributes;
    }

    public User(String UUID, String username, String passwordEncoded, String email, Timestamp created, Boolean blocked, Long failedAuth, String attributesAsString) {
        this(UUID, username, passwordEncoded, email, created, blocked, failedAuth, new JSONObject(attributesAsString));
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordEncoded() {
        return passwordEncoded;
    }

    public void setPasswordEncoded(String passwordEncoded) {
        this.passwordEncoded = passwordEncoded;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Long getFailedAuth() {
        return failedAuth;
    }

    public void setFailedAuth(Long failedAuth) {
        this.failedAuth = failedAuth;
    }

    public JSONObject getAttributes() {
        return attributes;
    }

    public void setAttributes(JSONObject attributes) {
        this.attributes = attributes;
    }

    public void setAttributesAsString(String attributesString) {
        this.attributes = new JSONObject(attributesString);
    }

    public String getAttributesAsString() {
        return attributes.toString();
    }
}
