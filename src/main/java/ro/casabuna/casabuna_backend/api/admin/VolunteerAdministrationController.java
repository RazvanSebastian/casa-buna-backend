package ro.casabuna.casabuna_backend.api.admin;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.casabuna.casabuna_backend.configuration.security.jwt.UserPrincipal;
import ro.casabuna.casabuna_backend.service.volunteer.VolunteerRequestService;

@RestController
@RequestMapping("/api/admin/volunteer")
@AllArgsConstructor
public class VolunteerAdministrationController {

    private final VolunteerRequestService volunteerRequestService;

    @PatchMapping("/request")
    public ResponseEntity<?> approveRequest(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long requestId) {
        volunteerRequestService.approveRequest(userPrincipal.id(), requestId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
