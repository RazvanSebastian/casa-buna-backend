package ro.casabuna.casabuna_backend.service.volunteer;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.exception.ResourceAlreadyExistsException;
import ro.casabuna.casabuna_backend.exception.ResourceNotFound;
import ro.casabuna.casabuna_backend.exception.ResourceUnchangeableException;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.dto.user.UserProfileDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestResponseDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestWithUserResponseDto;
import ro.casabuna.casabuna_backend.model.mapper.volunteer.VolunteerRequestMapper;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.notification.model.event.VolunteerRequestStatusChangeEvent;
import ro.casabuna.casabuna_backend.repository.UserRepository;
import ro.casabuna.casabuna_backend.repository.VolunteerRequestRepository;
import ro.casabuna.casabuna_backend.repository.specification.VolunteerSpecification;
import ro.casabuna.casabuna_backend.service.admin.UserAdministrationService;
import ro.casabuna.casabuna_backend.service.profile.UserProfileService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class VolunteerRequestServiceImpl implements VolunteerRequestService {
    private final VolunteerRequestRepository volunteerRequestRepository;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final UserAdministrationService userAdministrationService;
    private final UserProfileService userProfileService;

    private final VolunteerRequestMapper volunteerRequestMapper;

    @Transactional
    @Override
    public void updateRequestStatus(Long approverId, Long requestId, VolunteerRequestStatus status) {
        var requestEntity = volunteerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFound("No request in pending for given id"));

        if (requestEntity.getStatus() != VolunteerRequestStatus.PENDING) {
            throw new ResourceUnchangeableException("Only pending requests can be changed");
        }

        requestEntity.setStatus(status);
        requestEntity.setApprover(userRepository.getReferenceById(approverId));

        if (status == VolunteerRequestStatus.APPROVED) {
            // at this moment the user has only ROLE_USER
            var roles = new HashSet<>(List.of(RoleName.ROLE_USER, RoleName.ROLE_VOLUNTEER));
            userAdministrationService.updateRoles(requestEntity.getRequestor().getId(), roles);

            // update user profile
            var userProfileDto = new UserProfileDto(
                    requestEntity.getRequestor().getId(),
                    requestEntity.getPhone(),
                    requestEntity.getSelfDescription(),
                    requestEntity.getAge()
            );
            userProfileService.updateProfile(userProfileDto);
        }

        volunteerRequestRepository.save(requestEntity);

        eventPublisher.publishEvent(
                new VolunteerRequestStatusChangeEvent(
                        requestEntity.getRequestor().getId(),
                        status == VolunteerRequestStatus.APPROVED)
        );
    }

    @Transactional
    @Override
    public VolunteerRequestResponseDto createRequest(Long userId, NewVolunteerRequestDto requestModel) {
        if (volunteerRequestRepository.existsByRequestor_IdAndStatus(userId, VolunteerRequestStatus.PENDING)) {
            throw new ResourceAlreadyExistsException("A request already exists in pending");
        }

        var newRequestEntity = volunteerRequestMapper.toEntity(requestModel);
        newRequestEntity.setStatus(VolunteerRequestStatus.PENDING);
        newRequestEntity.setRequestor(userRepository.getReferenceById(userId));

        newRequestEntity = volunteerRequestRepository.save(newRequestEntity);
        return volunteerRequestMapper.toDto(newRequestEntity);
    }


    @Override
    public Optional<VolunteerRequestResponseDto> getRequestInPending(Long userId) {
        return volunteerRequestRepository.findByRequestor_IdAndStatus(userId, VolunteerRequestStatus.PENDING)
                .map(volunteerRequestMapper::toDto);
    }

    @Override
    public PagedModel<VolunteerRequestWithUserResponseDto> getAllRequests(PageQuery pageQuery, String requestorEmail, VolunteerRequestStatus status) {
        status = status == null ? VolunteerRequestStatus.PENDING : status;
        var spec = VolunteerSpecification.withFilters(status, requestorEmail);
        return new PagedModel<>(volunteerRequestRepository.findAll(spec, pageQuery.toPageRequest())
                .map(volunteerRequestMapper::toDtoWithUser));
    }
}
