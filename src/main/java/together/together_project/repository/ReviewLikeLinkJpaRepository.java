package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.ReviewLikeLink;

public interface ReviewLikeLinkJpaRepository extends JpaRepository<ReviewLikeLink, Long> {
}
