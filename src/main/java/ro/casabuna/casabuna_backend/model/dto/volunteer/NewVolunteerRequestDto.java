package ro.casabuna.casabuna_backend.model.dto.volunteer;

import jakarta.validation.constraints.*;
import java.util.List;

public record NewVolunteerRequestDto(
        @NotBlank(message = "Self description is required")
        String selfDescription,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotNull(message = "Age is required")
        Integer age,

        String occupation,

        @NotBlank(message = "Phone is required")
        String phone,

        List<String> educationActivities,

        List<String> educationLevels,

        List<String> healthActivities,

        List<String> itSupportActivities,

        List<String> logisticActivities,

        String otherActivities
) implements VolunteerRequestDto {
}
