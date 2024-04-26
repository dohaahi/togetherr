package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.StudyPostLikeLink;

public interface StudyPostLikeLinkJpaRepository extends JpaRepository<StudyPostLikeLink, Long> {
}
