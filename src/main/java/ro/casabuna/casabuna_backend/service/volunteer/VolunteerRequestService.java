package ro.casabuna.casabuna_backend.service.volunteer;

import jakarta.annotation.Nullable;
import org.springframework.data.web.PagedModel;
import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestResponseDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestWithUserResponseDto;
import ro.casabuna.casabuna_backend.model.query.PageQuery;

import java.util.Optional;

public interface VolunteerRequestService {

    void updateRequestStatus(Long userId, Long requestId, VolunteerRequestStatus status);

    VolunteerRequestResponseDto createRequest(Long userId, NewVolunteerRequestDto requestModel);

    Optional<VolunteerRequestResponseDto> getRequestInPending(Long userId);

    PagedModel<VolunteerRequestWithUserResponseDto> getAllRequests(PageQuery pageQuery, @Nullable String requestorEmail, VolunteerRequestStatus status);
}
