package together.together_project.team.application.repository;

import java.util.Optional;
import together.together_project.team.domain.Member;

public interface MemberRepository {

    public Member save(Member member);

    public Optional<Member> findById(Long id);

    public Optional<Member> findByUsername(String username);
}
