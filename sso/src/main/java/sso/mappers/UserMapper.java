package sso.mappers;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import sso.models.dtos.NewUserDTO;
import sso.models.dtos.UpdateUserDTO;
import sso.models.dtos.UserDTO;
import sso.models.entities.User;
import sso.utils.exceptions.ValidationException;
import sso.utils.security.PasswordEncoderFactory;
import sso.utils.security.Validator;

import java.util.ArrayList;
import java.util.List;



@Component
public class UserMapper {

    private final BCryptPasswordEncoder passwordEncoder = PasswordEncoderFactory.getPasswordEncoder();

    public User newUserDTOToUser(NewUserDTO newUserDTO) throws ValidationException {
        newUserDTO.validate();
        return new User(
            null,
            newUserDTO.getUsername(),
            passwordEncoder.encode(newUserDTO.getPassword()),
            newUserDTO.getEmail(),
            null,
            newUserDTO.isBlocked() != null ? newUserDTO.isBlocked() : false,
            null,
            newUserDTO.getAttributes() != null ? newUserDTO.getAttributes() : "{}"
        );
    }

    public User updateUserDTOToUser(UpdateUserDTO updateUserDTO) throws ValidationException {
        updateUserDTO.validate();
        return new User(
            updateUserDTO.getUUID(),
            updateUserDTO.getUsername(),
            updateUserDTO.getPassword() != null ? passwordEncoder.encode(updateUserDTO.getPassword()) : null,
            null,
            null,
            updateUserDTO.getBlocked(),
            null,
            updateUserDTO.getAttributes() != null ? updateUserDTO.getAttributes() : "{}"
        );
    }

    public UserDTO userToUserDTO(User user) {
        return new UserDTO(
            user.getUUID(),
            user.getUsername(),
            user.getEmail(),
            user.getCreated(),
            user.isBlocked(),
            user.getAttributesAsString()
        );
    }

    public List<UserDTO> usersToUserDTOs(List<User> users) {
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            userDTOs.add(this.userToUserDTO(user));
        }

        return userDTOs;
    }
}
