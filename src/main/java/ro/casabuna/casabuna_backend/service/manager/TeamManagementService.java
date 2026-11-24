package ro.casabuna.casabuna_backend.service.manager;

import org.springframework.data.web.PagedModel;
import ro.casabuna.casabuna_backend.model.dto.team.TeamResponseDto;
import ro.casabuna.casabuna_backend.model.dto.team.TeamStructureResponseDto;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamVolunteerDto;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.query.PageQuery;

import java.util.List;

public interface TeamManagementService {
    TeamStructureResponseDto getTeamStructure(Long teamId);

    PagedModel<TeamVolunteerDto> findUnassignedUsers(Long teamId, RoleName roleName, String search, PageQuery pageQuery);

    void assignMember(Long teamId, Long userId);

    /**
     * Removes a member from a team.
     * Also removes all volunteer-child assignments for this user in this team.
     *
     * @param teamId
     * @param userId
     */
    void removeMember(Long teamId, Long userId);

    void assignChildToVolunteer(Long teamId, Long volunteerId, Long childId);

    void removeChildAssignment(Long teamId, Long volunteerId, Long childId);

    List<TeamResponseDto> getManagerTeams(Long userId);
}
