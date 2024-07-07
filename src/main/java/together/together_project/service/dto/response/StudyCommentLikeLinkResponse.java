package together.together_project.service.dto.response;

import java.time.LocalDateTime;
import together.together_project.domain.StudyPostCommentLikeLink;

public record StudyCommentLikeLinkResponse(
        Long id,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static StudyCommentLikeLinkResponse of(StudyPostCommentLikeLink like) {
        return new StudyCommentLikeLinkResponse(
                like.getId(),
                like.getUser().getId(),
                like.getCreatedAt(),
                like.getUpdatedAt(),
                like.getDeletedAt()
        );
    }
}
