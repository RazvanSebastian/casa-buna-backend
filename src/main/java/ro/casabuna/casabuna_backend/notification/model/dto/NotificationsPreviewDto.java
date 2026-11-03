package ro.casabuna.casabuna_backend.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationsPreviewDto {
    private int unreadCounter;
    private List<NotificationDto> latest;
}
