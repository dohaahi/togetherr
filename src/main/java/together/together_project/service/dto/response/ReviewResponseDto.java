package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.ReviewPost;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewResponseDto(
        Long reviewId,
        Long studyId,
        Long userId,
        String reviewPicUrl,
        String content,
        int totalLikeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static ReviewResponseDto of(ReviewPost reviewPost) {
        return new ReviewResponseDto(
                reviewPost.getId(),
                reviewPost.getStudy().getStudyId(),
                reviewPost.getAuthor().getId(),
                reviewPost.getReviewPicUrl(),
                reviewPost.getContent(),
                reviewPost.getTotalLikeCount(),
                reviewPost.getCreatedAt(),
                reviewPost.getUpdatedAt(),
                reviewPost.getDeletedAt()
        );
    }
}
