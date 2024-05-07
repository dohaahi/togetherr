package together.together_project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import together.together_project.domain.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.nickname = :nickname")
    Optional<User> findByNickname(String nickname);
}
