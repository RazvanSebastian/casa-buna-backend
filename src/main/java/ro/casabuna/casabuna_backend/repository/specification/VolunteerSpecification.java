package ro.casabuna.casabuna_backend.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.UserEntity_;
import ro.casabuna.casabuna_backend.model.entity.volunteer.VolunteerRequestEntity;
import ro.casabuna.casabuna_backend.model.entity.volunteer.VolunteerRequestEntity_;

import java.util.ArrayList;

import static com.nimbusds.oauth2.sdk.util.StringUtils.isNotBlank;

public class VolunteerSpecification {

    public static Specification<VolunteerRequestEntity> withFilters(VolunteerRequestStatus status, @Nullable String requestorEmail) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            root.fetch(VolunteerRequestEntity_.approver, JoinType.LEFT);
            var requestorJoin = (Join<VolunteerRequestEntity, UserEntity>) root.fetch(VolunteerRequestEntity_.requestor, JoinType.LEFT);

            predicates.add(criteriaBuilder.equal(root.get(VolunteerRequestEntity_.STATUS), status));

            if (isNotBlank(requestorEmail)) {
                predicates.add(criteriaBuilder.like(requestorJoin.get(UserEntity_.EMAIL), requestorEmail));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
