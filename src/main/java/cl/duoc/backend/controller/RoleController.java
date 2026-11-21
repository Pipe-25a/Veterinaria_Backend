package cl.duoc.backend.controller;

import cl.duoc.backend.dto.RoleDto;
import cl.duoc.backend.entity.Role;
import cl.duoc.backend.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController extends AbstractCrudController<Role, RoleDto> {
    private final RoleRepository roleRepository;

    @Override
    protected JpaRepository<Role, String> repository() {
        return roleRepository;
    }

    @Override
    protected RoleDto toDto(Role entity) {
        RoleDto dto = new RoleDto();
        dto.setIdRole(entity.getIdRole());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    protected Role toEntity(RoleDto dto) {
        Role entity = new Role();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(Role entity, RoleDto dto) {
        entity.setName(dto.getName());
    }

    @Override
    protected String id(Role entity) {
        return entity.getIdRole();
    }
}
