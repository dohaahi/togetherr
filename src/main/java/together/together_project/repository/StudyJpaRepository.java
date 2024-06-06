package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.Study;

public interface StudyJpaRepository extends JpaRepository<Study, Long> {
}
