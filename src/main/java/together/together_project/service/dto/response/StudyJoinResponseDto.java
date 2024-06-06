package together.together_project.service.dto.response;

import together.together_project.domain.UserStudyJoinStatus;

public record StudyJoinResponseDto(
        UserStudyJoinStatus status
) {
    public static StudyJoinResponseDto from(UserStudyJoinStatus userStudyJoinStatus) {
        return new StudyJoinResponseDto(userStudyJoinStatus);
    }
}
