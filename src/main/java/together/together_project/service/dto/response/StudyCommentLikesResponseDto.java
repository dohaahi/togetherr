package together.together_project.service.dto.response;

import java.time.LocalDateTime;
import together.together_project.domain.StudyPostCommentLikeLink;

public record StudyCommentLikesResponseDto(
        Long id,
        String nickname,
        String profileUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static StudyCommentLikesResponseDto of(StudyPostCommentLikeLink commentLike) {
        return new StudyCommentLikesResponseDto(
                commentLike.getId(),
                commentLike.getUser().getNickname(),
                commentLike.getUser().getProfileUrl(),
                commentLike.getCreatedAt(),
                commentLike.getUpdatedAt(),
                commentLike.getDeletedAt()
        );
    }
}
