package ro.casabuna.casabuna_backend.notification.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ro.casabuna.casabuna_backend.model.entity.UserEntity_;
import ro.casabuna.casabuna_backend.notification.model.entity.NotificationEntity;
import ro.casabuna.casabuna_backend.notification.model.entity.NotificationEntity_;

public final class NotificationSpecifications {

    public static Specification<NotificationEntity> latestByUser(Long userId) {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get(NotificationEntity_.CREATED_AT)));
            return criteriaBuilder.equal(root.get(NotificationEntity_.USER).get(UserEntity_.ID), userId);
        };
    }
}
