package ro.casabuna.casabuna_backend.service.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.casabuna.casabuna_backend.configuration.security.jwt.UserPrincipal;
import ro.casabuna.casabuna_backend.model.dto.team.TeamStructureResponseDto;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.entity.RoleEntity;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.team.TeamEntity;
import ro.casabuna.casabuna_backend.model.entity.team.VolunteerChildAssignmentEntity;
import ro.casabuna.casabuna_backend.repository.RoleRepository;
import ro.casabuna.casabuna_backend.repository.TeamRepository;
import ro.casabuna.casabuna_backend.repository.UserRepository;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional(propagation = Propagation.REQUIRES_NEW)
@SpringBootTest
@Testcontainers
public class TeamManagementServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:18.6");

    @Autowired
    private TeamManagementService teamManagementService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private RoleRepository roleRepository;

    private TeamEntity testTeam;
    private RoleEntity managerRole;
    private RoleEntity volunteerRole;
    private RoleEntity childRole;

    @BeforeEach
    public void setUp() {
        managerRole = roleRepository.findByName(RoleName.ROLE_MANAGER)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(RoleName.ROLE_MANAGER).build()));
        volunteerRole = roleRepository.findByName(RoleName.ROLE_VOLUNTEER)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(RoleName.ROLE_VOLUNTEER).build()));
        childRole = roleRepository.findByName(RoleName.ROLE_CHILD)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(RoleName.ROLE_CHILD).build()));

        testTeam = teamRepository.save(TeamEntity.builder().name("Test Team").build());
    }

    @Test
    public void shouldGetTeamStructureWithManagersVolunteersAndAssignedChildren() {
        // given
        var team = teamRepository.save(TeamEntity.builder().name("Test Team 1").build());

        var manager1 = createUser("manager1@test.ro", "John", "Manager", Set.of(managerRole), testTeam);
        createUser("manager2@test.ro", "Jane", "Manager", Set.of(managerRole), testTeam);

        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                new UserPrincipal(manager1.getId(),manager1.getEmail()),
                null,
                Collections.emptyList()
        ));

        var child1 = createUser("child1@test.ro", "Child", "One", Set.of(childRole), testTeam);
        var child2 = createUser("child2@test.ro", "Child", "Two", Set.of(childRole), testTeam);
        var child3 = createUser("child3@test.ro", "Child", "Three", Set.of(childRole), testTeam);
        var child4 = createUser("child4@test.ro", "Child", "Four", Set.of(childRole), testTeam);

        createUser("child5@test.ro", "Child", "Five", Set.of(childRole), testTeam);

        var volunteer1 = createUser("volunteer1@test.ro", "Bob", "Volunteer", Set.of(volunteerRole), testTeam);
        volunteer1.getChildAssignments().add(new VolunteerChildAssignmentEntity(null, volunteer1, child1, team));
        volunteer1.getChildAssignments().add(new VolunteerChildAssignmentEntity(null, volunteer1, child2, team));
        userRepository.save(volunteer1);

        var volunteer2 = createUser("volunteer2@test.ro", "Alice", "Volunteer", Set.of(volunteerRole), testTeam);
        volunteer2.getChildAssignments().add(new VolunteerChildAssignmentEntity(null, volunteer2, child3, team));
        volunteer2.getChildAssignments().add(new VolunteerChildAssignmentEntity(null, volunteer2, child4, team));
        userRepository.save(volunteer2);

        // when
        TeamStructureResponseDto result = teamManagementService.getTeamStructure(testTeam.getId());

        // then
        assertNotNull(result);
        assertEquals(2, result.managers().size());
        assertEquals(2, result.volunteers().size());

        assertTrue(result.managers().stream().anyMatch(m -> m.email().equals("manager1@test.ro")));
        assertTrue(result.managers().stream().anyMatch(m -> m.email().equals("manager2@test.ro")));
        assertTrue(result.volunteers().stream().anyMatch(v -> v.email().equals("volunteer1@test.ro")));
        assertTrue(result.volunteers().stream().anyMatch(v -> v.email().equals("volunteer2@test.ro")));

        var assignedChildrenToVolunteer1 = result.volunteers().stream().filter(v -> v.email().equals("volunteer1@test.ro")).findFirst().get().assignedChildren();
        assertTrue(assignedChildrenToVolunteer1.stream().anyMatch(c -> c.email().equals("child1@test.ro")));
        assertTrue(assignedChildrenToVolunteer1.stream().anyMatch(c -> c.email().equals("child2@test.ro")));

        var assignedChildrenToVolunteer2 = result.volunteers().stream().filter(v -> v.email().equals("volunteer2@test.ro")).findFirst().get().assignedChildren();
        assertTrue(assignedChildrenToVolunteer2.stream().anyMatch(c -> c.email().equals("child3@test.ro")));
        assertTrue(assignedChildrenToVolunteer2.stream().anyMatch(c -> c.email().equals("child4@test.ro")));

        var unassignedChildren = result.unassignedChildren();
        assertTrue(unassignedChildren.stream().anyMatch(c -> c.email().equals("child5@test.ro")));
    }

    private UserEntity createUser(String email, String firstName, String lastName, Set<RoleEntity> roles, TeamEntity team) {
        var user = UserEntity.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .roles(roles)
                .teams(Set.of(team))
                .build();
        return userRepository.save(user);
    }
}
