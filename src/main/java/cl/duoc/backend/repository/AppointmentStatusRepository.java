package cl.duoc.backend.repository;

import cl.duoc.backend.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatus, String> {
    Optional<AppointmentStatus> findByName(String name);
}