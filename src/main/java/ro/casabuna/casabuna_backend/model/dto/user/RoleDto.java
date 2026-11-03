package ro.casabuna.casabuna_backend.model.dto.user;

import ro.casabuna.casabuna_backend.model.entity.RoleEntity;

public record RoleDto(RoleName name) {
    public RoleDto(RoleEntity roleEntity) {
        this(roleEntity.getName());
    }
}
