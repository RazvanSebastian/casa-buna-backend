package ro.casabuna.casabuna_backend.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.UserEntity_;
import ro.casabuna.casabuna_backend.model.entity.volunteer.VolunteerRequestEntity;
import ro.casabuna.casabuna_backend.model.entity.volunteer.VolunteerRequestEntity_;

import java.util.ArrayList;

import static com.nimbusds.oauth2.sdk.util.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VolunteerRequestSpecification {

    public static Specification<VolunteerRequestEntity> withFilters(VolunteerRequestStatus status, @Nullable String requestorEmail) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(criteriaBuilder.equal(root.get(VolunteerRequestEntity_.STATUS), status));

            Join<VolunteerRequestEntity, UserEntity> requestorJoin;

            boolean isCount = query.getResultType() == Long.class || query.getResultType() == long.class;
            if (!isCount) {
                requestorJoin = (Join<VolunteerRequestEntity, UserEntity>) root.fetch(VolunteerRequestEntity_.requestor, JoinType.LEFT);
                query.distinct(true);
            } else {
                requestorJoin = root.join(VolunteerRequestEntity_.requestor, JoinType.LEFT);
            }

            if (isNotBlank(requestorEmail)) {
                predicates.add(criteriaBuilder.like(requestorJoin.get(UserEntity_.EMAIL), "%" + requestorEmail + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
