package ro.casabuna.casabuna_backend.repository.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ro.casabuna.casabuna_backend.model.entity.UserEntity_;
import ro.casabuna.casabuna_backend.model.entity.team.TeamEntity;
import ro.casabuna.casabuna_backend.model.entity.team.TeamEntity_;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TeamSpecifications {

    public static Specification<TeamEntity> byName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<TeamEntity> byAssignedManager(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            var teamUserJoin = root.join(TeamEntity_.USERS);
            return criteriaBuilder.equal(teamUserJoin.get(UserEntity_.ID), managerId);
        };
    }
}
