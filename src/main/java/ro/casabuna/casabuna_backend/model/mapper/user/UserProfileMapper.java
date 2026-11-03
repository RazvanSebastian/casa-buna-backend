package ro.casabuna.casabuna_backend.model.mapper.user;

import org.mapstruct.Mapper;
import ro.casabuna.casabuna_backend.model.dto.user.UserProfileDto;
import ro.casabuna.casabuna_backend.model.entity.UserProfileEntity;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileDto toDto(UserProfileEntity userProfileEntity);

    UserProfileEntity toEntity(UserProfileDto userProfileDto);
}
