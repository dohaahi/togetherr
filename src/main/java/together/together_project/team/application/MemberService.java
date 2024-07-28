package together.together_project.team.application;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.team.application.dto.CreateMemberRequest;
import together.together_project.team.application.repository.MemberRepository;
import together.together_project.team.domain.Member;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member signup(CreateMemberRequest request) {
        memberRepository.findByUsername(request.username())
                .ifPresent(e -> {
                    throw new IllegalArgumentException("이미 가입된 회원");
                });

        Member member = Member.builder()
                .username(request.username())
                .password(request.password())
                .nickname(request.nickname())
                .build();

        return memberRepository.save(member);
    }


    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
