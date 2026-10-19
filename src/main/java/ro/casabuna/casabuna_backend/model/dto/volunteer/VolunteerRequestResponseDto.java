package ro.casabuna.casabuna_backend.model.dto.volunteer;

import ro.casabuna.casabuna_backend.model.dto.AuditableDto;

import java.time.LocalDateTime;
import java.util.List;

public record VolunteerRequestResponseDto(
        Long id,
        String selfDescription,
        String firstName,
        String lastName,
        Integer age,
        String occupation,
        String phone,
        List<String> educationActivities,
        List<String> educationLevels,
        List<String> healthActivities,
        List<String> itSupportActivities,
        List<String> logisticActivities,
        String otherActivities,
        VolunteerRequestStatus status,
        Long requestorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdByUserId,
        Long lastModifiedByUserId
) implements VolunteerRequestDto, AuditableDto {
}
