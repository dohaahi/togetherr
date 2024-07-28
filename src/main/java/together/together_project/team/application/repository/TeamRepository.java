package together.together_project.team.application.repository;

import java.util.Optional;
import together.together_project.team.domain.Team;

public interface TeamRepository {

    Team save(Team team);

    boolean existsByName(String name);

    Optional<Team> findById(Long teamId);
}
