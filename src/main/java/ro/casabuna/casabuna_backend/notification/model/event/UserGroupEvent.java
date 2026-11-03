package ro.casabuna.casabuna_backend.notification.model.event;

import ro.casabuna.casabuna_backend.model.dto.user.RoleName;

import java.util.Set;

public record UserGroupEvent(Set<RoleName> roles, String title, String message) {
}
