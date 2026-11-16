package cl.duoc.backend.controller;

import cl.duoc.backend.dto.UserDto;
import cl.duoc.backend.entity.Role;
import cl.duoc.backend.entity.User;
import cl.duoc.backend.repository.RoleRepository;
import cl.duoc.backend.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
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
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            validatePassword(dto.getPassword());
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
        entity.setRole(findRole(dto.getRoleId()));
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener entre 8 y 72 caracteres");
        }
    }

    @Override
    protected String id(User entity) {
        return entity.getIdUser();
    }
}
