package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.StudyPost;

public interface StudyPostJpaRepository extends JpaRepository<StudyPost, Long> {
}
