package cl.duoc.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Formato único de error para toda la API.
 * Instant se serializa en ISO-8601 con zona (UTC), por ejemplo 2026-11-16T14:30:00Z.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorDto {
    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<String> errors;
}
