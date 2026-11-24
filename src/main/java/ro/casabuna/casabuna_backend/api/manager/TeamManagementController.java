package ro.casabuna.casabuna_backend.api.manager;

import lombok.AllArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ro.casabuna.casabuna_backend.configuration.security.jwt.UserPrincipal;
import ro.casabuna.casabuna_backend.exception.InvalidRequestException;
import ro.casabuna.casabuna_backend.model.dto.team.TeamResponseDto;
import ro.casabuna.casabuna_backend.model.dto.team.TeamStructureResponseDto;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamVolunteerDto;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.service.manager.TeamManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/manager/team")
@AllArgsConstructor
public class TeamManagementController {
    private final TeamManagementService teamManagementService;

    @GetMapping("/assigned")
    public List<TeamResponseDto> getManagerTeams(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return teamManagementService.getManagerTeams(userPrincipal.id());
    }

    @GetMapping("/{teamId}/structure")
    public TeamStructureResponseDto getTeamStructure(@PathVariable Long teamId) {
        return teamManagementService.getTeamStructure(teamId);
    }

    @GetMapping("/{teamId}/users/unassigned")
    public PagedModel<TeamVolunteerDto> findUnassignedUsers(
            @PathVariable Long teamId,
            @RequestParam(name = "role") RoleName roleName,
            @RequestParam(name="search") String search,
            @ModelAttribute PageQuery pageQuery) {
        if (roleName != RoleName.ROLE_CHILD && roleName != RoleName.ROLE_VOLUNTEER) {
            throw new InvalidRequestException("Only ROLE_CHILD and ROLE_VOLUNTEER are allowed");
        }
        return teamManagementService.findUnassignedUsers(teamId, roleName, search, pageQuery);
    }

    @PutMapping("/{teamId}/member/{userId}")
    public void assignMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamManagementService.assignMember(teamId, userId);
    }

    @DeleteMapping("/{teamId}/member/{userId}")
    public void removeMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamManagementService.removeMember(teamId, userId);
    }

    @PutMapping("/{teamId}/volunteer/{volunteerId}/child/{childId}")
    public void assignChildToVolunteer(@PathVariable Long teamId, @PathVariable Long volunteerId, @PathVariable Long childId) {
        teamManagementService.assignChildToVolunteer(teamId, volunteerId, childId);
    }

    @DeleteMapping("/{teamId}/volunteer/{volunteerId}/child/{childId}")
    public void removeChildFromVolunteerAssignment(@PathVariable Long teamId, @PathVariable Long volunteerId, @PathVariable Long childId) {
        teamManagementService.removeChildAssignment(teamId, volunteerId, childId);
    }

}
