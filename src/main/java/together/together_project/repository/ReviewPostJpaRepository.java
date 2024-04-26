package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.ReviewPost;

public interface ReviewPostJpaRepository extends JpaRepository<ReviewPost, Long> {
}
