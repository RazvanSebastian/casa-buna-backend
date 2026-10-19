package ro.casabuna.casabuna_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.entity.RoleEntity;
import ro.casabuna.casabuna_backend.model.dto.RoleName;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.dto.RoleDto;
import ro.casabuna.casabuna_backend.model.dto.UserDto;
import ro.casabuna.casabuna_backend.repository.RoleRepository;
import ro.casabuna.casabuna_backend.repository.UserRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Optional<UserDto> findWithRolesByEmail(String email) {
        return userRepository.findWithRolesByEmail(email).map(UserDto::new);
    }

    @Override
    public UserDto save(UserDto userDto) {
        Set<RoleName> roleNames = userDto.roles()
                .stream()
                .map(RoleDto::name)
                .collect(Collectors.toSet());
        Set<RoleEntity> roleEntities = roleRepository.findByNameIn(roleNames);

        UserEntity userEntity = UserEntity.builder()
                .email(userDto.email())
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .roles(roleEntities)
                .build();

        userEntity = userRepository.save(userEntity);
        return new UserDto(userEntity);
    }

}
