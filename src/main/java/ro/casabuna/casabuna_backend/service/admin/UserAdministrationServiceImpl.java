package ro.casabuna.casabuna_backend.service.admin;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.dto.UserDto;
import ro.casabuna.casabuna_backend.model.entity.RoleEntity;
import ro.casabuna.casabuna_backend.model.dto.RoleName;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.exception.ResourceNotFound;
import ro.casabuna.casabuna_backend.model.dto.RoleDto;
import ro.casabuna.casabuna_backend.repository.RoleRepository;
import ro.casabuna.casabuna_backend.repository.UserRepository;
import ro.casabuna.casabuna_backend.repository.specification.UserSpecifications;

import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class UserAdministrationServiceImpl implements UserAdministrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public void updateRoles(long id, Set<RoleName> roles) {
        final UserEntity userEntity = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
        final Set<RoleEntity> givenRoleEntities = roleRepository.findByNameIn(roles);

        userEntity.getRoles().removeIf(role -> !givenRoleEntities.contains(role));
        userEntity.getRoles().addAll(givenRoleEntities);
    }

    @Override
    public PagedModel<UserDto> getAllUsers(int page, String email) {
        Specification<UserEntity> spec = UserSpecifications.equalsIfPresent("email", email);
        Page<UserDto> userModelPage = userRepository.findAll(spec, PageRequest.of(page - 1, 20))
                .map(UserDto::new);
        return new PagedModel<>(userModelPage);
    }

    @Override
    public Set<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream().map(RoleDto::new).collect(Collectors.toSet());
    }
}
