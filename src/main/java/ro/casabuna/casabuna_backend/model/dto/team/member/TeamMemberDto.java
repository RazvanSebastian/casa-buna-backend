package ro.casabuna.casabuna_backend.model.dto.team.member;

public sealed interface TeamMemberDto permits TeamManagerDto, TeamVolunteerDto, TeamChildDto {
    Long id();

    String email();

    String firstName();

    String lastName();
}
