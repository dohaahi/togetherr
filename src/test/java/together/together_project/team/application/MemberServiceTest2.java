package together.together_project.team.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import together.together_project.team.application.dto.CreateMemberRequest;
import together.together_project.team.domain.Member;
import together.together_project.team.fakerepos.FakeMemberRepository;

class MemberServiceTest2 {

    MemberService memberService = new MemberService(new FakeMemberRepository());

    @DisplayName("중복이 아닌 아이디로는 회원가입 가능")
    @Test
    void signupTest1() {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("username", "a123", "user");

        LocalDateTime now = LocalDateTime.now();
        Member member = memberService.signup(createMemberRequest);

        assertThat(member.getUsername()).isEqualTo("username");
        assertThat(member.getPassword()).isEqualTo("a123");
        assertThat(member.getNickname()).isEqualTo("user");
    }

    @DisplayName("중복된 아이디로는 회원가입 불가")
    @Test
    void signupTest2() {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("username", "a123", "user");

        memberService.signup(createMemberRequest);

        assertThrows(IllegalArgumentException.class, () -> memberService.signup(createMemberRequest), "이미 가입된 회원");
    }
}