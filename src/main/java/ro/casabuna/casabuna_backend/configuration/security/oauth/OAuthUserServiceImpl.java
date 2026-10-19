package ro.casabuna.casabuna_backend.configuration.security.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.dto.RoleDto;
import ro.casabuna.casabuna_backend.model.dto.UserDto;
import ro.casabuna.casabuna_backend.model.dto.RoleName;
import ro.casabuna.casabuna_backend.service.UserService;

import java.util.Set;

@Service
public class OAuthUserServiceImpl implements OAuthUserService {
    private final UserService userService;

    public OAuthUserServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    @Override
    public UserDto saveIfNotExists(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        return userService.findWithRolesByEmail(email)
                .orElseGet(() -> userService.save(buildUser(oAuth2User)));
    }

    private UserDto buildUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        RoleDto userRole = new RoleDto(RoleName.ROLE_USER);
        return new UserDto(null, email, firstName, lastName, Set.of(userRole));
    }
}
