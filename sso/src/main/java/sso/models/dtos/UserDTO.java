package sso.models.dtos;

import java.sql.Timestamp;

public class UserDTO {

    private String UUID;
    private String username;
    private String email;
    private Timestamp created;
    private Boolean blocked;
    private String attributes;

    public UserDTO(String UUID, String username, String email, Timestamp created, Boolean blocked, String attributes) {
        this.UUID = UUID;
        this.username = username;
        this.email = email;
        this.created = created;
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

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
}
