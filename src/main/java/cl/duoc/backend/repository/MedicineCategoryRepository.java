package cl.duoc.backend.repository;

import cl.duoc.backend.entity.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineCategoryRepository extends JpaRepository<MedicineCategory, String> {
}
