package cl.duoc.backend.repository;

import cl.duoc.backend.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, String> {
}
