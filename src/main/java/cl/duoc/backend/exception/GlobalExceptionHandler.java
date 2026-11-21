package cl.duoc.backend.exception;

import cl.duoc.backend.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Manejo global de errores de los controllers.
 *
 * Extiende ResponseEntityExceptionHandler, que ya cubre las excepciones típicas de Spring MVC
 * (método no soportado, JSON ilegible, parámetro faltante, tipo incorrecto, ResponseStatusException, etc.).
 * Todas esas salen por handleExceptionInternal, que aquí se redefine para devolver ApiErrorDto.
 *
 * Los errores de Security que ocurren en los filtros (401/403 antes de llegar al controller)
 * se manejan en RestAuthenticationEntryPoint y RestAccessDeniedHandler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ApiErrorFactory errorFactory;

    public GlobalExceptionHandler(ApiErrorFactory errorFactory) {
        this.errorFactory = errorFactory;
    }

    // ---------- Errores de dominio ----------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiErrorDto> handleBusinessRule(BusinessRuleException ex, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, null);
    }

    // ---------- Base de datos ----------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDto> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        // El detalle técnico (constraint, tabla, SQL) solo va al log, nunca al cliente.
        log.warn("Violación de integridad en {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "La operación entra en conflicto con datos existentes", request, null);
    }

    // ---------- Validación ----------

    /** Validación de @RequestParam / @PathVariable con @Validated en el controller. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .sorted()
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Solicitud inválida", request, details);
    }

    /** Validación de @Valid @RequestBody (devuelve el detalle por campo). */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
                .sorted()
                .toList();
        log.debug("Validación fallida en {}: {}", pathOf(request), details);
        return ResponseEntity.status(status)
                .headers(headers)
                .body(errorFactory.create(status, "Solicitud inválida", pathOf(request), details));
    }

    // ---------- Security dentro de controllers (@PreAuthorize, etc.) ----------

    /**
     * Si el usuario es anónimo devolvemos 401 (debe autenticarse);
     * si ya está autenticado pero sin rol suficiente, 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean anonymous = auth == null || auth instanceof AnonymousAuthenticationToken;
        return anonymous
                ? build(HttpStatus.UNAUTHORIZED, "Autenticación requerida", request, null)
                : build(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a este recurso", request, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDto> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Autenticación requerida", request, null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDto> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {
        // Esto fuerza a usar tu mensaje personalizado ("Credenciales inválidas")
        return build(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason(), request, null);
    }

    // ---------- Cualquier otra cosa = bug real (500) ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado procesando {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request, null);
    }

    // ---------- Salida común de todas las excepciones estándar de Spring MVC ----------

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        String path = pathOf(request);
        String message = body instanceof ProblemDetail problem && problem.getDetail() != null
            ? problem.getDetail()
            : (ex instanceof ResponseStatusException rse ? rse.getReason() : null);

        if (statusCode.is5xxServerError()) {
            log.error("Error {} procesando {}", statusCode.value(), path, ex);
            message = "Error interno del servidor";
        } else {
            log.debug("Error {} en {}: {}", statusCode.value(), path, ex.getMessage());
        }

        return ResponseEntity.status(statusCode)
                .headers(headers)
                .body(errorFactory.create(statusCode, message, path, null));
    }

    // ---------- Helpers ----------

    private ResponseEntity<ApiErrorDto> build(
            HttpStatus status, String message, HttpServletRequest request, List<String> details) {
        return ResponseEntity.status(status)
                .body(errorFactory.create(status, message, request.getRequestURI(), details));
    }

    private String pathOf(WebRequest request) {
        return request instanceof ServletWebRequest servletRequest
                ? servletRequest.getRequest().getRequestURI()
                : null;
    }
}
