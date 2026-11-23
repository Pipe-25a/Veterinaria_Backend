package cl.duoc.backend.controller;

import cl.duoc.backend.dto.ApiErrorDto;
import cl.duoc.backend.dto.UserDto;
import cl.duoc.backend.entity.Role;
import cl.duoc.backend.entity.User;
import cl.duoc.backend.repository.RoleRepository;
import cl.duoc.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Users", description = "Gestión de usuarios. Todos los endpoints requieren JWT con rol ADMIN.")
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

    // ---------- Endpoints CRUD heredados (solo documentación) ----------

    @Override
    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios, incluidos los inactivos. Rol: ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios"),
            @ApiResponse(responseCode = "401", description = "Sin token o token inválido", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso", content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public List<UserDto> findAll() {
        return super.findAll();
    }

    @Override
    @Operation(summary = "Consultar usuario por id", description = "Rol: ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "401", description = "Sin token o token inválido", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public UserDto findById(@PathVariable String id) {
        return super.findById(id);
    }

    @Override
    @Operation(summary = "Crear usuario", description = "username, email y password (8 a 72 caracteres) son obligatorios. "
            + "Si no se envía roleId, el usuario queda con rol CLIENTE. Rol: ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Sin token o token inválido", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "Usuario o correo duplicado", content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto dto, HttpServletRequest request) {
        return super.create(dto, request);
    }

    @Override
    @Operation(summary = "Editar usuario", description = "Actualiza username, email, rol y estado. La contraseña solo cambia si se envía una nueva. "
            + "Si no se envía roleId, se mantiene el rol actual. Rol: ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Sin token o token inválido", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "Usuario o correo duplicado", content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public UserDto update(@PathVariable String id, @Valid @RequestBody UserDto dto) {
        return super.update(id, dto);
    }

    // ---------- Conversión DTO <-> entidad (sin cambios) ----------

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

        // LÓGICA DEL ROL
        if (dto.getRoleId() != null && !dto.getRoleId().isBlank()) {
            entity.setRole(findRole(dto.getRoleId()));
        } else if (entity.getRole() == null) {
            Role defaultRole = roleRepository.findByName("CLIENTE")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Rol CLIENTE no configurado"));
            entity.setRole(defaultRole);
        }
    }

    // ---------- Endpoints propios ----------

    /**
     * Desactivar usuario (Soft Delete)
     * En lugar de borrar físicamente al usuario (lo que rompería las llaves
     * foráneas de citas, mascotas, etc.), lo marcamos como inactivo.
     */
    @Operation(summary = "Desactivar usuario", description = "Desactivación lógica (active = false). El usuario ya no puede iniciar sesión. "
            + "Un administrador no puede desactivarse a sí mismo. Rol: ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario desactivado"),
            @ApiResponse(responseCode = "400", description = "Intento de desactivar la propia cuenta", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Sin token o token inválido", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
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
    @Operation(summary = "Eliminar usuario", description = "Eliminación física. Un administrador no puede eliminarse a sí mismo. "
            + "Puede fallar con 409 si el usuario tiene mascotas o citas asociadas. Rol: ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "400", description = "Intento de eliminar la propia cuenta", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Sin token o token inválido", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "El usuario tiene datos asociados", content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        User user = getEntity(id);

        // Evitar que el administrador actual se elimine a sí mismo
        if (esElUsuarioActual(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No puedes eliminar tu propia cuenta de administrador");
        }

        return super.delete(id);
    }

    // ---------- Auxiliares (sin cambios) ----------

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

    /** Obtiene el ID del usuario que hace la petición desde el token JWT. */
    private String obtenerIdUsuarioActual() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
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