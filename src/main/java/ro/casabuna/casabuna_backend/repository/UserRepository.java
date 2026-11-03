package ro.casabuna.casabuna_backend.repository;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<UserEntity> findWithRolesByEmail(String email);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<UserEntity> findWithRolesById(Long id);

    @EntityGraph(attributePaths = {"roles"})
    Page<UserEntity> findAll(@Nullable Specification<UserEntity> spec, Pageable pageable);

    @Query("SELECT u.id FROM UserEntity u")
    Set<Long> findAllIds();

    @Query("SELECT DISTINCT u.id FROM UserEntity u JOIN u.roles r WHERE r.name IN :roles")
    Set<Long> findIdsByRoles(@Param("roles") Set<RoleName> roles);
}
