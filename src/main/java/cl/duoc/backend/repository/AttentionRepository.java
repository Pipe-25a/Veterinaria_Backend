package cl.duoc.backend.repository;

import cl.duoc.backend.entity.Attention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttentionRepository extends JpaRepository<Attention, String> {
}
