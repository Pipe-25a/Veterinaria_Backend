package cl.duoc.backend.controller;

import cl.duoc.backend.dto.ServiceCategoryDto;
import cl.duoc.backend.entity.ServiceCategory;
import cl.duoc.backend.repository.ServiceCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/service-categories")
@RequiredArgsConstructor
public class ServiceCategoryController extends AbstractCrudController<ServiceCategory, ServiceCategoryDto> {
    private final ServiceCategoryRepository categoryRepository;

    @Override
    protected JpaRepository<ServiceCategory, String> repository() {
        return categoryRepository;
    }

    @Override
    protected ServiceCategoryDto toDto(ServiceCategory entity) {
        ServiceCategoryDto dto = new ServiceCategoryDto();
        dto.setIdServiceCategory(entity.getIdServiceCategory());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    protected ServiceCategory toEntity(ServiceCategoryDto dto) {
        ServiceCategory entity = new ServiceCategory();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(ServiceCategory entity, ServiceCategoryDto dto) {
        entity.setName(dto.getName());
    }

    @Override
    protected String id(ServiceCategory entity) {
        return entity.getIdServiceCategory();
    }
}
