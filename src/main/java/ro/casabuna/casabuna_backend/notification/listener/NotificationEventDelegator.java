package ro.casabuna.casabuna_backend.notification.listener;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import ro.casabuna.casabuna_backend.notification.model.event.GlobalEvent;
import ro.casabuna.casabuna_backend.notification.model.event.UserEvent;
import ro.casabuna.casabuna_backend.notification.model.event.UserGroupEvent;
import ro.casabuna.casabuna_backend.notification.service.NotificationSubscriptionService;

@Component
@AllArgsConstructor
public class NotificationEventDelegator {

    private final NotificationSubscriptionService notificationSubscriptionService;

    @TransactionalEventListener(fallbackExecution = true)
    public void handleUserEvent(UserEvent event) {
        notificationSubscriptionService.sendUserNotification(event);
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void handleUserGroupEvent(UserGroupEvent event) {
        notificationSubscriptionService.sendUserGroupNotification(event);
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void handleGlobalEvent(GlobalEvent event) {
        notificationSubscriptionService.sendGlobalNotification(event);
    }
}
