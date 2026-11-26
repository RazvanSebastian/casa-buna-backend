package ro.casabuna.casabuna_backend.model.mapper.user;

import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamChildDto;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamManagerDto;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamMemberDto;
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

    @Mapping(target = "assignedChildren", source = "assignedChildren")
    TeamVolunteerDto toTeamVolunteerDto(UserEntity userEntity, Set<TeamChildDto> assignedChildren);

    TeamChildDto toTeamChildDto(UserEntity userEntity);

    @Name("toTeamMemberDto")
    default TeamMemberDto toTeamMemberDto(UserEntity userEntity) {
        return toTeamVolunteerDto(userEntity, Set.of());
    }

}
