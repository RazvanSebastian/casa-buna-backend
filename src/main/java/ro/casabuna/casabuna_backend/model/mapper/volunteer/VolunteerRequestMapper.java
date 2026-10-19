package ro.casabuna.casabuna_backend.model.mapper.volunteer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestResponseDto;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.volunteer.VolunteerRequestEntity;

@Mapper(componentModel = "spring")
public interface VolunteerRequestMapper {

    @Mapping(source = "requestor", target = "requestorId", qualifiedByName = "userEntityToId")
    VolunteerRequestResponseDto toDto(VolunteerRequestEntity requestEntity);

    VolunteerRequestEntity toEntity(NewVolunteerRequestDto requestDto);

    @Named("userEntityToId")
    default Long userReferenceToModel(UserEntity userEntity) {
        return userEntity == null ? null : userEntity.getId();
    }
}
