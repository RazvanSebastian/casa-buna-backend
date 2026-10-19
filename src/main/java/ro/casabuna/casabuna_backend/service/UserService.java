package ro.casabuna.casabuna_backend.service;

import ro.casabuna.casabuna_backend.model.dto.UserDto;

import java.util.Optional;

public interface UserService {

    Optional<UserDto> findWithRolesByEmail(String email);

    UserDto save(UserDto userDto);
}
