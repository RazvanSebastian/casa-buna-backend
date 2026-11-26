package ro.casabuna.casabuna_backend.service.manager;

import lombok.AllArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.access.team.IsAssociatedManager;
import ro.casabuna.casabuna_backend.exception.ResourceConditionUnsatisfiedException;
import ro.casabuna.casabuna_backend.exception.ResourceNotFound;
import ro.casabuna.casabuna_backend.model.dto.team.TeamResponseDto;
import ro.casabuna.casabuna_backend.model.dto.team.TeamStructureResponseDto;
import ro.casabuna.casabuna_backend.model.dto.team.member.TeamMemberDto;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.team.VolunteerChildAssignmentEntity;
import ro.casabuna.casabuna_backend.model.mapper.team.TeamMapper;
import ro.casabuna.casabuna_backend.model.mapper.user.UserMapper;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.repository.TeamRepository;
import ro.casabuna.casabuna_backend.repository.UserRepository;
import ro.casabuna.casabuna_backend.repository.VolunteerChildAssignmentRepository;
import ro.casabuna.casabuna_backend.repository.specification.TeamSpecifications;
import ro.casabuna.casabuna_backend.repository.specification.UserSpecifications;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@IsAssociatedManager
public class TeamManagementServiceImpl implements TeamManagementService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final VolunteerChildAssignmentRepository volunteerChildAssignmentRepository;

    private final UserMapper userMapper;
    private final TeamMapper teamMapper;

    @Override
    @Transactional(readOnly = true)
    public TeamStructureResponseDto getTeamStructure(Long teamId) {
        var managerSpec = UserSpecifications.teamManagers(teamId);
        var managers = userRepository.findAll(managerSpec)
                .stream()
                .map(userMapper::toTeamManagerDto)
                .collect(Collectors.toSet());

        var childrenByVolunteersId = volunteerChildAssignmentRepository
                .findAllWithChildByTeamId(teamId)
                .stream()
                .collect(Collectors.groupingBy(
                        assignment -> assignment.getVolunteer().getId(),
                        Collectors.mapping(
                                assignment -> userMapper.toTeamChildDto(assignment.getChild()),
                                Collectors.toSet()
                        )
                ));

        var teamVolunteersSpec = UserSpecifications.teamVolunteers(teamId);
        var volunteersWithChildren = userRepository.findAll(teamVolunteersSpec)
                .stream()
                .map(volunteer -> userMapper.toTeamVolunteerDto(
                        volunteer, childrenByVolunteersId.getOrDefault(volunteer.getId(), Set.of())
                ))
                .collect(Collectors.toSet());

        var unassignedChildrenSpec = UserSpecifications.unassignedChildren(teamId);
        var unassignedChildren = userRepository.findAll(unassignedChildrenSpec)
                .stream()
                .map(userMapper::toTeamChildDto)
                .collect(Collectors.toSet());

        return new TeamStructureResponseDto(teamId, managers, volunteersWithChildren, unassignedChildren);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<TeamMemberDto> findUnassignedUsers(Long teamId, RoleName roleName, String search, PageQuery pageQuery) {
        var spec = UserSpecifications.usersNotInTeam(teamId, roleName, search);
        var usersPage = userRepository.findAll(spec, pageQuery.toPageRequest())
                .map(userMapper::toTeamMemberDto);
        return new PagedModel<>(usersPage);
    }

    @Override
    @Transactional
    public void assignMember(Long teamId, Long userId) {
        Set<RoleName> allowedRoles = Set.of(RoleName.ROLE_VOLUNTEER, RoleName.ROLE_CHILD);
        UserEntity userEntity = userRepository.findWithTeamsByIdAndRoles(userId, allowedRoles)
                .orElseThrow(() -> new ResourceConditionUnsatisfiedException("User must have ROLE_VOLUNTEER or ROLE_CHILD to be assigned to a team"));
        userEntity.getTeams().add(teamRepository.getReferenceById(teamId));
    }

    @Override
    @Transactional
    public void removeMember(Long teamId, Long userId) {
        // Verify is user exists, is part of the team and has a role that allows removal
        Set<RoleName> allowedRoles = Set.of(RoleName.ROLE_VOLUNTEER, RoleName.ROLE_CHILD);
        boolean hasAllowedRole = userRepository.existsByIdAndTeamIdAndRoleNames(userId, teamId, allowedRoles);

        if (!hasAllowedRole) {
            throw new ResourceConditionUnsatisfiedException("User must be a volunteer or child to be removed from team");
        }

        var teamEntity = teamRepository.getReferenceById(teamId);
        var userEntity = userRepository.getReferenceById(userId);

        // Remove all volunteer-child assignments for this user in this team
        volunteerChildAssignmentRepository.deleteByTeamAndUser(teamEntity, userEntity);

        // Remove user from team
        userRepository.deleteTeamMember(userId, teamId);
    }

    @Override
    @Transactional
    public void assignChildToVolunteer(Long teamId, Long volunteerId, Long childId) {
        var respectTeamAndRoles = userRepository.existsByIdAndTeamIdAndRoleNames(volunteerId, teamId, Set.of(RoleName.ROLE_VOLUNTEER))
                && userRepository.existsByIdAndTeamIdAndRoleNames(childId, teamId, Set.of(RoleName.ROLE_CHILD));
        if (respectTeamAndRoles) {
            VolunteerChildAssignmentEntity newAssignment = VolunteerChildAssignmentEntity.builder()
                    .team(teamRepository.getReferenceById(teamId))
                    .volunteer(userRepository.getReferenceById(volunteerId))
                    .child(userRepository.getReferenceById(childId))
                    .build();
            volunteerChildAssignmentRepository.save(newAssignment);
        } else {
            throw new ResourceConditionUnsatisfiedException("Volunteer or child should be assigned to the team " + teamId);
        }
    }

    @Override
    @Transactional
    public void removeChildAssignment(Long teamId, Long volunteerId, Long childId) {
        VolunteerChildAssignmentEntity assignmentEntity = volunteerChildAssignmentRepository.findByVolunteerAndChildAndTeam(
                userRepository.getReferenceById(volunteerId),
                userRepository.getReferenceById(childId),
                teamRepository.getReferenceById(teamId)
        ).orElseThrow(() -> new ResourceNotFound("Assignment not found"));
        volunteerChildAssignmentRepository.delete(assignmentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    @IsAssociatedManager(required = false)
    public List<TeamResponseDto> getManagedTeams(Long userId) {
        var spec = TeamSpecifications.byAssignedManager(userId);
        return teamRepository.findAll(spec).stream()
                .map(teamMapper::toDto)
                .toList();
    }
}
