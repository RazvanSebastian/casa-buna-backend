package ro.casabuna.casabuna_backend.model.dto;

import ro.casabuna.casabuna_backend.model.entity.UserEntity;

import java.util.Set;
import java.util.stream.Collectors;

public record UserDto(Long id, String email, String firstName, String lastName, Set<RoleDto> roles) {
    public UserDto(UserEntity userEntity) {
        this(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getRoles().stream()
                        .map(RoleDto::new)
                        .collect(Collectors.toSet()));
    }

    public UserDto(Long id) {
        this(id, null, null, null, null);
    }
}
