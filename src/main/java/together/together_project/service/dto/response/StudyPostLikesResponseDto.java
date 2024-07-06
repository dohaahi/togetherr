package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.StudyPostLikeLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyPostLikesResponseDto(
        Long id,
        String nickname,
        String profileUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static StudyPostLikesResponseDto of(StudyPostLikeLink studyPostLikeLink) {
        return new StudyPostLikesResponseDto(
                studyPostLikeLink.getId(),
                studyPostLikeLink.getUser().getNickname(),
                studyPostLikeLink.getUser().getProfileUrl(),
                studyPostLikeLink.getCreatedAt(),
                studyPostLikeLink.getUpdatedAt(),
                studyPostLikeLink.getDeletedAt()
        );
    }
}
