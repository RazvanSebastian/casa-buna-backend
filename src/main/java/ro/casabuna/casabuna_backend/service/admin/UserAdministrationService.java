package ro.casabuna.casabuna_backend.service.admin;

import jakarta.annotation.Nullable;
import org.springframework.data.web.PagedModel;
import ro.casabuna.casabuna_backend.model.dto.RoleName;
import ro.casabuna.casabuna_backend.model.dto.RoleDto;
import ro.casabuna.casabuna_backend.model.dto.UserDto;

import java.util.Set;

public interface UserAdministrationService {

    void updateRoles(long id, Set<RoleName> roles);

    PagedModel<UserDto> getAllUsers(int page, @Nullable String email);

    Set<RoleDto> getAllRoles();
}
