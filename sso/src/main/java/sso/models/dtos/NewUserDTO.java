package sso.models.dtos;

import sso.models.entities.User;
import sso.utils.exceptions.ValidationException;
import sso.utils.security.Validator;

public class NewUserDTO {

    private String username;
    private String password;
    private String email;
    private Boolean blocked;
    private String attributes;

    public NewUserDTO(String username, String password, String email, Boolean blocked, String attributes) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.blocked = blocked;
        this.attributes = attributes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public void validate() throws ValidationException {
        Validator.validatePattern(User.PATTERN_USERNAME, this.username);
        Validator.validatePattern(User.PATTERN_PASSWORD, this.password);
        Validator.validatePattern(User.PATTERN_EMAIL, this.email);

        if (this.getAttributes() != null) {
            Validator.validateAttributesString(this.attributes);
        }
    }

    @Override
    public String toString() {
        return  "{ " +
                "'username': '" + username + "', " +
                "'password': '" + password + "', " +
                "'email': '" + email + "', " +
                "'blocked': " + blocked + ", " +
                "'attributes': '" + attributes + "' " +
                "}";
    }
}
