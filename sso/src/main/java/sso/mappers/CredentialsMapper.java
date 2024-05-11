package sso.mappers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import sso.models.dtos.CredentialsDTO;
import sso.models.entities.Credentials;
import sso.utils.security.PasswordEncoderFactory;

@Component
public class CredentialsMapper {

    public Credentials credentialsDTOToCredentials(CredentialsDTO credentialsDTO) {
        return new Credentials(
                credentialsDTO.getUsername(),
                credentialsDTO.getPassword()
        );
    }
}
