package ro.casabuna.casabuna_backend.notification.model.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        Long userId,
        String title,
        String message,
        boolean read,
        LocalDateTime createdAt
) {
}
