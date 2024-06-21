package vu.jesource.authentication.web.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import vu.jesource.authentication.web.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findUserByEmailAndUsername(String email, String username);
}
