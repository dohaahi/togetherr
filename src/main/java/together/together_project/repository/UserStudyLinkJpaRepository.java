package together.together_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.domain.UserStudyLink;

public interface UserStudyLinkJpaRepository extends JpaRepository<UserStudyLink, Long> {
}
