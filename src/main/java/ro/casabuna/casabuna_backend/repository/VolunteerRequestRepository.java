package ro.casabuna.casabuna_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.casabuna.casabuna_backend.model.entity.volunteer.VolunteerRequestEntity;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;

import java.util.Optional;

@Repository
public interface VolunteerRequestRepository extends JpaRepository<VolunteerRequestEntity, Long> {

    boolean existsByRequestor_IdAndStatus(Long userId, VolunteerRequestStatus status);

    Optional<VolunteerRequestEntity> findByRequestor_IdAndStatus(Long userId, VolunteerRequestStatus status);
}
