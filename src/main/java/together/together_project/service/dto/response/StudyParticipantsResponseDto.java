package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.UserStudyLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyParticipantsResponseDto(
        Long id,
        String nickname,
        String profileUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static StudyParticipantsResponseDto from(UserStudyLink userStudyLink) {

        return new StudyParticipantsResponseDto(
                userStudyLink.getId(),
                userStudyLink.getParticipant().getNickname(),
                userStudyLink.getParticipant().getProfileUrl(),
                userStudyLink.getCreatedAt(),
                userStudyLink.getUpdatedAt(),
                userStudyLink.getDeletedAt()
        );
    }
}
