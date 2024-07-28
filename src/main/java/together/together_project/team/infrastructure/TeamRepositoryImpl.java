package together.together_project.team.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.team.application.repository.TeamRepository;
import together.together_project.team.domain.Team;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepository {

    private final JPAQueryFactory q;

    private final TeamJapRepository teamJapRepository;

    @Override
    public Team save(Team team) {
        return teamJapRepository.save(team);
    }

    @Override
    public boolean existsByName(String name) {
        return teamJapRepository.existsByName(name);
    }

    @Override
    public Optional<Team> findById(Long teamId) {
        return teamJapRepository.findById(teamId);
    }
}
