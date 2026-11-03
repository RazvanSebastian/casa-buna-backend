package ro.casabuna.casabuna_backend.model.dto.user;

public record UserDetailsDto(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}
