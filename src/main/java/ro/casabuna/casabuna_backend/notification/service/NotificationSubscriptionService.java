package ro.casabuna.casabuna_backend.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationSubscriptionService {

    SseEmitter subscribe(Long userId);

    void sendNotification(Long userId, String title, String message);

    void sendGlobalNotification(String title, String message);
}
