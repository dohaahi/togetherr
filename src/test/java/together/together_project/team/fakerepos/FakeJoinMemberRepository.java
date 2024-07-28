package together.together_project.team.fakerepos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import together.together_project.team.application.repository.JoinMemberRepository;
import together.together_project.team.domain.JoinMember;
import together.together_project.team.domain.Member;
import together.together_project.team.domain.Team;

public class FakeJoinMemberRepository implements JoinMemberRepository {

    private final List<JoinMember> store = new ArrayList<>();

    private static Long SEQUENCE = 1L;

    @Override
    public Optional<JoinMember> findByJoinerAndTeam(Member member, Team team) {
        for (JoinMember joinMember : store) {
            if (joinMember.getTeam().equals(team) && joinMember.getJoiner().equals(member)) {
                return Optional.of(joinMember);
            }
        }
        return Optional.empty();
    }

    @Override
    public JoinMember save(JoinMember member) {
        if (member.getId() == null) {
            member.setId(SEQUENCE++);
        }
        store.add(member);
        return member;
    }

    @Override
    public void delete(JoinMember member) {
        store.removeIf(joinMember -> joinMember.getId().equals(member.getId()));
    }
}
