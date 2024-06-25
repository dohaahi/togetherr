package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.ReviewPost;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewPostResponseDto(
        Long id,
        Long studyId,
        String content,
        String reviewPicUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ReviewPostResponseDto of(ReviewPost review) {
        return new ReviewPostResponseDto(
                review.getId(),
                review.getStudy().getStudyId(),
                review.getContent(),
                review.getReviewPicUrl(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getDeletedAt()
        );
    }
}
