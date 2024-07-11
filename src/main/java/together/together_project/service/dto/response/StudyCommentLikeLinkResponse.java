package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import together.together_project.domain.StudyPostCommentLikeLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyCommentLikeLinkResponse(
        Long id,
        Long userId,
        boolean hasLike
) {
    public static StudyCommentLikeLinkResponse of(StudyPostCommentLikeLink like, boolean hasLike) {
        return new StudyCommentLikeLinkResponse(
                like.getId(),
                like.getUser().getId(),
                hasLike
        );
    }
}
