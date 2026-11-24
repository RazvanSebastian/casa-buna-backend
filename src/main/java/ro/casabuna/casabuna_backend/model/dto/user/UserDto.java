package ro.casabuna.casabuna_backend.model.dto.user;

import java.util.Set;

public record UserDto(Long id, String email, String firstName, String lastName, Set<RoleDto> roles) {
}
