package ro.casabuna.casabuna_backend.notification.api;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ro.casabuna.casabuna_backend.configuration.security.jwt.UserPrincipal;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationDto;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationsPreviewDto;
import ro.casabuna.casabuna_backend.notification.model.event.GlobalEvent;
import ro.casabuna.casabuna_backend.notification.service.NotificationService;
import ro.casabuna.casabuna_backend.notification.service.NotificationSubscriptionService;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final NotificationService notificationService;
    private final NotificationSubscriptionService notificationSubscriptionService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserPrincipal user) {
        return notificationSubscriptionService.subscribe(user.id());
    }

    @GetMapping
    public PagedModel<NotificationDto> findAll(
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute PageQuery pageQuery) {
        return notificationService.findAll(user.id(), pageQuery);
    }

    @GetMapping("/preview")
    public NotificationsPreviewDto getNotificationsPreview(@AuthenticationPrincipal UserPrincipal user) {
        return notificationService.getNotificationsPreview(user.id());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable("id") Long id) {
        boolean isChanged = notificationService.markToRead(user.id(), id);
        return isChanged ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/test")
    public void test() {
        applicationEventPublisher.publishEvent(new GlobalEvent("Title", "message"));
    }
}
