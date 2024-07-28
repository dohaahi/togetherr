package together.together_project.team.application.dto;

import together.together_project.team.domain.Member;
import together.together_project.team.domain.Team;

public record CreateTeamRequest(
        String name,
        int memberCountLimit
) {

    public Team intoTeam(Member owner) {
        return Team.builder()
                .name(name())
                .memberCountLimit(memberCountLimit())
                .owner(owner)
                .build();
    }
}
