package ro.casabuna.casabuna_backend.model.dto.team.member;

import java.util.Set;

public record TeamVolunteerDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<TeamChildDto> assignedChildren
) implements TeamMemberDto {
}
