package ro.casabuna.casabuna_backend.service;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.volunteer.VolunteerRequestEntity;
import ro.casabuna.casabuna_backend.repository.UserRepository;
import ro.casabuna.casabuna_backend.repository.VolunteerRequestRepository;
import ro.casabuna.casabuna_backend.configuration.security.jwt.UserPrincipal;
import ro.casabuna.casabuna_backend.service.volunteer.VolunteerRequestService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@SpringBootTest
@Testcontainers
public class VolunteerRequestServiceIntegrationTest {

    private UserEntity requestorUserEntity;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:18.6");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerRequestRepository volunteerRequestRepository;

    @Autowired
    private VolunteerRequestService volunteerRequestService;

    @BeforeEach
    public void setUp() {
        requestorUserEntity = createRequestorUserEntity();
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        new UserPrincipal(requestorUserEntity.getId(), "test@test.ro"),
                        null,
                        Collections.emptyList()
                )
        );
    }

    @Test
    public void shouldApproveRequest() {
        // given
        var givenEntity = volunteerRequestRepository.save(
                VolunteerRequestEntity.builder()
                        .status(VolunteerRequestStatus.PENDING)
                        .build()
        );

        // when
        volunteerRequestService.approveRequest(requestorUserEntity.getId(), givenEntity.getId());

        // then
        var actualEntity = volunteerRequestRepository.findById(givenEntity.getId()).orElseThrow();
        assertEquals(VolunteerRequestStatus.APPROVED, actualEntity.getStatus());
    }

    @Test
    public void shouldCreateRequest() {
        // given
        var givenRequestModel = new NewVolunteerRequestDto(
                "selfDescription",
                "John",
                "Doe",
                Integer.valueOf(30),
                "Engineer",
                "1234567890",
                List.of("school"),
                List.of("univeristy"),
                List.of("health"),
                List.of("itSupport"),
                List.of("logistic"),
                "otherActivities"
        );

        // when
        var actualRequestModel = volunteerRequestService.createRequest(requestorUserEntity.getId(), givenRequestModel);

        // then
        assertNotNull(actualRequestModel.id());
        assertEquals(requestorUserEntity.getId(), actualRequestModel.requestorId());
        assertEquals(VolunteerRequestStatus.PENDING, actualRequestModel.status());
        assertEquals(givenRequestModel.selfDescription(), actualRequestModel.selfDescription());
        assertEquals(givenRequestModel.firstName(), actualRequestModel.firstName());
        assertEquals(givenRequestModel.lastName(), actualRequestModel.lastName());
        assertEquals(givenRequestModel.age(), actualRequestModel.age());
        assertEquals(givenRequestModel.occupation(), actualRequestModel.occupation());
        assertEquals(givenRequestModel.phone(), actualRequestModel.phone());
        assertEquals(givenRequestModel.educationActivities(), actualRequestModel.educationActivities());
        assertEquals(givenRequestModel.educationLevels(), actualRequestModel.educationLevels());
        assertEquals(givenRequestModel.healthActivities(), actualRequestModel.healthActivities());
        assertEquals(givenRequestModel.itSupportActivities(), actualRequestModel.itSupportActivities());
        assertEquals(givenRequestModel.logisticActivities(), actualRequestModel.logisticActivities());
        assertEquals(givenRequestModel.otherActivities(), actualRequestModel.otherActivities());
    }

    private @NonNull UserEntity createRequestorUserEntity() {
        return userRepository.save(
                UserEntity.builder()
                        .email("test@test.ro")
                        .build()
        );
    }

}
