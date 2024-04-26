package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.StudyPostComment;

public interface StudyPostCommentJpaRepository extends JpaRepository<StudyPostComment, Long> {
}
