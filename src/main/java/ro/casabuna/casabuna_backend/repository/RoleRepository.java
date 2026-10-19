package ro.casabuna.casabuna_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.casabuna.casabuna_backend.model.entity.RoleEntity;
import ro.casabuna.casabuna_backend.model.dto.RoleName;

import java.util.Collection;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Set<RoleEntity> findByNameIn(Collection<RoleName> roles);
}
