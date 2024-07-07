package together.together_project.service.dto.response;

import java.time.LocalDateTime;
import together.together_project.domain.ReviewLikeLink;

public record ReviewLikeResponseDto(
        Long id,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ReviewLikeResponseDto of(ReviewLikeLink reviewLikeLink) {
        return new ReviewLikeResponseDto(
                reviewLikeLink.getId(),
                reviewLikeLink.getUser().getId(),
                reviewLikeLink.getCreatedAt(),
                reviewLikeLink.getUpdatedAt(),
                reviewLikeLink.getDeletedAt()
        );
    }
}
