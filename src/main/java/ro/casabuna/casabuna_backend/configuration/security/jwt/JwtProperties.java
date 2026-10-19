package ro.casabuna.casabuna_backend.configuration.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        int expiration,
        boolean isSecured) {

    public MacAlgorithm getAlgorithm() {
        return MacAlgorithm.HS256;
    }

    public String getJwtCookieName() {
        return "jwt_token";
    }
}
