package ro.casabuna.casabuna_backend.model.mapper.volunteer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ro.casabuna.casabuna_backend.model.dto.UserDetailsDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestResponseDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestWithUserResponseDto;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.volunteer.VolunteerRequestEntity;

@Mapper(componentModel = "spring")
public interface VolunteerRequestMapper {

    @Mapping(source = "requestor", target = "requestorId", qualifiedByName = "userEntityToId")
    VolunteerRequestResponseDto toDto(VolunteerRequestEntity requestEntity);

    VolunteerRequestEntity toEntity(NewVolunteerRequestDto requestDto);

    @Mapping(source = "requestor", target = "requestor", qualifiedByName = "userEntityToDto")
    @Mapping(source = "approver", target = "approver", qualifiedByName = "userEntityToDto")
    VolunteerRequestWithUserResponseDto toDtoWithUser(VolunteerRequestEntity requestEntity);

    @Named("userEntityToId")
    default Long userReferenceToModel(UserEntity userEntity) {
        return userEntity == null ? null : userEntity.getId();
    }

    @Named("userEntityToDto")
    default UserDetailsDto userEntityToDto(UserEntity userEntity) {
        return userEntity == null ? null : new UserDetailsDto(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName()
        );
    }
}
