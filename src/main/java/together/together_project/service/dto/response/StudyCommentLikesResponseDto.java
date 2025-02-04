package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import together.together_project.domain.StudyPostCommentLikeLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyCommentLikesResponseDto(
        Long id,
        String nickname,
        String profileUrl
) {

    public static StudyCommentLikesResponseDto of(StudyPostCommentLikeLink commentLike) {
        return new StudyCommentLikesResponseDto(
                commentLike.getId(),
                commentLike.getUser().getNickname(),
                commentLike.getUser().getProfileUrl()
        );
    }
}
