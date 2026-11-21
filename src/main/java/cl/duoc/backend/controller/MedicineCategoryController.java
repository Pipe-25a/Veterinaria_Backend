package cl.duoc.backend.controller;

import cl.duoc.backend.dto.MedicineCategoryDto;
import cl.duoc.backend.entity.MedicineCategory;
import cl.duoc.backend.repository.MedicineCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medicine-categories")
@RequiredArgsConstructor
public class MedicineCategoryController extends AbstractCrudController<MedicineCategory, MedicineCategoryDto> {
    private final MedicineCategoryRepository categoryRepository;

    @Override
    protected JpaRepository<MedicineCategory, String> repository() {
        return categoryRepository;
    }

    @Override
    protected MedicineCategoryDto toDto(MedicineCategory entity) {
        MedicineCategoryDto dto = new MedicineCategoryDto();
        dto.setIdMedicineCategory(entity.getIdMedicineCategory());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    protected MedicineCategory toEntity(MedicineCategoryDto dto) {
        MedicineCategory entity = new MedicineCategory();
        updateEntity(entity, dto);
        return entity;
    }

    @Override
    protected void updateEntity(MedicineCategory entity, MedicineCategoryDto dto) {
        entity.setName(dto.getName());
    }

    @Override
    protected String id(MedicineCategory entity) {
        return entity.getIdMedicineCategory();
    }
}
