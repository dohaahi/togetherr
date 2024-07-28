package together.together_project.team.infrastructure;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.team.application.repository.JoinMemberRepository;
import together.together_project.team.domain.JoinMember;
import together.together_project.team.domain.Member;
import together.together_project.team.domain.Team;

@Repository
@RequiredArgsConstructor
public class JoinMemberRepositoryImpl implements JoinMemberRepository {

    private final JoinMemberJpaRepository joinMemberJpaRepository;

    @Override
    public Optional<JoinMember> findByJoinerAndTeam(Member member, Team team) {
        return joinMemberJpaRepository.findByJoinerAndTeam(member, team);
    }

    @Override
    public JoinMember save(JoinMember member) {
        return joinMemberJpaRepository.save(member);
    }

    @Override
    public void delete(JoinMember member) {
        joinMemberJpaRepository.delete(member);
    }
}
