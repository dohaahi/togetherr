package together.together_project.service.dto.response;

import java.time.LocalDateTime;
import together.together_project.domain.StudyPostLikeLink;

public record StudyPostLikeResponseDto(
        Long id,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static StudyPostLikeResponseDto of(StudyPostLikeLink studyPostLikeLink) {
        return new StudyPostLikeResponseDto(
                studyPostLikeLink.getId(),
                studyPostLikeLink.getUser().getId(),
                studyPostLikeLink.getCreatedAt(),
                studyPostLikeLink.getUpdatedAt(),
                studyPostLikeLink.getDeletedAt()
        );
    }
}
