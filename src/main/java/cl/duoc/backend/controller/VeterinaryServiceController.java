package cl.duoc.backend.controller;

import cl.duoc.backend.dto.VeterinaryServiceDto;
import cl.duoc.backend.entity.ServiceCategory;
import cl.duoc.backend.entity.VeterinaryService;
import cl.duoc.backend.repository.ServiceCategoryRepository;
import cl.duoc.backend.repository.VeterinaryServiceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class VeterinaryServiceController extends AbstractCrudController<VeterinaryService, VeterinaryServiceDto> {
    private final VeterinaryServiceRepository serviceRepository;
    private final ServiceCategoryRepository categoryRepository;

    @Override
    protected JpaRepository<VeterinaryService, String> repository() {
        return serviceRepository;
    }

    @Override
    protected VeterinaryServiceDto toDto(VeterinaryService entity) {
        VeterinaryServiceDto dto = new VeterinaryServiceDto();
        dto.setIdService(entity.getIdService());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCategoryId(entity.getCategory() == null ? null : entity.getCategory().getIdServiceCategory());
        return dto;
    }

    @Override
    protected VeterinaryService toEntity(VeterinaryServiceDto dto) {
        VeterinaryService entity = new VeterinaryService();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(VeterinaryService entity, VeterinaryServiceDto dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategoryId() == null ? null : categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría de servicio no encontrada")));
    }

    @Override
    protected String id(VeterinaryService entity) {
        return entity.getIdService();
    }
}
