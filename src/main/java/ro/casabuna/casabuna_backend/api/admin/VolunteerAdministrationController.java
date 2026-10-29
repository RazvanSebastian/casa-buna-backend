package ro.casabuna.casabuna_backend.api.admin;

import lombok.AllArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ro.casabuna.casabuna_backend.configuration.security.jwt.UserPrincipal;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestWithUserResponseDto;
import ro.casabuna.casabuna_backend.service.volunteer.VolunteerRequestService;

@RestController
@RequestMapping("/api/admin/volunteer")
@AllArgsConstructor
public class VolunteerAdministrationController {
    private final VolunteerRequestService volunteerRequestService;

    @PatchMapping("/request/{requestId}/status/{status}")
    public ResponseEntity<?> updateRequestStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long requestId,
            @PathVariable VolunteerRequestStatus status) {
        volunteerRequestService.updateRequestStatus(userPrincipal.id(), requestId, status);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/request/all")
    public PagedModel<VolunteerRequestWithUserResponseDto> findAll(
            @RequestParam(name = "page", defaultValue = "10") int page,
            @RequestParam(name = "status", defaultValue = "PENDING") VolunteerRequestStatus status,
            @RequestParam(name="requestorEmail", required = false) String requestorEmail) {
        return volunteerRequestService.getAllRequests(page, requestorEmail, status);
    }
}
