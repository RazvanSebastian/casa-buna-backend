package ro.casabuna.casabuna_backend.notification.model.event;

public record UserEvent(Long userId, String title, String message) {
}
