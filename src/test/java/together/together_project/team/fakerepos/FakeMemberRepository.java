package together.together_project.team.fakerepos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import together.together_project.team.application.repository.MemberRepository;
import together.together_project.team.domain.Member;

public class FakeMemberRepository implements MemberRepository {

    private static Long SEQUENCE = 1L;

    private final Map<Long, Member> store = new HashMap<>();

    @Override
    public Member save(Member member) {
        if (member.getId() == null) {
            member.setId(SEQUENCE++);
        }
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        for (Member member : store.values()) {
            if (member.getUsername().equals(username)) {
                return Optional.of(member);
            }
        }
        return Optional.empty();
    }
}
