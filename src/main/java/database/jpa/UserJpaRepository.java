package database.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import users.User;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String username);
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    List<User> findByNameContainingIgnoreCase(
            String nameQuery, Pageable pageable);
}
