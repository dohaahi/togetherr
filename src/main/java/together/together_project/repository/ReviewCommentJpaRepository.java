package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.ReviewComment;

public interface ReviewCommentJpaRepository extends JpaRepository<ReviewComment, Long> {

}
