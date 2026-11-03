package ro.casabuna.casabuna_backend.notification.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationDto;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationsPreviewDto;
import ro.casabuna.casabuna_backend.notification.model.mapper.NotificationMapper;
import ro.casabuna.casabuna_backend.notification.repository.NotificationRepository;
import ro.casabuna.casabuna_backend.notification.repository.specification.NotificationSpecifications;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public PagedModel<NotificationDto> findAll(Long userId, PageQuery pageQuery) {
        var spec = NotificationSpecifications.latestByUser(userId);
        var notifications = notificationRepository.findAll(spec, pageQuery.toPageRequest())
                .map(notificationMapper::toDto);
        return new PagedModel<>(notifications);
    }

    @Transactional
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
    @Transactional
    public boolean markToRead(Long userId, Long notificationId) {
        return notificationRepository.markAsRead(notificationId, userId) > 0;
    }
}
