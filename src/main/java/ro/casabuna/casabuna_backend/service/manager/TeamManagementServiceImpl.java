package ro.casabuna.casabuna_backend.service.manager;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
import ro.casabuna.casabuna_backend.notification.model.event.UserEvent;
import ro.casabuna.casabuna_backend.notification.service.NotificationService;
import ro.casabuna.casabuna_backend.notification.service.NotificationSubscriptionService;
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

    private final ApplicationEventPublisher eventPublisher;

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
        var userEntity = userRepository.findWithTeamsByIdAndRoles(userId, allowedRoles)
                .orElseThrow(() -> new ResourceConditionUnsatisfiedException("User must have ROLE_VOLUNTEER or ROLE_CHILD to be assigned to a team"));
        var teamEntity = teamRepository.findById(teamId).orElseThrow(() -> new ResourceNotFound("Team not found"));
        userEntity.getTeams().add(teamEntity);
        eventPublisher.publishEvent(
                new UserEvent(
                        userId,
                        "Nou in echipa",
                        "Ai fost adaugat in echipa " + teamEntity.getName()
                )
        );
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

        var teamEntity = teamRepository.findById(teamId).orElseThrow(() -> new ResourceNotFound("Team not found"));
        var userEntity = userRepository.getReferenceById(userId);

        // Remove all volunteer-child assignments for this user in this team
        volunteerChildAssignmentRepository.deleteByTeamAndUser(teamEntity, userEntity);

        // Remove user from team
        userRepository.deleteTeamMember(userId, teamId);

        eventPublisher.publishEvent(
                new UserEvent(
                        userId,
                        "Scos din echipa",
                        "Ai fost scos din echipa " + teamEntity.getName()
                )
        );
    }

    @Override
    @Transactional
    public void assignChildToVolunteer(Long teamId, Long volunteerId, Long childId) {
        var respectTeamAndRoles = userRepository.existsByIdAndTeamIdAndRoleNames(volunteerId, teamId, Set.of(RoleName.ROLE_VOLUNTEER))
                && userRepository.existsByIdAndTeamIdAndRoleNames(childId, teamId, Set.of(RoleName.ROLE_CHILD));
        if (respectTeamAndRoles) {
            var childEntity = userRepository.findById(childId).orElseThrow(() -> new ResourceNotFound("Child not found"));
            var volunteerEntity = userRepository.findById(volunteerId).orElseThrow(() -> new ResourceNotFound("Volunteer not found"));
            var teamEntity = teamRepository.findById(teamId).orElseThrow(() -> new ResourceNotFound("Team not found"));

            VolunteerChildAssignmentEntity newAssignment = VolunteerChildAssignmentEntity.builder()
                    .team(teamEntity)
                    .volunteer(volunteerEntity)
                    .child(childEntity)
                    .build();
            volunteerChildAssignmentRepository.save(newAssignment);

            eventPublisher.publishEvent(
                    new UserEvent(
                            volunteerId,
                            "Copil asignat",
                            childEntity.getFirstName() + " " + childEntity.getLastName() + " ti-a fost asignat in echipa " + teamEntity.getName() + " pentru voluntariat!"
                    )
            );

            eventPublisher.publishEvent(
                    new UserEvent(
                            childId,
                            "Voluntar asignat",
                            volunteerEntity.getFirstName() + " " + volunteerEntity.getLastName() + " ti-a fost asignat in echipa " + teamEntity.getName()
                    )
            );
        } else {
            throw new ResourceConditionUnsatisfiedException("Volunteer or child should be assigned to the team " + teamId);
        }
    }

    @Override
    @Transactional
    public void removeChildAssignment(Long teamId, Long volunteerId, Long childId) {
        var volunteerEntity = userRepository.findById(volunteerId).orElseThrow(() -> new ResourceNotFound("Volunteer not found"));
        var childEntity = userRepository.findById(childId).orElseThrow(() -> new ResourceNotFound("Child not found"));
        var teamEntity = teamRepository.findById(teamId).orElseThrow(() -> new ResourceNotFound("Team not found"));

        VolunteerChildAssignmentEntity assignmentEntity = volunteerChildAssignmentRepository.findByVolunteerAndChildAndTeam(
                volunteerEntity,
                childEntity,
                teamEntity
        ).orElseThrow(() -> new ResourceNotFound("Assignment not found"));
        volunteerChildAssignmentRepository.delete(assignmentEntity);

        eventPublisher.publishEvent(
                new UserEvent(
                        volunteerId,
                        "Revocare asignare copil",
                        childEntity.getFirstName() + " " + childEntity.getLastName() + " nu iti mai este asignat in echipa " + teamEntity.getName() + " pentru voluntariat!"
                )
        );

        eventPublisher.publishEvent(
                new UserEvent(
                        volunteerId,
                        "Revocare asignare voluntar",
                        volunteerEntity.getFirstName() + " " + volunteerEntity.getLastName() + " nu iti mai este asignat in echipa " + teamEntity.getName() + " pentru voluntariat!"
                )
        );
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
