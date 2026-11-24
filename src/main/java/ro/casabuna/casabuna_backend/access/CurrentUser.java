package ro.casabuna.casabuna_backend.access;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ro.casabuna.casabuna_backend.configuration.security.jwt.UserPrincipal;

@Component
public class CurrentUser {

    public UserPrincipal get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AuthenticationCredentialsNotFoundException("No authenticated user");
        }
        return principal;
    }
}
