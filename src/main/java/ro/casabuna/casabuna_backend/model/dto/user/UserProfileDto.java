package ro.casabuna.casabuna_backend.model.dto.user;

public record UserProfileDto(
        Long id,
        String phone,
        String selfDescription,
        int age
) {}
