package together.together_project.service.dto.response;

import together.together_project.domain.UserStudyJoinStatus;

public record RespondToJoinResponseDto(
        UserStudyJoinStatus status
) {
    public static RespondToJoinResponseDto from(UserStudyJoinStatus respondToJoinRequest) {
        return new RespondToJoinResponseDto(respondToJoinRequest);
    }
}
