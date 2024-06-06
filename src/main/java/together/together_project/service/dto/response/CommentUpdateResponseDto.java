package together.together_project.service.dto.response;

import java.time.LocalDateTime;
import together.together_project.domain.StudyPostComment;

public record CommentUpdateResponseDto(
        Long id,
        Long userId,
        String content,
        int totalLikeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static CommentUpdateResponseDto from(StudyPostComment comment) {
        return new CommentUpdateResponseDto(
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
