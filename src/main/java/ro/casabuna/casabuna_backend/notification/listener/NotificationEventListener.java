package ro.casabuna.casabuna_backend.notification.listener;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ro.casabuna.casabuna_backend.notification.model.event.GlobalEvent;
import ro.casabuna.casabuna_backend.notification.model.event.VolunteerRequestStatusChangeEvent;
import ro.casabuna.casabuna_backend.notification.service.NotificationSubscriptionService;

@Component
@AllArgsConstructor
public class NotificationEventListener {

    private final NotificationSubscriptionService notificationSubscriptionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVolunteerRequestStatusChange(VolunteerRequestStatusChangeEvent event) {
        final String message = "Cererea ta de voluntariat a fost" + (event.approved() ? "aprobata" : "refuzata");
        final String title = "Status cerere";
        notificationSubscriptionService.sendNotification(event.userId(), title, message);
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void handleGlobalEvent(GlobalEvent event) {
        notificationSubscriptionService.sendGlobalNotification(event.title(), event.message());
    }
}
