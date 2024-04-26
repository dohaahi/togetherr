package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.ReviewCommentLikeLink;

public interface ReviewCommentLikeLinkJpaRepository extends JpaRepository<ReviewCommentLikeLink, Long> {
}
