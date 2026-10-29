package ro.casabuna.casabuna_backend.service.volunteer;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.exception.ResourceAlreadyExistsException;
import ro.casabuna.casabuna_backend.exception.ResourceNotFound;
import ro.casabuna.casabuna_backend.exception.ResourceUnchangeableException;
import ro.casabuna.casabuna_backend.model.dto.RoleName;
import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestResponseDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestWithUserResponseDto;
import ro.casabuna.casabuna_backend.model.mapper.volunteer.VolunteerRequestMapper;
import ro.casabuna.casabuna_backend.repository.UserRepository;
import ro.casabuna.casabuna_backend.repository.VolunteerRequestRepository;
import ro.casabuna.casabuna_backend.repository.specification.VolunteerSpecification;
import ro.casabuna.casabuna_backend.service.admin.UserAdministrationService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VolunteerRequestServiceImpl implements VolunteerRequestService {
    private final VolunteerRequestRepository volunteerRequestRepository;
    private final UserRepository userRepository;

    private final UserAdministrationService userAdministrationService;

    private final VolunteerRequestMapper volunteerRequestMapper;

    @Transactional
    @Override
    public void updateRequestStatus(Long userId, Long requestId, VolunteerRequestStatus status) {
        var requestEntity = volunteerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFound("No request in pending for given id and requestor id"));

        if (requestEntity.getStatus() == VolunteerRequestStatus.APPROVED) {
            throw new ResourceUnchangeableException("An approved request cannot be changed");
        }

        requestEntity.setStatus(status);
        requestEntity.setApprover(userRepository.getReferenceById(userId));

        if (status == VolunteerRequestStatus.APPROVED) {
            // at this moment the user has only ROLE_USER
            var roles = new HashSet<>(List.of(RoleName.ROLE_USER, RoleName.ROLE_VOLUNTEER));
            userAdministrationService.updateRoles(requestEntity.getRequestor().getId(), roles);
        }

        volunteerRequestRepository.save(requestEntity);
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

    @Transactional(readOnly = true)
    @Override
    public Optional<VolunteerRequestResponseDto> getRequestInPending(Long userId) {
        return volunteerRequestRepository.findByRequestor_IdAndStatus(userId, VolunteerRequestStatus.PENDING)
                .map(volunteerRequestMapper::toDto);
    }

    @Override
    public PagedModel<VolunteerRequestWithUserResponseDto> getAllRequests(int page, String requestorEmail, VolunteerRequestStatus status) {
        status = status == null ? VolunteerRequestStatus.PENDING : status;
        var spec = VolunteerSpecification.withFilters(status, requestorEmail);
        return new PagedModel<>(volunteerRequestRepository.findAll(spec, PageRequest.of(page - 1, 10))
                .map(volunteerRequestMapper::toDtoWithUser));
    }
}
