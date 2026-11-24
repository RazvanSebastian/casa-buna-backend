package ro.casabuna.casabuna_backend.model.dto.team.member;

public record TeamManagerDto(
        Long id,
        String email,
        String firstName,
        String lastName
) implements TeamMemberDto {
}
