package ro.casabuna.casabuna_backend.configuration.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ro.casabuna.casabuna_backend.model.dto.UserDto;
import ro.casabuna.casabuna_backend.configuration.security.jwt.JwtProperties;
import ro.casabuna.casabuna_backend.configuration.security.jwt.JwtTokenProvider;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final OAuthUserService oAuthUserService;

    public OAuthSuccessHandler(JwtTokenProvider jwtTokenProvider,
                               JwtProperties jwtProperties,
                               OAuthUserService oAuthUserService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.oAuthUserService = oAuthUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDto userDto = oAuthUserService.saveIfNotExists((OAuth2User) authentication.getPrincipal());
        String jwtToken = getJwtToken(userDto);
        appendJwtCookie(response, jwtToken);
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:4200");
    }

    private String getJwtToken(UserDto userDto) {
        Set<String> roles = userDto.roles().stream()
                .map(roleModel -> roleModel.name().name())
                .collect(Collectors.toSet());
        Long id = userDto.id();
        String email = userDto.email();
        return jwtTokenProvider.generateToken(id, email, roles);
    }

    private void appendJwtCookie(HttpServletResponse response, String jwtToken) {
        Cookie cookie = new Cookie(jwtProperties.getJwtCookieName(), jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.isSecured());
        cookie.setPath("/");
        cookie.setMaxAge(jwtProperties.expiration());
        response.addCookie(cookie);
    }
}
