package cl.duoc.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public abstract class AbstractCrudController<T, D> {
    protected abstract JpaRepository<T, String> repository();

    protected abstract D toDto(T entity);

    protected abstract T toEntity(D dto);

    protected abstract void updateEntity(T entity, D dto);

    protected abstract String id(T entity);

    @GetMapping
    public List<D> findAll() {
        return repository().findAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public D findById(@PathVariable String id) {
        return toDto(getEntity(id));
    }

    @PostMapping
    public ResponseEntity<D> create(@Valid @RequestBody D dto, HttpServletRequest request) {
        T saved = repository().save(toEntity(dto));
        URI location = URI.create(request.getRequestURL().toString() + "/" + id(saved));
        return ResponseEntity.created(location).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public D update(@PathVariable String id, @Valid @RequestBody D dto) {
        T entity = getEntity(id);
        updateEntity(entity, dto);
        return toDto(repository().save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repository().delete(getEntity(id));
        return ResponseEntity.noContent().build();
    }

    protected T getEntity(String id) {
        return repository().findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Recurso no encontrado"));
    }
}
