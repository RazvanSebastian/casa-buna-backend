package ro.casabuna.casabuna_backend.notification.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ro.casabuna.casabuna_backend.notification.model.dto.NotificationDto;
import ro.casabuna.casabuna_backend.notification.model.entity.NotificationEntity;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "user.id", target = "userId")
    NotificationDto toDto(NotificationEntity notificationEntity);
}
