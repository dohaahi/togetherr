package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.ReviewComment;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewCommentsResponseDto(
        Long id,
        Long userId,
        String content,
        int totalLikeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ReviewCommentsResponseDto of(ReviewComment comment) {
        return new ReviewCommentsResponseDto(
                comment.getId(),
                comment.getAuthor().getId(),
                comment.getContent(),
                comment.getTotalLikeCount(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getDeletedAt()
        );
    }
}
