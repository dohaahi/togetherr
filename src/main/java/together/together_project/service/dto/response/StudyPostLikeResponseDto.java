package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import together.together_project.domain.StudyPostLikeLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyPostLikeResponseDto(
        Long id,
        Long userId,
        boolean hasLike
) {
    public static StudyPostLikeResponseDto of(StudyPostLikeLink studyPostLikeLink, boolean hasLike) {
        return new StudyPostLikeResponseDto(
                studyPostLikeLink.getId(),
                studyPostLikeLink.getUser().getId(),
                hasLike
        );
    }
}
