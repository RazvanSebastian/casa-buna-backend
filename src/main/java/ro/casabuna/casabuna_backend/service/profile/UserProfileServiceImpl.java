package ro.casabuna.casabuna_backend.service.profile;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.dto.user.UserProfileDto;
import ro.casabuna.casabuna_backend.model.entity.UserProfileEntity;
import ro.casabuna.casabuna_backend.model.mapper.user.UserProfileMapper;
import ro.casabuna.casabuna_backend.repository.UserProfileRepository;
import ro.casabuna.casabuna_backend.repository.UserRepository;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void updateProfile(UserProfileDto userProfileDto) {
        UserProfileEntity entity = userProfileMapper.toEntity(userProfileDto);
        entity.setUser(userRepository.getReferenceById(entity.getId()));
        userProfileRepository.save(entity);
    }

}
