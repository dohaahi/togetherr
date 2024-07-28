package together.together_project.team.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import together.together_project.team.application.dto.CreateTeamRequest;
import together.together_project.team.application.dto.ModifyTeamStatusRequest;
import together.together_project.team.application.repository.JoinMemberRepository;
import together.together_project.team.application.repository.TeamRepository;
import together.together_project.team.domain.JoinMember;
import together.together_project.team.domain.Member;
import together.together_project.team.domain.Team;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    private final JoinMemberRepository joinMemberJpaRepository;

    private final MemberService memberService;

    // owner가 팀  생성
    public Team createTeam(Member owner, CreateTeamRequest request) {
        // 이름 중복 X
        boolean isDuplicateTeam = teamRepository.existsByName(request.name());
        if (isDuplicateTeam) {
            throw new IllegalArgumentException("이미 생성된 팀 이름");
        }

        return teamRepository.save(request.intoTeam(owner));
    }

    // owner가 팀 상태 변경
    public Team modifyTeamStatus(Member owner, Long teamId, ModifyTeamStatusRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("없는 팀"));

        team.modifyStatus(owner, request.teamStatus());
        return team;
    }

    // 사용자가 Team에 가입 신청
    public JoinMember joinTeam(Member member, Long teamId) {
        // 1. 가입 여부 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("없는 팀"));

        // 2. Owner 여부 확인(자기 자신 팀에 가입 불가)
        boolean canJoin = team.canJoin(member);
        if (!canJoin) {
            throw new IllegalArgumentException("가입 불가");
        }

        return joinMemberJpaRepository.save(JoinMember.builder()
                .joiner(member)
                .team(team)
                .build());
    }

    // owner가 가입 신청한 사용자 수락
    public void acceptJoining(Member owner, Long memberId, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("없는 팀"));
        team.throwIfNotOwner(owner);

        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("없는 회원"));

        JoinMember joinMember = joinMemberJpaRepository.findByJoinerAndTeam(member, team)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청 한 적 없음"));

        team.addMember(member);
        joinMemberJpaRepository.delete(joinMember);
    }
}
