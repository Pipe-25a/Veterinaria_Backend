package cl.duoc.backend.security;

import cl.duoc.backend.exception.ApiErrorFactory;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/** Responde 403 en JSON cuando el usuario está autenticado pero no tiene el rol necesario. */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final ApiErrorFactory errorFactory;

    public RestAccessDeniedHandler(ObjectMapper objectMapper, ApiErrorFactory errorFactory) {
        this.objectMapper = objectMapper;
        this.errorFactory = errorFactory;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(
                response.getOutputStream(),
                errorFactory.create(
                        HttpStatus.FORBIDDEN,
                        "No tienes permiso para acceder a este recurso",
                        request.getRequestURI(),
                        null));
    }
}
