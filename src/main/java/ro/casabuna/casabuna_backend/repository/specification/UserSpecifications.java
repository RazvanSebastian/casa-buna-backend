package ro.casabuna.casabuna_backend.repository.specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.entity.RoleEntity_;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.UserEntity_;
import ro.casabuna.casabuna_backend.model.entity.team.TeamEntity_;
import ro.casabuna.casabuna_backend.model.entity.team.VolunteerChildAssignmentEntity;
import ro.casabuna.casabuna_backend.model.entity.team.VolunteerChildAssignmentEntity_;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSpecifications {


    public static Specification<UserEntity> equalsIfPresent(String field, Object value, boolean fetchRoles) {
        return ((root, query, criteriaBuilder) -> {
            if (fetchRoles && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch(UserEntity_.ROLES);
            }
            return value == null ?
                    criteriaBuilder.conjunction() :
                    criteriaBuilder.equal(root.get(field), value);
        });
    }

    public static Specification<UserEntity> teamManagers(Long teamId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            var roleJoin = root.join(UserEntity_.roles);
            var rolePredicate = criteriaBuilder.equal(roleJoin.get(RoleEntity_.NAME), RoleName.ROLE_MANAGER);

            var teamJoin = root.join(UserEntity_.teams);
            var teamPredicate = criteriaBuilder.equal(teamJoin.get(TeamEntity_.ID), teamId);

            return criteriaBuilder.and(rolePredicate, teamPredicate);
        };
    }

    public static Specification<UserEntity> teamVolunteers(Long teamId) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<>();

            var rolesJoin = root.join(UserEntity_.roles);
            var rolesPredicate = criteriaBuilder.equal(rolesJoin.get(RoleEntity_.NAME), RoleName.ROLE_VOLUNTEER);
            predicates.add(rolesPredicate);

            var teamJoin = root.join(UserEntity_.teams);
            var teamPredicate = criteriaBuilder.equal(teamJoin.get(TeamEntity_.ID), teamId);
            predicates.add(teamPredicate);

            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<UserEntity> unassignedChildren(Long teamId) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<>();

            var rolesJoin = root.join(UserEntity_.roles);
            var rolesPredicate = criteriaBuilder.equal(rolesJoin.get(RoleEntity_.NAME), RoleName.ROLE_CHILD);
            predicates.add(rolesPredicate);

            var teamJoin = root.join(UserEntity_.teams);
            var teamPredicate = criteriaBuilder.equal(teamJoin.get(TeamEntity_.ID), teamId);
            predicates.add(teamPredicate);

            Subquery<Long> noAssignmentSubquery = query.subquery(Long.class);
            Root<VolunteerChildAssignmentEntity> assignment = noAssignmentSubquery.from(VolunteerChildAssignmentEntity.class);
            noAssignmentSubquery.select(criteriaBuilder.literal(1L))
                    .where(
                            criteriaBuilder.equal(assignment.get(VolunteerChildAssignmentEntity_.child).get(UserEntity_.id), root.get(UserEntity_.id)),
                            criteriaBuilder.equal(assignment.get(VolunteerChildAssignmentEntity_.team).get(TeamEntity_.ID), teamId)
                    );
            predicates.add(criteriaBuilder.not(criteriaBuilder.exists(noAssignmentSubquery)));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<UserEntity> usersNotInTeam(Long teamId, RoleName roleName, String search) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<>();

            // Has the role
            var rolesJoin = root.join(UserEntity_.roles);
            var rolesPredicate = criteriaBuilder.equal(rolesJoin.get(RoleEntity_.NAME), roleName);
            predicates.add(rolesPredicate);

            // Not in the team
            Subquery<Long> teamSubquery = query.subquery(Long.class);
            var teamRoot = teamSubquery.from(UserEntity.class);
            var teamJoin = teamRoot.join(UserEntity_.teams);
            teamSubquery.select(criteriaBuilder.literal(1L))
                    .where(
                            criteriaBuilder.equal(teamRoot.get(UserEntity_.id), root.get(UserEntity_.id)),
                            criteriaBuilder.equal(teamJoin.get(TeamEntity_.ID), teamId)
                    );
            predicates.add(criteriaBuilder.not(criteriaBuilder.exists(teamSubquery)));

            // Search by email, first name or last name
            if (search != null && !search.isBlank()) {
                var searchPattern = "%" + search.toLowerCase() + "%";
                var emailPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(UserEntity_.EMAIL)),
                        searchPattern
                );
                var firstNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(UserEntity_.FIRST_NAME)),
                        searchPattern
                );
                var lastNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(UserEntity_.LAST_NAME)),
                        searchPattern
                );
                predicates.add(criteriaBuilder.or(emailPredicate, firstNamePredicate, lastNamePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
