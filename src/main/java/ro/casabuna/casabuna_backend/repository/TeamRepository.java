package ro.casabuna.casabuna_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ro.casabuna.casabuna_backend.model.entity.team.TeamEntity;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long>, JpaSpecificationExecutor<TeamEntity> {

    boolean existsByIdAndUsersId(Long teamId, Long userId);
}
