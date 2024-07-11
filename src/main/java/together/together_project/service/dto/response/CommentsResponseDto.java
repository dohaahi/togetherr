package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.StudyPostComment;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CommentsResponseDto(
        Long id,
        Long userId,
        String content,
        int totalLikeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static CommentsResponseDto from(StudyPostComment comment) {
        return new CommentsResponseDto(
                comment.getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getProfileUrl(),
                comment.getTotalLikeCount(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getDeletedAt()
        );
    }
}
