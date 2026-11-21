package cl.duoc.backend.repository;

import cl.duoc.backend.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, String> {
}
