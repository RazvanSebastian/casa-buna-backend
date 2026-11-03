package ro.casabuna.casabuna_backend.notification.service;
import org.springframework.data.web.PagedModel;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationDto;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationsPreviewDto;

public interface NotificationService {

    PagedModel<NotificationDto> findAll(Long userId, PageQuery pageQuery);

    NotificationsPreviewDto getNotificationsPreview(Long userId);

    boolean markToRead(Long userId, Long notificationId);
}
