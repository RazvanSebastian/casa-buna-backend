package ro.casabuna.casabuna_backend.service.volunteer;

import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestResponseDto;

import java.util.Optional;

public interface VolunteerRequestService {

    void approveRequest(Long userId, Long requestId);

    VolunteerRequestResponseDto createRequest(Long userId, NewVolunteerRequestDto requestModel);

    Optional<VolunteerRequestResponseDto> getRequestInPending(Long userId);
}
