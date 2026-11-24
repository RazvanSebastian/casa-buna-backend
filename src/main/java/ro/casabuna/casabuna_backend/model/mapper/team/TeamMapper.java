package ro.casabuna.casabuna_backend.model.mapper.team;

import org.mapstruct.Mapper;
import ro.casabuna.casabuna_backend.model.dto.team.NewTeamRequestDto;
import ro.casabuna.casabuna_backend.model.dto.team.TeamResponseDto;
import ro.casabuna.casabuna_backend.model.entity.team.TeamEntity;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    TeamResponseDto toDto(TeamEntity teamEntity);

    TeamEntity toEntity(TeamResponseDto teamResponseDto);

    TeamEntity toEntity(NewTeamRequestDto newTeamRequestDto);
}
