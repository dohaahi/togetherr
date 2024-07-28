package together.together_project.team.application.repository;

import java.util.Optional;
import together.together_project.team.domain.JoinMember;
import together.together_project.team.domain.Member;
import together.together_project.team.domain.Team;

public interface JoinMemberRepository {

    Optional<JoinMember> findByJoinerAndTeam(Member member, Team team);

    JoinMember save(JoinMember member);

    void delete(JoinMember member);
}
