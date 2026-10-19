package ro.casabuna.casabuna_backend.api.admin;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.casabuna.casabuna_backend.model.dto.RoleName;
import ro.casabuna.casabuna_backend.model.dto.RoleDto;
import ro.casabuna.casabuna_backend.model.dto.UserDto;
import ro.casabuna.casabuna_backend.service.admin.UserAdministrationService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/user")
@AllArgsConstructor
public class UserAdministrationController {

    private final UserAdministrationService userAdministrationService;

    @PatchMapping("/role")
    public ResponseEntity<?> updateRole(@RequestBody UserDto userDto) {
        Set<RoleName> roleNames = userDto.roles().stream().map(RoleDto::name).collect(Collectors.toSet());
        userAdministrationService.updateRoles(userDto.id(), roleNames);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/role/all")
    public Set<RoleDto> getAllRoles() {
        return userAdministrationService.getAllRoles();
    }

    @GetMapping("/all")
    public PagedModel<UserDto> getAllUsers(@RequestParam(name = "page", defaultValue = "1") int page,
                                           @RequestParam(name = "email", required = false) @Nullable String email) {
        return userAdministrationService.getAllUsers(page, email);
    }
}
