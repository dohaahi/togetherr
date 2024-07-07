package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.ReviewLikeLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewLikesResponseDto(
        Long id,
        String nickname,
        String profileUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ReviewLikesResponseDto of(ReviewLikeLink reviewLikeLink) {
        return new ReviewLikesResponseDto(
                reviewLikeLink.getId(),
                reviewLikeLink.getUser().getNickname(),
                reviewLikeLink.getUser().getProfileUrl(),
                reviewLikeLink.getCreatedAt(),
                reviewLikeLink.getUpdatedAt(),
                reviewLikeLink.getDeletedAt()
        );
    }
}
