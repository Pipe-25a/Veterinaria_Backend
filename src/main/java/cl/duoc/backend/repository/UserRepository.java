package cl.duoc.backend.repository;

import cl.duoc.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);
}
