package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import together.together_project.domain.ReviewCommentLikeLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewCommentLikeResponseDto(
        Long id,
        Long userId,
        boolean hasLike
) {
    public static ReviewCommentLikeResponseDto of(ReviewCommentLikeLink commentLike, boolean hasLike) {
        return new ReviewCommentLikeResponseDto(
                commentLike.getId(),
                commentLike.getUser().getId(),
                hasLike
        );
    }
}
