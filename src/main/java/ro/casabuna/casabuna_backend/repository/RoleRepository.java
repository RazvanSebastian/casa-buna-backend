package ro.casabuna.casabuna_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.casabuna.casabuna_backend.model.entity.RoleEntity;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(RoleName roleName);

    Set<RoleEntity> findByNameIn(Collection<RoleName> roles);
}
