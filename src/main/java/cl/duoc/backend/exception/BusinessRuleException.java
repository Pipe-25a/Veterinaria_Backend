package cl.duoc.backend.exception;

/**
 * Se violó una regla de negocio (422). Ejemplo: agendar una cita en un horario ocupado,
 * o dar de alta una mascota sin dueño. Úsala en vez de IllegalArgumentException.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
