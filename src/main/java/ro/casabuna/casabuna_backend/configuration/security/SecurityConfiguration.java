package ro.casabuna.casabuna_backend.configuration.security;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.util.WebUtils;
import ro.casabuna.casabuna_backend.configuration.security.jwt.JwtProperties;
import ro.casabuna.casabuna_backend.configuration.security.oauth.OAuthSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   BearerTokenResolver bearerTokenResolver,
                                                   Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter,
                                                   JwtDecoder jwtDecoder,
                                                   JwtProperties jwtProperties) {
        http
                .securityMatcher("/api/**") // Only runs for routes starting with /api/
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/api/volunteer/request/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(resourceServer -> {
                    resourceServer.jwt(jwt -> {
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                        jwt.decoder(jwtDecoder);
                    });
                    resourceServer.bearerTokenResolver(bearerTokenResolver);
                })
                .logout(logout -> logout
                        .logoutUrl("/api/user/logout")
                        .deleteCookies(jwtProperties.getJwtCookieName())
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        return http.build();
    }

    /**
     * Chain 2: Handles Google/Microsoft login and callback flows.
     * Completely isolated from the resource server filters!
     */
    @Bean
    @Order(2)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http, OAuthSuccessHandler oAuthSuccessHandler, OAuth2AuthorizationRequestResolver authorizationRequestResolver) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/login/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oAuth2LoginConfigurer -> oAuth2LoginConfigurer
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(authorizationRequestResolver)
                        )
                        .successHandler(oAuthSuccessHandler)
                );

        return http.build();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver(JwtProperties jwtProperties) {
        return request -> {
            Cookie cookie = WebUtils.getCookie(request, jwtProperties.getJwtCookieName());
            return cookie != null ? cookie.getValue() : null;
        };
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");

        resolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params -> params.put("prompt", "login"))
        );

        return resolver;
    }
}
