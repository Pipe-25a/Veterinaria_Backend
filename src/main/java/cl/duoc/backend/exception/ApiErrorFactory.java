package cl.duoc.backend.exception;

import cl.duoc.backend.dto.ApiErrorDto;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

/** Construye el ApiErrorDto en un solo lugar para el handler global y los handlers de Security. */
@Component
public class ApiErrorFactory {

    public ApiErrorDto create(HttpStatusCode status, String message, String path, List<String> details) {
        HttpStatus known = HttpStatus.resolve(status.value());
        return ApiErrorDto.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(known == null ? status.toString() : known.getReasonPhrase())
                .message(message == null || message.isBlank() ? "Solicitud fallida" : message)
                .path(path)
                .errors(details == null || details.isEmpty() ? null : details)
                .build();
    }
}
