package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.ReviewCommentLikeLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewCommentLikeResponseDto(
        Long id,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ReviewCommentLikeResponseDto of(ReviewCommentLikeLink commentLike) {
        return new ReviewCommentLikeResponseDto(
                commentLike.getId(),
                commentLike.getUser().getId(),
                commentLike.getCreatedAt(),
                commentLike.getUpdatedAt(),
                commentLike.getDeletedAt()
        );
    }
}
