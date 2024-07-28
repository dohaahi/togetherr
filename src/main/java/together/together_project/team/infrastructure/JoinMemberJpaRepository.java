package together.together_project.team.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.team.domain.JoinMember;
import together.together_project.team.domain.Member;
import together.together_project.team.domain.Team;

public interface JoinMemberJpaRepository extends JpaRepository<JoinMember, Long> {

    Optional<JoinMember> findByJoinerAndTeam(Member member, Team team);
}
