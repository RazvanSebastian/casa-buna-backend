package ro.casabuna.casabuna_backend.model.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamChildDto;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamManagerDto;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamVolunteerDto;
import ro.casabuna.casabuna_backend.model.dto.user.UserDto;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.team.VolunteerChildAssignmentEntity;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    UserDto toUserDtoWithRoles(UserEntity userEntity);

    TeamManagerDto toTeamManagerDto(UserEntity userEntity);

    @Mapping(target = "assignedChildren", expression = "java(toTeamChildDtoSet(userEntity.getChildAssignments()))")
    TeamVolunteerDto toTeamVolunteerDto(UserEntity userEntity);

    TeamChildDto toTeamChildDto(UserEntity userEntity);

    default Set<TeamChildDto> toTeamChildDtoSet(Set<VolunteerChildAssignmentEntity> assignments) {
        return assignments.stream()
                .map(VolunteerChildAssignmentEntity::getChild)
                .map(this::toTeamChildDto)
                .collect(Collectors.toSet());
    }
}
