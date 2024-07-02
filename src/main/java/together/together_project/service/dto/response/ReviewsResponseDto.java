package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.ReviewPost;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewsResponseDto(
        Long id,
        String reviewPicUrl,
        int totalLikeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ReviewsResponseDto of(ReviewPost reviewPost) {
        return new ReviewsResponseDto(
                reviewPost.getId(),
                reviewPost.getReviewPicUrl(),
                reviewPost.getTotalLikeCount(),
                reviewPost.getCreatedAt(),
                reviewPost.getUpdatedAt(),
                reviewPost.getDeletedAt()
        );
    }
}
