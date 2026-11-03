package ro.casabuna.casabuna_backend.notification.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationDto;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationsPreviewDto;
import ro.casabuna.casabuna_backend.notification.model.entity.NotificationEntity;
import ro.casabuna.casabuna_backend.notification.model.mapper.NotificationMapper;
import ro.casabuna.casabuna_backend.notification.repository.NotificationRepository;
import ro.casabuna.casabuna_backend.notification.repository.specification.NotificationSpecifications;
import ro.casabuna.casabuna_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public PagedModel<NotificationDto> findAll(Long userId, PageQuery pageQuery) {
        var spec = NotificationSpecifications.latestByUser(userId);
        var notifications = notificationRepository.findAll(spec, pageQuery.toPageRequest())
                .map(notificationMapper::toDto);
        return new PagedModel<>(notifications);
    }

    @Transactional(readOnly = true)
    @Override
    public NotificationsPreviewDto getNotificationsPreview(Long userId) {
        int counter = notificationRepository.countUnreadByUserId(userId);
        var spec = NotificationSpecifications.latestByUser(userId);
        List<NotificationDto> latest = notificationRepository.findAll(spec, PageRequest.of(0, 5))
                .stream()
                .map(notificationMapper::toDto)
                .toList();
        return new NotificationsPreviewDto(counter, latest);
    }


    @Override
    public boolean markToRead(Long userId, Long notificationId) {
        return notificationRepository.markAsRead(notificationId, userId) > 0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public NotificationDto saveUserNotification(Long userId, String title, String message) {
        UserEntity userEntity = userRepository.getReferenceById(userId);
        NotificationEntity notification = NotificationEntity.builder()
                .user(userEntity)
                .title(title)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationMapper.toDto(notificationRepository.save(notification));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<NotificationDto> saveUserGroupNotification(Set<Long> subscribedUserIds, Set<RoleName> roles, String title, String message) {
        Set<Long> userIds = userRepository.findIdsByRoles(roles);
        return saveForUsers(userIds, title, message).stream()
                .filter(entity -> subscribedUserIds.contains(entity.getUser().getId()))
                .map(notificationMapper::toDto)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<NotificationDto> saveGlobalNotification(Set<Long> subscribedUserIds, String title, String message) {
        Set<Long> userIds = userRepository.findAllIds();
        return saveForUsers(userIds, title, message)
                .stream()
                .filter(entity -> subscribedUserIds.contains(entity.getUser().getId()))
                .map(notificationMapper::toDto)
                .toList();
    }

    private List<NotificationEntity> saveForUsers(Set<Long> userIds, String title, String message) {
        List<NotificationEntity> notificationEntities = userIds.stream()
                .map(id -> NotificationEntity.builder()
                        .user(userRepository.getReferenceById(id))
                        .title(title)
                        .message(message)
                        .read(false)
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        return notificationRepository.saveAll(notificationEntities);
    }
}
