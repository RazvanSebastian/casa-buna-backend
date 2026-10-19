package ro.casabuna.casabuna_backend.model.dto;

import java.time.LocalDateTime;

public interface AuditableDto {
    LocalDateTime createdAt();
    LocalDateTime updatedAt();
    Long createdByUserId();
    Long lastModifiedByUserId();
}