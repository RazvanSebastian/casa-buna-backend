package ro.casabuna.casabuna_backend.model.dto.team;

import ro.casabuna.casabuna_backend.model.dto.team.member.TeamChildDto;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamManagerDto;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamVolunteerDto;

import java.util.Set;

public record TeamStructureResponseDto(
        Long id,
        Set<TeamManagerDto> managers,
        Set<TeamVolunteerDto> volunteers,
        Set<TeamChildDto> unassignedChildren
) {
}
