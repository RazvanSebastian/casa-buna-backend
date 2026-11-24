package ro.casabuna.casabuna_backend.access.team;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.casabuna.casabuna_backend.repository.TeamRepository;

@Component
@AllArgsConstructor
public class TeamSecurity {
    private final TeamRepository teamRepository;

    public boolean validateManager(Long teamId, Long managerId) {
        return teamRepository.existsByIdAndUsersId(teamId, managerId);
    }
}
