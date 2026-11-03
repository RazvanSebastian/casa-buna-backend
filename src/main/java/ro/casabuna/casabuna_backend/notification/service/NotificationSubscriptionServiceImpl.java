package ro.casabuna.casabuna_backend.notification.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationDto;
import ro.casabuna.casabuna_backend.notification.model.event.GlobalEvent;
import ro.casabuna.casabuna_backend.notification.model.event.UserEvent;
import ro.casabuna.casabuna_backend.notification.model.event.UserGroupEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationSubscriptionServiceImpl implements NotificationSubscriptionService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    private final NotificationService notificationService;

    @Override
    public SseEmitter subscribe(Long userId) {
        return emitters.compute(userId, (id, existingEmitter) -> {
            if (existingEmitter != null) {
                return existingEmitter;
            }

            SseEmitter newEmitter = new SseEmitter(0L);

            newEmitter.onCompletion(() -> emitters.remove(userId));
            newEmitter.onTimeout(() -> emitters.remove(userId));
            newEmitter.onError((e) -> emitters.remove(userId));

            log.info("User {} subscribed to notifications", userId);

            return newEmitter;
        });
    }

    @Override
    public void sendUserNotification(UserEvent event) {
        NotificationDto notificationDto = notificationService.saveUserNotification(event.userId(), event.title(), event.message());
        SseEmitter emitter = emitters.get(event.userId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(notificationDto));
            } catch (IOException e) {
                emitters.remove(event.userId());
            }
        }
    }

    @Override
    public void sendUserGroupNotification(UserGroupEvent event) {
        final List<NotificationDto> notificationsToSend = notificationService.saveUserGroupNotification(
                emitters.keySet(),
                event.roles(),
                event.title(),
                event.message());
        emitNotifications(notificationsToSend);
    }

    @Override
    public void sendGlobalNotification(GlobalEvent event) {
        final List<NotificationDto> notificationsToSend = notificationService.saveGlobalNotification(
                emitters.keySet(),
                event.title(),
                event.message());
        emitNotifications(notificationsToSend);
    }

    @Scheduled(fixedRate = 10000)
    public void sendHeartbeat() {
        List<Long> failedEmitters = new ArrayList<>();
        List<Map.Entry<Long, SseEmitter>> snapshot = new ArrayList<>(emitters.entrySet());

        for (Map.Entry<Long, SseEmitter> entry : snapshot) {
            try {
                entry.getValue().send(SseEmitter.event().comment("heartbeat"));
            } catch (IOException e) {
                failedEmitters.add(entry.getKey());
            }
        }
        failedEmitters.forEach(emitters::remove);
    }

    private void emitNotifications(List<NotificationDto> notificationsToSend) {
        List<Long> failedEmitters = new ArrayList<>();
        for (NotificationDto notificationDto : notificationsToSend) {
            Long userId = notificationDto.userId();
            SseEmitter sseEmitter = emitters.get(userId);
            if (sseEmitter != null) {
                try {
                    sseEmitter.send(SseEmitter.event().data(notificationDto));
                } catch (IOException e) {
                    failedEmitters.add(userId);
                }
            }
        }
        failedEmitters.forEach(emitters::remove);
    }
}
