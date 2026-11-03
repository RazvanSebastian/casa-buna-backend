package ro.casabuna.casabuna_backend.notification.model.event;

public record VolunteerRequestStatusChangeEvent(Long userId, boolean approved) {
}
