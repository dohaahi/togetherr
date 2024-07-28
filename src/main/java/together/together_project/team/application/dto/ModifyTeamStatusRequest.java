package together.together_project.team.application.dto;

import together.together_project.team.domain.TeamStatus;

public record ModifyTeamStatusRequest(
        TeamStatus teamStatus
) {
}
