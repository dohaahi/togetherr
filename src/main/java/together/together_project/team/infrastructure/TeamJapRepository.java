package together.together_project.team.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.team.domain.Team;

public interface TeamJapRepository extends JpaRepository<Team, Long> {

    boolean existsByName(String name);
}
