package ro.casabuna.casabuna_backend.service.profile;

import ro.casabuna.casabuna_backend.model.dto.user.UserProfileDto;

public interface UserProfileService {

    void updateProfile(UserProfileDto userProfileDto);
}
