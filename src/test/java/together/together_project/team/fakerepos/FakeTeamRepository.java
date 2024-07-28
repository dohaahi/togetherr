package together.together_project.team.fakerepos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import together.together_project.team.application.repository.TeamRepository;
import together.together_project.team.domain.Team;

public class FakeTeamRepository implements TeamRepository {

    private final Map<Long, Team> store = new HashMap<>();

    private static Long SEQUENCE = 1L;

    @Override
    public Team save(Team team) {
        if (team.getId() == null) {
            team.setId(SEQUENCE++);
        }
        store.put(team.getId(), team);
        return team;
    }

    @Override
    public boolean existsByName(String name) {
        for (Team team : store.values()) {
            if (team.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Team> findById(Long teamId) {
        return Optional.ofNullable(store.get(teamId));
    }
}
