package ro.casabuna.casabuna_backend.notification.service;

import org.springframework.data.web.PagedModel;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationDto;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationsPreviewDto;

import java.util.List;
import java.util.Set;

public interface NotificationService {

    PagedModel<NotificationDto> findAll(Long userId, PageQuery pageQuery);

    NotificationsPreviewDto getNotificationsPreview(Long userId);

    boolean markToRead(Long userId, Long notificationId);

    NotificationDto saveUserNotification(Long userId, String title, String message);

    List<NotificationDto> saveUserGroupNotification(Set<Long> subscribedUserIds, Set<RoleName> roles, String title, String message);

    List<NotificationDto> saveGlobalNotification(Set<Long> subscribedUserIds, String title, String message);
}
