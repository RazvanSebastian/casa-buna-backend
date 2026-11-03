package ro.casabuna.casabuna_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.casabuna.casabuna_backend.model.entity.UserProfileEntity;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
}
