package ro.casabuna.casabuna_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<UserEntity> findWithRolesByEmail(String email);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<UserEntity> findWithRolesById(Long id);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.teams WHERE u.id = :id AND EXISTS (SELECT 1 FROM u.roles r WHERE r.name IN :roleNames)")
    Optional<UserEntity> findWithTeamsByIdAndRoles(@Param("id") Long id, @Param("roleNames") Set<RoleName> roleNames);

    @Query("SELECT u.id FROM UserEntity u")
    Set<Long> findAllIds();

    @Query("SELECT DISTINCT u.id FROM UserEntity u JOIN u.roles r WHERE r.name IN :roles")
    Set<Long> findIdsByRoles(@Param("roles") Set<RoleName> roles);

    boolean existsByIdAndTeamsId(Long id, Long teamId);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u JOIN u.teams t JOIN u.roles r WHERE u.id = :userId AND t.id = :teamId AND r.name IN :roleNames")
    boolean existsByIdAndTeamIdAndRoleNames(@Param("userId") Long userId, @Param("teamId") Long teamId, @Param("roleNames") Set<RoleName> roleNames);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM team_members WHERE user_id = :userId AND team_id = :teamId", nativeQuery = true)
    void deleteTeamMember(@Param("userId") Long userId, @Param("teamId") Long teamId);
}
