package together.together_project.service.dto.response;

import java.time.LocalDateTime;
import together.together_project.domain.ReviewCommentLikeLink;

public record ReviewCommentLikesResponseDto(
        Long id,
        String nickname,
        String profileUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ReviewCommentLikesResponseDto of(ReviewCommentLikeLink commentLike) {
        return new ReviewCommentLikesResponseDto(
                commentLike.getId(),
                commentLike.getUser().getNickname(),
                commentLike.getUser().getProfileUrl(),
                commentLike.getCreatedAt(),
                commentLike.getUpdatedAt(),
                commentLike.getDeletedAt()
        );
    }
}
