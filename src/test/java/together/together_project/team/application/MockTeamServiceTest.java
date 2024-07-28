package together.together_project.team.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static together.together_project.team.domain.TeamStatus.INVITING;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import together.together_project.team.application.dto.CreateTeamRequest;
import together.together_project.team.application.dto.ModifyTeamStatusRequest;
import together.together_project.team.application.repository.MemberRepository;
import together.together_project.team.domain.JoinMember;
import together.together_project.team.domain.Member;
import together.together_project.team.domain.Team;
import together.together_project.team.infrastructure.JoinMemberJpaRepository;
import together.together_project.team.infrastructure.TeamRepositoryImpl;

@ExtendWith(MockitoExtension.class)
class MockTeamServiceTest {

    @InjectMocks
    TeamService teamService;

    @Mock
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    TeamRepositoryImpl teamRepository;

    @Mock
    JoinMemberJpaRepository joinMemberJpaRepository;

//    @DisplayName("팀 만들기")
//    @Test
//    void createTeamTest() {
//        // given
//        CreateMemberRequest createMemberRequest = new CreateMemberRequest("username", "a123", "aaa");
//        Member member = memberService.signup(createMemberRequest);
//
//        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
//
//        // when
//        Team team = teamService.createTeam(member, createTeamRequest);
//
//        // then
//        assertThat(team.getName()).isEqualTo("teamA");
//        assertThat(team.getMemberCountLimit()).isEqualTo(5);
//        assertThat(team.getOwner().getUsername()).isEqualTo("username");
//    }

    @DisplayName("중복 팀명인 경우 생성 실패")
    @Test
    void createTeamTest2() {
        // given
        Member member = Member.builder().username("user").nickname("aa").password("a123").build();

        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);

        // when
        // then
        when(teamRepository.existsByName(any())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(member, createTeamRequest),
                "이미 생성된 팀 이름");
    }

    @DisplayName("owner는 팀 상태 변경 가능")
    @Test
    void test3() {
        // given
        Member member = Member.builder().id(1L).username("user").build();

        // when
        when(teamRepository.findById(any())).thenReturn(
                Optional.of(Team.builder().id(1L).name("team").owner(member).status(INVITING).build()));

        Team resultTeam = teamService.modifyTeamStatus(member, 1L, new ModifyTeamStatusRequest(INVITING));

        // then
        assertThat(resultTeam.getStatus()).isEqualTo(INVITING);
        assertThat(resultTeam.getName()).isEqualTo("team");
        assertThat(resultTeam.getOwner().getUsername()).isEqualTo("user");
    }

//    @DisplayName("owner가 아니면 팀 상태 변경 불가능")
//    @Test
//    void test4() {
//        // given
//        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123", "aaa");
//        Member memberA = memberService.signup(createMemberRequest);
//
//        CreateMemberRequest createMemberRequest2 = new CreateMemberRequest("userB", "a123", "bbb");
//        Member memberB = memberService.signup(createMemberRequest2);
//
//        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
//        Team team = teamService.createTeam(memberA, createTeamRequest);
//
//        // when
//        // then
//        ModifyTeamStatusRequest modifyTeamStatusRequest = new ModifyTeamStatusRequest(COMPLETED);
//
//        assertThatThrownBy(
//                () -> teamService.modifyTeamStatus(memberB, team.getId(), modifyTeamStatusRequest)).isInstanceOf(
//                IllegalArgumentException.class).hasMessage("권한 없음");
//    }

    @DisplayName("가입 신청")
    @Test
    void joinTest() {
        // given
        Member owner = Member.builder().id(1L).username("owner").build();
        Member applier = Member.builder().id(2L).username("applier").build();
        Team team = Team.builder().id(1L).owner(owner).memberCountLimit(5).status(INVITING).build();
        JoinMember joinMember1 = JoinMember.builder().id(1L).team(team).joiner(applier).build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        when(joinMemberJpaRepository.save(any())).thenReturn(joinMember1);

        // when
        LocalDateTime now = LocalDateTime.now();
        JoinMember joinMember = teamService.joinTeam(applier, team.getId());

        // then
        assertThat(joinMember.getTeam()).isEqualTo(team);
        assertThat(joinMember.getJoiner()).isEqualTo(applier);
    }

//    @DisplayName("존재하지 않는 팀에는 가입 불가능")
//    @Test
//    void joinTest2() {
//        // given
//        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
//        Member member = memberService.signup(createMemberRequest);
//
//        // when
//        // then
//        assertThatThrownBy(() -> teamService.joinTeam(member, Long.MAX_VALUE)).isInstanceOf(
//                IllegalArgumentException.class).hasMessage("없는 팀");
//    }
//
//    @DisplayName("리더는 팀에 가입 불가능")
//    @Test
//    void joinTest3() {
//        // given
//        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
//        Member member = memberService.signup(createMemberRequest);
//        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
//        Team team = teamService.createTeam(member, createTeamRequest);
//        team.modifyStatus(member, INVITING);
//
//        // when
//        // then
//        assertThatThrownBy(() -> teamService.joinTeam(member, team.getId())).isInstanceOf(
//                IllegalArgumentException.class).hasMessage("가입 불가");
//    }

    @DisplayName("이미 다른 팀에 참여중이면 가입 신청 불가")
    @Test
    void joinTest4() {
        Member owner = Member.builder().id(1L).username("owner").build();
        Team team = Team.builder().id(1L).owner(owner).build();
        Team prevTeam = Team.builder().id(2L).owner(owner).build();
        Member applier = Member.builder().id(3L).username("applier").team(prevTeam).build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // when
        // then
        assertThatThrownBy(() -> teamService.joinTeam(applier, team.getId())).isInstanceOf(
                IllegalArgumentException.class).hasMessage("가입 불가");

    }

//    // TODO
////    @DisplayName("이미 참여중인 팀에 가입 불가능")
////    @Test
////    void joinTest5() {
////        // given
////        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
////        Member owner = memberService.signup(createMemberRequest);
////        CreateMemberRequest createMemberRequest2 = new CreateMemberRequest("userB", "a123!", "bbb");
////        Member member = memberService.signup(createMemberRequest2);
////        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
////        Team team = teamService.createTeam(owner, createTeamRequest);
////        team.modifyStatus(owner, INVITING);
////
////        teamService.acceptJoining(owner, member.getId(), team.getId());
////
////        // when
////        // then
////        assertThatThrownBy(() -> teamService.joinTeam(owner, team.getId())).isInstanceOf(IllegalArgumentException.class)
////                .hasMessage("가입 불가");
////    }

    @DisplayName("Team이 Inviting 상태가 아니면 가입 신청 불가")
    @Test
    void joinTest6() {
        Member owner = Member.builder().id(1L).username("owner").build();
        Member applier = Member.builder().id(2L).username("applier").build();
        Team team = Team.builder().id(1L).owner(owner).build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // when
        // then
        assertThatThrownBy(() -> teamService.joinTeam(applier, team.getId())).isInstanceOf(
                IllegalArgumentException.class).hasMessage("가입 불가");
    }

    @DisplayName("Team 인원수 초과시 가입 신청 불가")
    @Test
    void joinTest7() {
        Member owner = Member.builder().id(1L).username("owner").build();
        Member applier = Member.builder().id(2L).username("applier").build();
        Team team = Team.builder().id(1L).owner(owner).memberCountLimit(1).build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // when
        // then
        assertThatThrownBy(() -> teamService.joinTeam(applier, team.getId())).isInstanceOf(
                IllegalArgumentException.class);
    }

    @DisplayName("가입 신청 수락 성공")
    @Test
    void test4() {
        // given
        Member owner = Member.builder().id(1L).username("owner").build();
        Member applier = Member.builder().id(2L).username("applier").build();
        Team team = spy(Team.builder().id(1L).owner(owner).memberCountLimit(5).status(INVITING).build());
        JoinMember joiner = JoinMember.builder().id(1L).team(team).joiner(applier).build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(memberService.findById(2L)).thenReturn(Optional.of(applier));
        when(joinMemberJpaRepository.findByJoinerAndTeam(applier, team)).thenReturn(Optional.of(joiner));

        // when
        teamService.acceptJoining(owner, applier.getId(), team.getId());

        // then
        verify(team).throwIfNotOwner(owner);
        verify(team).addMember(applier);
        verify(joinMemberJpaRepository).delete(joiner);
    }
}