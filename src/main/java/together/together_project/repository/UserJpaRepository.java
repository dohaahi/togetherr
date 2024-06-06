package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
