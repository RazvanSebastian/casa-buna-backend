package ro.casabuna.casabuna_backend.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ro.casabuna.casabuna_backend.notification.model.event.GlobalEvent;
import ro.casabuna.casabuna_backend.notification.model.event.UserEvent;
import ro.casabuna.casabuna_backend.notification.model.event.UserGroupEvent;

public interface NotificationSubscriptionService {

    SseEmitter subscribe(Long userId);

    void sendUserNotification(UserEvent event);

    void sendUserGroupNotification(UserGroupEvent event);

    void sendGlobalNotification(GlobalEvent event);
}
