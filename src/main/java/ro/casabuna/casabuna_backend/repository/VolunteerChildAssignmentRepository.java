package ro.casabuna.casabuna_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.entity.team.TeamEntity;
import ro.casabuna.casabuna_backend.model.entity.team.VolunteerChildAssignmentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerChildAssignmentRepository extends JpaRepository<VolunteerChildAssignmentEntity, Long>, JpaSpecificationExecutor<VolunteerChildAssignmentEntity> {

    Optional<VolunteerChildAssignmentEntity> findByVolunteerAndChildAndTeam(UserEntity volunteer, UserEntity child, TeamEntity team);

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM VolunteerChildAssignmentEntity assignment
            WHERE assignment.team = :team AND (assignment.volunteer = :user OR assignment.child = :user)
            """)
    void deleteByTeamAndUser(@Param("team") TeamEntity team, @Param("user") UserEntity user);

    @Query("""
        select assignment
        from VolunteerChildAssignmentEntity assignment
        join fetch assignment.child
        where assignment.team.id = :teamId
        """)
    List<VolunteerChildAssignmentEntity> findAllWithChildByTeamId(@Param("teamId") Long teamId);

}
