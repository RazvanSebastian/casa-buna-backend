package ro.casabuna.casabuna_backend.model.dto.volunteer;

import java.util.List;

public sealed interface VolunteerRequestDto permits NewVolunteerRequestDto, VolunteerRequestResponseDto, VolunteerRequestWithUserResponseDto {
    String selfDescription();

    List<String> educationActivities();

    List<String> educationLevels();

    List<String> healthActivities();

    List<String> itSupportActivities();

    List<String> logisticActivities();

    String otherActivities();

    String firstName();

    String lastName();

    Integer age();

    String occupation();

    String phone();
}
