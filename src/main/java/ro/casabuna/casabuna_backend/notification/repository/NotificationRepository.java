package ro.casabuna.casabuna_backend.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.casabuna.casabuna_backend.notification.model.entity.NotificationEntity;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>, JpaSpecificationExecutor<NotificationEntity> {

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.user.id = :userId AND n.read = false")
    int countUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.read = true WHERE n.id = :notificationId AND n.user.id = :userId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
}
