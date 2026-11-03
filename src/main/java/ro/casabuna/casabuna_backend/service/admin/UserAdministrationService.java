package ro.casabuna.casabuna_backend.service.admin;

import jakarta.annotation.Nullable;
import org.springframework.data.web.PagedModel;
import ro.casabuna.casabuna_backend.model.dto.user.RoleName;
import ro.casabuna.casabuna_backend.model.dto.user.RoleDto;
import ro.casabuna.casabuna_backend.model.dto.user.UserDto;
import ro.casabuna.casabuna_backend.model.query.PageQuery;

import java.util.Set;

public interface UserAdministrationService {

    void updateRoles(long id, Set<RoleName> roles);

    PagedModel<UserDto> getAllUsers(PageQuery pageQuery, @Nullable String email);

    Set<RoleDto> getAllRoles();
}
