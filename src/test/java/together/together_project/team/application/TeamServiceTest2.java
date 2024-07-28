package together.together_project.team.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static together.together_project.team.domain.TeamStatus.COMPLETED;
import static together.together_project.team.domain.TeamStatus.INVITING;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import together.together_project.team.application.dto.CreateMemberRequest;
import together.together_project.team.application.dto.CreateTeamRequest;
import together.together_project.team.application.dto.ModifyTeamStatusRequest;
import together.together_project.team.application.repository.JoinMemberRepository;
import together.together_project.team.application.repository.MemberRepository;
import together.together_project.team.application.repository.TeamRepository;
import together.together_project.team.domain.JoinMember;
import together.together_project.team.domain.Member;
import together.together_project.team.domain.Team;
import together.together_project.team.fakerepos.FakeJoinMemberRepository;
import together.together_project.team.fakerepos.FakeMemberRepository;
import together.together_project.team.fakerepos.FakeTeamRepository;


class TeamServiceTest2 {

    MemberRepository memberRepository = new FakeMemberRepository();
    TeamRepository teamRepository = new FakeTeamRepository();
    JoinMemberRepository joinMemberRepository = new FakeJoinMemberRepository();

    MemberService memberService = new MemberService(memberRepository);

    TeamService teamService = new TeamService(teamRepository, joinMemberRepository, memberService);

    @DisplayName("팀 만들기")
    @Test
    void createTeamTest() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("username", "a123", "aaa");
        Member member = memberService.signup(createMemberRequest);

        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);

        // when
        Team team = teamService.createTeam(member, createTeamRequest);

        // then
        assertThat(team.getName()).isEqualTo("teamA");
        assertThat(team.getMemberCountLimit()).isEqualTo(5);
        assertThat(team.getOwner().getUsername()).isEqualTo("username");
    }

    @DisplayName("중복 팀명인 경우 생성 실패")
    @Test
    void createTeamTest2() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123", "aaa");
        Member member = memberService.signup(createMemberRequest);

        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
        teamService.createTeam(member, createTeamRequest);

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(member, createTeamRequest),
                "이미 생성된 팀 이름");
    }

    @DisplayName("owner는 팀 상태 변경 가능")
    @Test
    void test3() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123", "aaa");
        Member member = memberService.signup(createMemberRequest);

        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
        Team team = teamService.createTeam(member, createTeamRequest);

        // when
        ModifyTeamStatusRequest modifyTeamStatusRequest = new ModifyTeamStatusRequest(COMPLETED);
        teamService.modifyTeamStatus(member, team.getId(), modifyTeamStatusRequest);

        // then
        assertThat(team.getStatus()).isEqualTo(COMPLETED);
        assertThat(team.getName()).isEqualTo("teamA");
        assertThat(team.getOwner().getUsername()).isEqualTo("user");
    }

    @DisplayName("owner가 아니면 팀 상태 변경 불가능")
    @Test
    void test4() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123", "aaa");
        Member memberA = memberService.signup(createMemberRequest);

        CreateMemberRequest createMemberRequest2 = new CreateMemberRequest("userB", "a123", "bbb");
        Member memberB = memberService.signup(createMemberRequest2);

        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
        Team team = teamService.createTeam(memberA, createTeamRequest);

        // when
        // then
        ModifyTeamStatusRequest modifyTeamStatusRequest = new ModifyTeamStatusRequest(COMPLETED);

        assertThatThrownBy(
                () -> teamService.modifyTeamStatus(memberB, team.getId(), modifyTeamStatusRequest)).isInstanceOf(
                IllegalArgumentException.class).hasMessage("권한 없음");
    }

    @DisplayName("가입 신청")
    @Test
    void joinTest() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
        Member member = memberService.signup(createMemberRequest);
        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
        Team team = teamService.createTeam(member, createTeamRequest);
        team.modifyStatus(member, INVITING);

        CreateMemberRequest createMemberRequest2 = new CreateMemberRequest("userB", "a123!", "bbb");
        Member memberB = memberService.signup(createMemberRequest2);

        // when
        JoinMember joinMember = teamService.joinTeam(memberB, team.getId());

        // then
        assertThat(joinMember.getTeam()).isEqualTo(team);
        assertThat(joinMember.getJoiner()).isEqualTo(memberB);
    }

    @DisplayName("존재하지 않는 팀에는 가입 불가능")
    @Test
    void joinTest2() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
        Member member = memberService.signup(createMemberRequest);

        // when
        // then
        assertThatThrownBy(() -> teamService.joinTeam(member, Long.MAX_VALUE)).isInstanceOf(
                IllegalArgumentException.class).hasMessage("없는 팀");
    }

    @DisplayName("리더는 팀에 가입 불가능")
    @Test
    void joinTest3() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
        Member member = memberService.signup(createMemberRequest);
        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
        Team team = teamService.createTeam(member, createTeamRequest);
        team.modifyStatus(member, INVITING);

        // when
        // then
        assertThatThrownBy(() -> teamService.joinTeam(member, team.getId())).isInstanceOf(
                IllegalArgumentException.class).hasMessage("가입 불가");
    }

    @DisplayName("이미 다른 팀에 참여중이면 가입 신청 불가")
    @Test
    void joinTest4() {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
        Member owner = memberService.signup(createMemberRequest);
        CreateMemberRequest createMemberRequest2 = new CreateMemberRequest("user2", "a123!", "bbb");
        Member owner2 = memberService.signup(createMemberRequest2);

        CreateMemberRequest createMemberRequest3 = new CreateMemberRequest("applier", "a123!", "bbb");
        Member applier = memberService.signup(createMemberRequest3);

        Team teamA = teamService.createTeam(owner, new CreateTeamRequest("teamA", 5));
        teamA.modifyStatus(owner, INVITING);

        Team teamB = teamService.createTeam(owner2, new CreateTeamRequest("teamB", 5));
        teamB.modifyStatus(owner2, INVITING);

        teamService.joinTeam(applier, teamA.getId());
        teamService.joinTeam(applier, teamB.getId());

        // when
        // then
        teamService.acceptJoining(owner, applier.getId(), teamA.getId());
        assertThatThrownBy(() -> teamService.acceptJoining(owner2, applier.getId(), teamB.getId())).isInstanceOf(
                IllegalArgumentException.class);

    }

    @DisplayName("Team이 Inviting 상태가 아니면 가입 신청 불가")
    @Test
    void joinTest6() {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
        Member owner = memberService.signup(createMemberRequest);

        CreateMemberRequest createMemberRequest3 = new CreateMemberRequest("applier", "a123!", "bbb");
        Member applier = memberService.signup(createMemberRequest3);

        Team team = teamService.createTeam(owner, new CreateTeamRequest("teamA", 5));

        // when
        // then
        assertThatThrownBy(() -> teamService.joinTeam(applier, team.getId())).isInstanceOf(
                IllegalArgumentException.class);

    }

    @DisplayName("Team 인원수 초과시 가입 신청 불가")
    @Test
    void joinTest7() {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
        Member owner = memberService.signup(createMemberRequest);

        CreateMemberRequest createMemberRequest2 = new CreateMemberRequest("applier", "a123!", "bbb");
        Member applier = memberService.signup(createMemberRequest2);

        Team team = teamService.createTeam(owner, new CreateTeamRequest("teamA", 1));
        team.modifyStatus(owner, INVITING);

        // when
        // then
        assertThatThrownBy(() -> teamService.joinTeam(applier, team.getId())).isInstanceOf(
                IllegalArgumentException.class);

    }

    @DisplayName("가입 수락 테스트 성공")
    @Test
    void acceptTest() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("user", "a123!", "aaa");
        Member member = memberService.signup(createMemberRequest);
        CreateTeamRequest createTeamRequest = new CreateTeamRequest("teamA", 5);
        Team team = teamService.createTeam(member, createTeamRequest);
        team.modifyStatus(member, INVITING);

        CreateMemberRequest createMemberRequest2 = new CreateMemberRequest("userB", "a123!", "bbb");
        Member memberB = memberService.signup(createMemberRequest2);

        // when
        JoinMember joinMember = teamService.joinTeam(memberB, team.getId());

        // then
        assertThat(joinMember.getTeam()).isEqualTo(team);
        assertThat(joinMember.getJoiner()).isEqualTo(memberB);
    }
}