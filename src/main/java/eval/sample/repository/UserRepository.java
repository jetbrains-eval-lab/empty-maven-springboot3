package eval.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import eval.sample.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}