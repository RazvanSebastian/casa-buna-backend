package ro.casabuna.casabuna_backend.service.volunteer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestResponseDto;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;
import ro.casabuna.casabuna_backend.exception.ResourceAlreadyExistsException;
import ro.casabuna.casabuna_backend.exception.ResourceNotFound;
import ro.casabuna.casabuna_backend.model.mapper.volunteer.VolunteerRequestMapper;
import ro.casabuna.casabuna_backend.repository.UserRepository;
import ro.casabuna.casabuna_backend.repository.VolunteerRequestRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VolunteerRequestServiceImpl implements VolunteerRequestService {
    private final VolunteerRequestRepository volunteerRequestRepository;
    private final UserRepository userRepository;
    private final VolunteerRequestMapper volunteerRequestMapper;

    @Transactional
    @Override
    public void approveRequest(Long userId, Long requestId) {
        var requestEntity = volunteerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFound("No request in pending for given id and requestor id"));

        requestEntity.setStatus(VolunteerRequestStatus.APPROVED);
        requestEntity.setApprover(userRepository.getReferenceById(userId));

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
}
