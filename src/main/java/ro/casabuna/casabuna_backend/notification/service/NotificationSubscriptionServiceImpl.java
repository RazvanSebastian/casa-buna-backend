package ro.casabuna.casabuna_backend.notification.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationDto;
import ro.casabuna.casabuna_backend.notification.model.entity.NotificationEntity;
import ro.casabuna.casabuna_backend.notification.model.mapper.NotificationMapper;
import ro.casabuna.casabuna_backend.notification.repository.NotificationRepository;
import ro.casabuna.casabuna_backend.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationSubscriptionServiceImpl implements NotificationSubscriptionService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

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
    public void sendNotification(Long userId, String title, String message) {
        UserEntity userEntity = userRepository.getReferenceById(userId);
        NotificationEntity notification = NotificationEntity.builder()
                .user(userEntity)
                .title(title)
                .message(message)
                .read(false)
                .build();
        NotificationDto notificationDto = notificationMapper.toDto(notificationRepository.save(notification));

        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(notificationDto));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }

    @Override
    @Transactional
    public void sendGlobalNotification(String title, String message) {
        List<NotificationEntity> notificationEntities = userRepository.findAllIds().stream()
                .map(id -> NotificationEntity.builder()
                        .user(userRepository.getReferenceById(id))
                        .title(title)
                        .message(message)
                        .read(false)
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        List<NotificationDto> notificationsToSend = notificationRepository.saveAll(notificationEntities)
                .stream()
                .filter(entity -> emitters.containsKey(entity.getUser().getId()))
                .map(notificationMapper::toDto)
                .toList();

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
}
