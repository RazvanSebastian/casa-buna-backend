package ro.casabuna.casabuna_backend.repository.specification;

import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    public static <T> Specification<T> equalsIfPresent(String field, Object value) {
        return ((root, query, criteriaBuilder) ->
                value == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get(field), value));
    }
}
