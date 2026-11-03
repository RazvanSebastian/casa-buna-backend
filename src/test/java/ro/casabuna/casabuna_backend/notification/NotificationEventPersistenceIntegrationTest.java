package ro.casabuna.casabuna_backend.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.notification.model.event.GlobalEvent;
import ro.casabuna.casabuna_backend.notification.model.event.UserEvent;
import ro.casabuna.casabuna_backend.notification.model.event.UserGroupEvent;
import ro.casabuna.casabuna_backend.notification.repository.NotificationRepository;
import ro.casabuna.casabuna_backend.repository.RoleRepository;
import ro.casabuna.casabuna_backend.repository.UserRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
class NotificationEventPersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:18.6");

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldPersistUserNotificationAfterPublishingTransactionCommits() {
        UserEntity user = createUser("committed@test.ro");

        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                eventPublisher.publishEvent(new UserEvent(user.getId(), "Title", "Message")));

        assertEquals(1, notificationRepository.count());
    }

    @Test
    void shouldNotPersistUserNotificationWhenPublishingTransactionRollsBack() {
        UserEntity user = createUser("rolled-back@test.ro");

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            eventPublisher.publishEvent(new UserEvent(user.getId(), "Title", "Message"));
            status.setRollbackOnly();
        });

        assertEquals(0, notificationRepository.count());
    }

    @Test
    void shouldPersistUserGroupNotificationAfterPublishingTransactionCommits() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                userRepository.save(UserEntity.builder()
                        .email("admin@test.ro")
                        .roles(roleRepository.findByNameIn(Set.of(RoleName.ROLE_ADMIN)))
                        .build()));

        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                eventPublisher.publishEvent(new UserGroupEvent(
                        Set.of(RoleName.ROLE_ADMIN), "Title", "Message")));

        assertEquals(1, notificationRepository.count());
    }

    @Test
    void shouldPersistGlobalNotificationWhenPublishedWithoutTransaction() {
        createUser("global@test.ro");

        eventPublisher.publishEvent(new GlobalEvent("Title", "Message"));

        assertEquals(1, notificationRepository.count());
    }

    private UserEntity createUser(String email) {
        return userRepository.save(UserEntity.builder().email(email).build());
    }
}
