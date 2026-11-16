package cl.duoc.backend.exception;

/** El recurso pedido no existe (404). Úsala en los services en vez de NoSuchElementException. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object id) {
        return new ResourceNotFoundException(resource + " con id " + id + " no encontrado");
    }
}
