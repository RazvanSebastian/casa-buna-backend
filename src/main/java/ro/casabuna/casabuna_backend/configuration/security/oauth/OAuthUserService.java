package ro.casabuna.casabuna_backend.configuration.security.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;
import ro.casabuna.casabuna_backend.model.dto.UserDto;

public interface OAuthUserService {

    UserDto saveIfNotExists(OAuth2User oAuth2User);
}
