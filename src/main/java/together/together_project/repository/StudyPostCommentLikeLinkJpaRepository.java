package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.StudyPostCommentLikeLink;

public interface StudyPostCommentLikeLinkJpaRepository extends JpaRepository<StudyPostCommentLikeLink, Long> {
}
