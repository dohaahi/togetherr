package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import together.together_project.domain.StudyPostLikeLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyPostLikesResponseDto(
        Long id,
        String nickname,
        String profileUrl
) {
    public static StudyPostLikesResponseDto of(StudyPostLikeLink studyPostLikeLink) {
        return new StudyPostLikesResponseDto(
                studyPostLikeLink.getId(),
                studyPostLikeLink.getUser().getNickname(),
                studyPostLikeLink.getUser().getProfileUrl()
        );
    }
}
