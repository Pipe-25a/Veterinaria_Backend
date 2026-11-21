package cl.duoc.backend.controller;

import cl.duoc.backend.dto.MedicineDto;
import cl.duoc.backend.entity.Medicine;
import cl.duoc.backend.entity.MedicineCategory;
import cl.duoc.backend.repository.MedicineCategoryRepository;
import cl.duoc.backend.repository.MedicineRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medicines")
@RequiredArgsConstructor
public class MedicineController extends AbstractCrudController<Medicine, MedicineDto> {
    private final MedicineRepository medicineRepository;
    private final MedicineCategoryRepository categoryRepository;

    @Override
    protected JpaRepository<Medicine, String> repository() {
        return medicineRepository;
    }

    @Override
    protected MedicineDto toDto(Medicine entity) {
        MedicineDto dto = new MedicineDto();
        dto.setIdMedicine(entity.getIdMedicine());
        dto.setCategoryId(entity.getCategory() == null ? null : entity.getCategory().getIdMedicineCategory());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        dto.setActive(entity.isActive());
        return dto;
    }

    @Override
    protected Medicine toEntity(MedicineDto dto) {
        Medicine entity = new Medicine();
        updateEntity(entity, dto);
        entity.setActive(dto.getActive() == null || dto.getActive());
        return entity;
    }

    @Override
    protected void updateEntity(Medicine entity, MedicineDto dto) {
        entity.setCategory(dto.getCategoryId() == null ? null : categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría de medicamento no encontrada")));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }

    @Override
    protected String id(Medicine entity) {
        return entity.getIdMedicine();
    }
}
