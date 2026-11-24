package ro.casabuna.casabuna_backend.model.mapper.user;

import org.mapstruct.Mapper;
import ro.casabuna.casabuna_backend.model.dto.user.RoleDto;
import ro.casabuna.casabuna_backend.model.entity.RoleEntity;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(RoleEntity roleEntity);
}
