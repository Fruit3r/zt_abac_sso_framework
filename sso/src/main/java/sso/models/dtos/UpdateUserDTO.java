package sso.models.dtos;

import sso.models.entities.User;
import sso.utils.exceptions.ValidationException;
import sso.utils.security.Validator;

public class UpdateUserDTO {

    private String UUID;
    private String username;
    private String password;
    private Boolean blocked;
    private String attributes;

    public UpdateUserDTO(String UUID, String username, String password, Boolean blocked, String attributes) {
        this.UUID = UUID;
        this.username = username;
        this.password = password;
        this.blocked = blocked;
        this.attributes = attributes;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getBlocked() {
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
        Validator.validatePattern(User.PATTERN_UUID, this.UUID);

        if (this.getUsername() != null) {
            Validator.validatePattern(User.PATTERN_USERNAME, this.username);
        }
        if (this.getAttributes() != null) {
            Validator.validateAttributesString(this.attributes);
        }
    }

    @Override
    public String toString() {
        return  "{ " +
                "'UUID': '" + UUID + "', " +
                "'username': '" + username + "', " +
                "'password': '" + password + "', " +
                "'blocked': " + blocked + ", " +
                "'attributes': '" + attributes + "' " +
                "}";
    }
}
