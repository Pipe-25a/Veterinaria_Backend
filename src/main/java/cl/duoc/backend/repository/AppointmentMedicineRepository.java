package cl.duoc.backend.repository;

import cl.duoc.backend.entity.AppointmentMedicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentMedicineRepository extends JpaRepository<AppointmentMedicine, String> {
}
