package ro.casabuna.casabuna_backend.api.volunteer;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestResponseDto;
import ro.casabuna.casabuna_backend.configuration.security.jwt.UserPrincipal;
import ro.casabuna.casabuna_backend.service.volunteer.VolunteerRequestService;

@RestController
@RequestMapping("/api/volunteer/request")
@AllArgsConstructor
public class VolunteerRequestController {
    private final VolunteerRequestService volunteerRequestService;

    @GetMapping
    public ResponseEntity<VolunteerRequestResponseDto> getRequestInPending(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return volunteerRequestService.getRequestInPending(userPrincipal.id())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VolunteerRequestResponseDto createRequest(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                     @Valid @RequestBody NewVolunteerRequestDto requestModel) {
        return volunteerRequestService.createRequest(userPrincipal.id(), requestModel);
    }
}
