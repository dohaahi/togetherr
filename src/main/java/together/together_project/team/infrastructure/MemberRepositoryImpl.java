package together.together_project.team.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.team.application.repository.MemberRepository;
import together.together_project.team.domain.Member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final JPAQueryFactory q;

    private final MemberJapRepository memberJapRepository;

    @Override
    public Member save(Member member) {
        return memberJapRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJapRepository.findById(id);
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        return memberJapRepository.findByUsername(username);
    }

}
