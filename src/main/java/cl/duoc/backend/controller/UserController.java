package cl.duoc.backend.controller;

import cl.duoc.backend.dto.UserDto;
import cl.duoc.backend.entity.Role;
import cl.duoc.backend.entity.User;
import cl.duoc.backend.repository.RoleRepository;
import cl.duoc.backend.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController extends AbstractCrudController<User, UserDto> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected JpaRepository<User, String> repository() {
        return userRepository;
    }

    @Override
    protected UserDto toDto(User entity) {
        UserDto dto = new UserDto();
        dto.setIdUser(entity.getIdUser());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setActive(entity.isActive());
        dto.setRoleId(entity.getRole() == null ? null : entity.getRole().getIdRole());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    protected User toEntity(UserDto dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }
        validatePassword(dto.getPassword());
        User entity = new User();
        updateEntity(entity, dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setActive(dto.getActive() == null || dto.getActive());
        return entity;
    }

    @Override
    protected void updateEntity(User entity, UserDto dto) {
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());

        // Solo actualiza la contraseña si se envía una nueva
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            validatePassword(dto.getPassword());
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Actualiza el estado activo si se envía
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }

        entity.setRole(findRole(dto.getRoleId()));
    }

    /**
     * Desactivar usuario (Soft Delete)
     * En lugar de borrar físicamente al usuario (lo que rompería las llaves
     * foráneas de citas, mascotas, etc.),
     * lo marcamos como inactivo.
     */
    @PatchMapping("/{id}/desactivar")
    public UserDto desactivarUsuario(@PathVariable String id) {
        User user = getEntity(id);

        // Evitar que el administrador actual se desactive a sí mismo
        if (esElUsuarioActual(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No puedes desactivar tu propia cuenta de administrador");
        }

        user.setActive(false);
        return toDto(userRepository.save(user));
    }

    /* Eliminar usuario (Hard Delete) */
    @Override
    public org.springframework.http.ResponseEntity<Void> delete(@PathVariable String id) {
        User user = getEntity(id);

        // Evitar que el administrador actual se elimine a sí mismo
        if (esElUsuarioActual(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No puedes eliminar tu propia cuenta de administrador");
        }

        return super.delete(id);
    }

    private Role findRole(String roleId) {
        if (roleId == null) {
            return null;
        }
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
    }

    private void validatePassword(String password) {
        if (password.length() < 8 || password.length() > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener entre 8 y 72 caracteres");
        }
    }

    /**
     * Obtiene el ID del usuario que está haciendo la petición desde el token JWT.
     */
    private String obtenerIdUsuarioActual() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject(); // El subject es el ID del usuario en tu AuthService
        }
        return null;
    }

    private boolean esElUsuarioActual(User user) {
        String currentId = obtenerIdUsuarioActual();
        return currentId != null && currentId.equals(user.getIdUser());
    }

    @Override
    protected String id(User entity) {
        return entity.getIdUser();
    }
}